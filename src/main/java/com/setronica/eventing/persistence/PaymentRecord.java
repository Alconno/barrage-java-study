package com.setronica.eventing.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "payment_records")
public class PaymentRecord {
    @Id
    private int id;
    @Column(nullable = false)
    private BigDecimal total;
}