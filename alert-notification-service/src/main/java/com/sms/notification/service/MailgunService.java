package com.sms.notification.service;

import com.sms.notification.exception.NotificationDeliveryException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class MailgunService {

    private static final Logger log = LoggerFactory.getLogger(MailgunService.class);

    private final RestClient mailgunRestClient;
    private final String domain;
    private final String fromAddress;

    public MailgunService(RestClient mailgunRestClient,
                           String domain,
                          String fromAddress) {
        this.mailgunRestClient = mailgunRestClient;
        this.domain = domain;
        this.fromAddress = fromAddress;
    }

    /**
     * Sends a plain-text email via Mailgun. Returns Mailgun's message id on
     * success (used later to correlate delivery-status webhooks).
     */
    public String sendEmail(String to, String subject, String body) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("from", fromAddress);
        form.add("to", to);
        form.add("subject", subject);
        form.add("text", body);

        try {
            String responseBody = mailgunRestClient.post()
                    .uri("/{domain}/messages", domain)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(String.class);

            String messageId = new JSONObject(responseBody).optString("id", null);
            log.info("Mailgun accepted email to {} (id={})", to, messageId);
            return messageId;

        } catch (Exception e) {
            log.error("Mailgun send failed for recipient {}", to, e);
            throw new NotificationDeliveryException("Mailgun send failed: " + e.getMessage(), e);
        }
    }
}
