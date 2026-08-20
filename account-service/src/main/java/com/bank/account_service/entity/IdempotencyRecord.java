package com.bank.account_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_records",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "idempotencyKey")
        })
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdempotencyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private BigDecimal balanceAfterOperation;

    private LocalDateTime createdAt;
}
