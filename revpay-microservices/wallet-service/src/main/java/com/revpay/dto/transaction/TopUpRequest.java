package com.revpay.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopUpRequest {

    @NotNull
    @DecimalMin(value = "1.00", message = "Minimum top-up is 1.00")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private Long paymentMethodId;

    private String description;

    @NotBlank(message = "Transaction PIN is required")
    @Size(min = 4, max = 6, message = "PIN must be 4-6 digits")
    @Pattern(regexp = "^\\d{4,6}$", message = "PIN must contain only digits")
    private String transactionPin;
}