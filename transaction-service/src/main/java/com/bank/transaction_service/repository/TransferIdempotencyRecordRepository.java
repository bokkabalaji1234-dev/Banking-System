package com.bank.transaction_service.repository;

import com.bank.transaction_service.entity.TransferIdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferIdempotencyRecordRepository
        extends JpaRepository<TransferIdempotencyRecord, Long> {

    Optional<TransferIdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
}
