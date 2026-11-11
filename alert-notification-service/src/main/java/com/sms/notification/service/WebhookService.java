package com.sms.notification.service;

import com.sms.notification.entity.NotificationStatus;
import com.sms.notification.exception.InvalidRequestException;
import com.sms.notification.util.MailgunSignatureVerifier;
import com.twilio.security.RequestValidator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final NotificationService notificationService;
    private final String mailgunSigningKey;
    private final String twilioAuthToken;

    public WebhookService(NotificationService notificationService,
                           String mailgunSigningKey,
                          String twilioAuthToken) {
        this.notificationService = notificationService;
        this.mailgunSigningKey = mailgunSigningKey;
        this.twilioAuthToken = twilioAuthToken;
    }

    /**
     * Mailgun webhooks POST a JSON body containing both the event data and its own signature block.
     */
    public void handleMailgunWebhook(String rawPayload) {
        JSONObject payload = new JSONObject(rawPayload);
        JSONObject signatureBlock = payload.optJSONObject("signature");
        if (signatureBlock == null) {
            throw new InvalidRequestException("Missing Mailgun signature block");
        }

        String timestamp = signatureBlock.optString("timestamp");
        String token = signatureBlock.optString("token");
        String signature = signatureBlock.optString("signature");

        if (!MailgunSignatureVerifier.isValid(timestamp, token, signature, mailgunSigningKey)) {
            throw new InvalidRequestException("Invalid Mailgun webhook signature");
        }

        JSONObject eventData = payload.optJSONObject("event-data");
        if (eventData == null) {
            log.warn("Mailgun webhook had a valid signature but no event-data");
            return;
        }

        String event = eventData.optString("event");

        // Mailgun nests the message id under message.headers.message-id specifically.
        String providerMessageId = null;
        JSONObject messageObj = eventData.optJSONObject("message");
        if (messageObj != null) {
            JSONObject headers = messageObj.optJSONObject("headers");
            if (headers != null) {
                providerMessageId = headers.optString("message-id", null);
            }
        }

        log.info("Mailgun webhook event={} messageId={}", event, providerMessageId);

        if (providerMessageId == null) {
            return;
        }
        String organizationId = "";
        switch (event) {
            case "delivered" -> notificationService.updateStatusByProviderMessageId(
                    providerMessageId, NotificationStatus.DELIVERED, null, organizationId);
            case "failed" -> {
                String description = "Delivery failed";
                JSONObject deliveryStatus = eventData.optJSONObject("delivery-status");
                if (deliveryStatus != null) {
                    description = deliveryStatus.optString("description", description);
                }
                notificationService.updateStatusByProviderMessageId(
                        providerMessageId, NotificationStatus.FAILED, description, organizationId);
            }
            case "bounced" -> notificationService.updateStatusByProviderMessageId(
                    providerMessageId, NotificationStatus.BOUNCED, "Bounced", organizationId);
            default -> log.debug("Ignoring Mailgun event type: {}", event);
        }
    }

    /**
     * Twilio status callbacks are form-encoded POSTs, validated against the
     * exact URL Twilio was configured to call plus all POST parameters.
     */
    public void handleTwilioStatusCallback(String requestUrl, Map<String, String> params, String signature) {
        RequestValidator validator = new RequestValidator(twilioAuthToken);
        boolean isValid = validator.validate(requestUrl, params, signature);

        if (!isValid) {
            throw new InvalidRequestException("Invalid Twilio webhook signature");
        }

        String messageSid = params.get("MessageSid");
        String messageStatus = params.get("MessageStatus");
        log.info("Twilio status callback sid={} status={}", messageSid, messageStatus);

        if (messageSid == null || messageStatus == null) {
            return;
        }
        String organizationId = "";
        switch (messageStatus) {
            case "delivered" -> notificationService.updateStatusByProviderMessageId(
                    messageSid, NotificationStatus.DELIVERED, null, organizationId);
            case "failed", "undelivered" -> notificationService.updateStatusByProviderMessageId(
                    messageSid, NotificationStatus.FAILED, "Twilio reported status: " + messageStatus, organizationId);
            default -> log.debug("Ignoring Twilio status: {}", messageStatus);
        }
    }
}
