package com.setronica.eventing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Integer> {
}