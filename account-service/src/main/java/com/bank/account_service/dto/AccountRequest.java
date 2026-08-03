package com.bank.account_service.dto;

import com.bank.account_service.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotNull(message = "AccountType is required")
    private AccountType accountType;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0",inclusive = true,
            message = "Balance cannot be negative")
    private BigDecimal balance;

    @NotBlank(message = "Branch Name is required")
    private String branchName;

    @NotNull(message = "Customer Id is required")
    private Long customerId;
}
