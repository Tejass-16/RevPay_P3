package com.revpay.controller;

import com.revpay.common.RevPayException;
import com.revpay.entity.User;
import com.revpay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserPinController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/validate-pin")
    public ResponseEntity<PinValidationResponse> validatePin(@RequestBody PinValidationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> RevPayException.notFound("User not found"));

        // Check if PIN is locked
        if (user.getPinLockedUntil() != null && user.getPinLockedUntil().isAfter(Instant.now())) {
            return ResponseEntity.ok(new PinValidationResponse(false, 
                "PIN is temporarily locked due to multiple failed attempts. Please try again later."));
        }

        // Validate PIN
        if (!passwordEncoder.matches(request.getTransactionPin(), user.getTransactionPin())) {
            // Increment failed attempts
            int attempts = user.getPinAttempts() + 1;
            user.setPinAttempts(attempts);
            
            // Lock after 3 failed attempts for 30 minutes
            if (attempts >= 3) {
                user.setPinLockedUntil(Instant.now().plusSeconds(1800)); // 30 minutes
                userRepository.save(user);
                return ResponseEntity.ok(new PinValidationResponse(false, 
                    "PIN locked due to multiple failed attempts. Please try again in 30 minutes."));
            }
            
            userRepository.save(user);
            return ResponseEntity.ok(new PinValidationResponse(false, 
                "Invalid PIN. " + (3 - attempts) + " attempts remaining."));
        }

        // Reset failed attempts on successful validation
        user.setPinAttempts(0);
        user.setPinLockedUntil(null);
        userRepository.save(user);

        return ResponseEntity.ok(new PinValidationResponse(true, "PIN validated successfully"));
    }

    // DTOs
    public static class PinValidationRequest {
        private String email;
        private String transactionPin;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getTransactionPin() { return transactionPin; }
        public void setTransactionPin(String transactionPin) { this.transactionPin = transactionPin; }
    }

    public static class PinValidationResponse {
        private boolean valid;
        private String message;

        public PinValidationResponse(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
