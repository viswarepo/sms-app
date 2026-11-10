package com.sms.user.exception;

public class SubscriptionServiceUnavailableException extends RuntimeException {
    public SubscriptionServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
