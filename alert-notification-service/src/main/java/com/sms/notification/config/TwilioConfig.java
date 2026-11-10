package com.sms.notification.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {


    private String accountSid;


    private String authToken;

    @PostConstruct
    public void init() {
        // Twilio's SDK is initialized statically rather than via a client
        // bean - Message.creator() etc. read these values internally.
        Twilio.init(accountSid, authToken);
    }
}
