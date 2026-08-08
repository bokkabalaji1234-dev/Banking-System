package com.bank.transaction_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime localDateTime;
    private int status;
    private String error;
    private String message;
    private String path;
}
