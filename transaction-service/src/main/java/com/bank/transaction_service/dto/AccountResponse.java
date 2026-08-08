package com.bank.transaction_service.dto;


import com.bank.transaction_service.enums.AccountStatus;
import com.bank.transaction_service.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    
    private Long accountId;
    
    private String accountNumber;

    private AccountType accountType;

    private BigDecimal balance;

    private String branchName;
    private AccountStatus status;
    private Long customerId;
    private LocalDateTime createdDate;
}
