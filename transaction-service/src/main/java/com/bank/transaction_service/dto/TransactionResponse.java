package com.bank.transaction_service.dto;

import com.bank.transaction_service.enums.TransactionStatus;
import com.bank.transaction_service.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long transactionId;

    private Long accountId;

    private TransactionType transactionType;

    private BigDecimal amount;

    private String description;

    private TransactionStatus transactionStatus;

    private LocalDateTime transactionDate;
}
