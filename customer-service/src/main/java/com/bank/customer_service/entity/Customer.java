package com.bank.customer_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false,length = 100)
    private String customerName;

    @Column(unique = true,nullable = false,length = 100)
    private String email;

    @Column(nullable = false,length = 15)
    private String phoneNumber;

    private String address;

    @Column(nullable = false)
    private LocalDateTime createdDate;
}
