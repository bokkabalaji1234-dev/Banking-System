package com.bank.account_service.repository;

import com.bank.account_service.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord,Long> {

    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
}
