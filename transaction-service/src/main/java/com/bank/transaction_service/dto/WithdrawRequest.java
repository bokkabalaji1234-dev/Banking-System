package com.bank.transaction_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawRequest {

    @NotNull(message = "account id is required")
    private Long accountId;

    @NotNull(message = "amount is required")
    @Positive
    private BigDecimal amount;

    private String description;
}
