package com.bank.transaction_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {

    @NotNull(message = "From account Id is required")
    private Long fromAccountId;

    @NotNull(message = "To account Id is required")
    private Long toAccountId;

    @NotNull(message = "amount is required")
    @Positive
    private BigDecimal amount;

    private String description;
}
