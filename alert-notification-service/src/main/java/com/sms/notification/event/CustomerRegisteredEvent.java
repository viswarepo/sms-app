package com.sms.notification.event;

public record CustomerRegisteredEvent(
        String customerId,
        String organizationId,
        String externalId,
        String email,
        String name,
        String mobile
) {
    public static CustomerRegisteredEvent of(
            String customerId,
            String organizationId,
            String externalId,
            String email,
            String name,
            String mobile) {
        return new CustomerRegisteredEvent(
                customerId,
                organizationId,
                externalId,
                email,
                name,
                mobile);
    }
}
