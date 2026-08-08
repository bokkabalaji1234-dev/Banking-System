package com.bank.account_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositBalanceRequest {

    @NotNull(message = "amounr is required")
    @Positive
    private BigDecimal amount;
}
