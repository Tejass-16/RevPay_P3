package com.revpay.service;

import com.revpay.common.RevPayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class PinValidationService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public PinValidationService(RestTemplate restTemplate,
                              @Value("${user.service.url:http://localhost:8081/api}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public void validateTransactionPin(String email, String providedPin) {
        try {
            // Call user service to validate PIN
            String validationUrl = userServiceUrl + "/users/validate-pin";
            PinValidationRequest request = new PinValidationRequest(email, providedPin);
            
            PinValidationResponse response = restTemplate.postForObject(
                validationUrl, request, PinValidationResponse.class);
            
            if (!response.isValid()) {
                throw RevPayException.badRequest(response.getMessage());
            }
            
        } catch (Exception e) {
            if (e instanceof RevPayException) {
                throw e;
            }
            throw RevPayException.internalError("PIN validation service unavailable");
        }
    }

    // DTOs for PIN validation
    public static class PinValidationRequest {
        private String email;
        private String transactionPin;

        public PinValidationRequest(String email, String transactionPin) {
            this.email = email;
            this.transactionPin = transactionPin;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getTransactionPin() { return transactionPin; }
        public void setTransactionPin(String transactionPin) { this.transactionPin = transactionPin; }
    }

    public static class PinValidationResponse {
        private boolean valid;
        private String message;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
