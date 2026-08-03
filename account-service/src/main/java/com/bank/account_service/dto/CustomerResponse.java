package com.bank.account_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponse {
    private Long customerId;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdDate;
}
