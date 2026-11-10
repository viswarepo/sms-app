package com.sms.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MailgunConfig {


    private String baseUrl;


    private String apiKey;

    /**
     * Mailgun authenticates with HTTP Basic auth using the literal username
     * "api" and your private API key as the password - not a bearer token.
     */
    @Bean
    public RestClient mailgunRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBasicAuth("api", apiKey);
                    return execution.execute(request, body);
                })
                .build();
    }
}
