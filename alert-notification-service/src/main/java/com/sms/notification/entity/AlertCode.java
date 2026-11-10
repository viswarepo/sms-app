package com.sms.notification.entity;

public enum AlertCode {
    USR_EMAIL_ALERT_001("User Created"),
    USR_EMAIL_ALERT_002("Organization Created"),
    USR_EMAIL_ALERT_003("Forgot Password Link sent"),
    USR_EMAIL_ALERT_004("Password reset success"),

    SUB_EMAIL_ALERT_001("Customer Created"),
    SUB_EMAIL_ALERT_002("Subscription Created"),
    SUB_EMAIL_ALERT_003("Subscription Cancelled"),
    SUB_EMAIL_ALERT_004("Subscription Renewed"),
    SUB_EMAIL_ALERT_005("Subscription Reactivate"),
    SUB_EMAIL_ALERT_006("Subscription Resume"),
    SUB_EMAIL_ALERT_007("Subscription Paused"),
    SUB_EMAIL_ALERT_008("Subscription Created"),
    SUB_EMAIL_ALERT_009("Payment Success"),
    SUB_EMAIL_ALERT_010("Payment Failure"),

    PLN_EMAIL_ALERT_001("Plan Created"),

    PMT_EMAIL_ALERT_001("Invoice Generated"),
    PMT_EMAIL_ALERT_002("Invoice Paid"),
    PMT_EMAIL_ALERT_003("Due date Reminder"),

    /*
    * SMS Alert code
     */

    USR_SMS_ALERT_001("User Created"),
    USR_SMS_ALERT_002("Organization Created"),
    USR_SMS_ALERT_003("Forgot Password Link sent"),
    USR_SMS_ALERT_004("Password reset success"),

    SUB_SMS_ALERT_001("Customer Created"),
    SUB_SMS_ALERT_002("Subscription Created"),
    SUB_SMS_ALERT_003("Subscription Cancelled"),
    SUB_SMS_ALERT_004("Subscription Renewed"),
    SUB_SMS_ALERT_005("Subscription Reactivate"),
    SUB_SMS_ALERT_006("Subscription Resume"),
    SUB_SMS_ALERT_007("Subscription Paused"),
    SUB_SMS_ALERT_008("Subscription Created"),
    SUB_SMS_ALERT_009("Payment Success"),
    SUB_SMS_ALERT_010("Payment Failure"),

    PLN_SMS_ALERT_001("Plan Created"),

    PMT_SMS_ALERT_001("Invoice Generated"),
    PMT_SMS_ALERT_002("Invoice Paid"),
    PMT_SMS_ALERT_003("Due date Reminder");


    private final String description;
    AlertCode(String description) { this.description = description; }
    public String getCode() { return name().replace("_", "-"); }
    public String getDescription() { return description; }
}
