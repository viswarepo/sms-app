package com.sms.user.client;

import com.sms.user.dto.CreateCustomerRequest;
import com.sms.user.exception.SubscriptionServiceUnavailableException;
import com.sms.user.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class SubscriptionServiceClient {

    private final RestTemplate restTemplate;

    @Value("${subscription-service.url}")
    private String subscriptionServiceBaseUrl;

    @Value("${internal.api-key}")
    private String internalApiKey;

    public SubscriptionServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createCustomer(CreateCustomerRequest request, String userRole) {
        String url = subscriptionServiceBaseUrl + "/api/v1/customers/create/"+userRole;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Organization-Id",request.getOrganizationId());
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.postForEntity(url, new HttpEntity<>(request, headers), Object.class);
        } catch (HttpClientErrorException.Conflict e) {
            throw new UserAlreadyExistsException(
                    "Username or email already registered: " + request.getUsername());
        } catch (HttpServerErrorException | ResourceAccessException e) {
            log.error("subscription-service unreachable while registering customer as a user", e);
            throw new SubscriptionServiceUnavailableException("subscription-service is currently unavailable", e);
        }
    }
}
