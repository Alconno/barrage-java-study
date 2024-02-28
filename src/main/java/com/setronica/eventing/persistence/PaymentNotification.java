package com.setronica.eventing.persistence;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PaymentNotification {
    @Column(nullable = false)
    private Integer orderId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String state;

}