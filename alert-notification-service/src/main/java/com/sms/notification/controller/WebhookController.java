package com.sms.notification.controller;

import com.sms.notification.service.WebhookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    /**
     * Mailgun webhook endpoint. Must receive the RAW JSON body since it
     * contains Mailgun's own embedded signature block for verification.
     */
    @PostMapping("/mailgun")
    public ResponseEntity<Void> handleMailgunWebhook(@RequestBody String rawPayload) {
        webhookService.handleMailgunWebhook(rawPayload);
        return ResponseEntity.ok().build();
    }

    /**
     * Twilio status callback endpoint. Twilio signs the exact URL + all
     * form parameters, so both must be reconstructed exactly as Twilio saw
     * them - see README for reverse-proxy/HTTPS considerations.
     */
    @PostMapping("/twilio/status")
    public ResponseEntity<Void> handleTwilioStatusCallback(
            HttpServletRequest request,
            @RequestHeader("X-Twilio-Signature") String signature) {

        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values.length > 0) {
                params.put(key, values[0]);
            }
        });

        String requestUrl = request.getRequestURL().toString();
        webhookService.handleTwilioStatusCallback(requestUrl, params, signature);
        return ResponseEntity.ok().build();
    }
}
