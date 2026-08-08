package com.bank.transaction_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawBalanceRequest {
    @NotNull(message = "amount is required")
    @Positive
    private BigDecimal amount;

}
