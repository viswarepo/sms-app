package com.sms.notification.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Verifies Mailgun webhook signatures per
 * https://documentation.mailgun.com/en/latest/user_manual.html#webhooks
 * signature = HMAC-SHA256(signing_key, timestamp + token), hex-encoded.
 */
public final class MailgunSignatureVerifier {

    private MailgunSignatureVerifier() {
    }

    public static boolean isValid(String timestamp, String token, String signature, String signingKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal((timestamp + token).getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString().equals(signature);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Could not compute Mailgun webhook signature", e);
        }
    }
}
