package com.sms.user.producer;

import com.sms.user.event.CustomerRegisteredEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertOrgCustomerRegisterEventProducer {

    public static final String ORG_CREATED_TOPIC = "org-cust-user.created";

    private final KafkaTemplate<String, CustomerRegisteredEvent> kafkaTemplate;

    public AlertOrgCustomerRegisterEventProducer(KafkaTemplate<String, CustomerRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void alertOrgCustomerCreated(CustomerRegisteredEvent event) {
        kafkaTemplate.send(ORG_CREATED_TOPIC, event);
    }
}

