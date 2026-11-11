package com.sms.notification.service;

import com.sms.notification.exception.NotificationDeliveryException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private static final Logger log = LoggerFactory.getLogger(TwilioService.class);

    private final String fromNumber;

    public TwilioService( String fromNumber) {
        this.fromNumber = fromNumber;
    }

    /**
     * Sends an SMS via Twilio. `to` must be in E.164 format (e.g. +14155552671).
     * Returns Twilio's message SID on success.
     */
    public String sendSms(String to, String body) {
        body ="sms_order_confirmation";
        try {
            Message message = Message.creator(
                            new PhoneNumber(to),
                            new PhoneNumber(fromNumber),
                            body)
                    .create();

            log.info("Twilio accepted SMS to {} (sid={}, status={})", to, message.getSid(), message.getStatus());
            return message.getSid();

        } catch (Exception e) {
            log.error("Twilio send failed for recipient {}", to, e);
            throw new NotificationDeliveryException("Twilio send failed: " + e.getMessage(), e);
        }
    }
}
