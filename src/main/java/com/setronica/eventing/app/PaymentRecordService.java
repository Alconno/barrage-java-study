package com.setronica.eventing.app;

import com.setronica.eventing.exceptions.OrderInProcess;
import com.setronica.eventing.persistence.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentRecordService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PaymentRecordService.class);

    private final PaymentRecordRepository paymentRecordRepository;
    private final TicketOrderRepository ticketOrderRepository;

    public PaymentRecordService(PaymentRecordRepository paymentRecordRepository, TicketOrderRepository ticketOrderRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.ticketOrderRepository = ticketOrderRepository;
    }

    public void pay(TicketOrder existingTicketOrder) {
        if (existingTicketOrder.getStatus() != TicketStatus.BOOKED) {
            throw new OrderInProcess("Order currently in Process");
        }
        log.info("Sending order to provider");

        PaymentRecord newPaymentRecord = new PaymentRecord();
        newPaymentRecord.setId(existingTicketOrder.getId());

        double total = BigDecimal.valueOf(existingTicketOrder.getAmount()).multiply(existingTicketOrder.getPrice()).doubleValue();
        newPaymentRecord.setTotal(BigDecimal.valueOf(total));

        log.info("Saving payment record to DB");
        paymentRecordRepository.save(newPaymentRecord);

        log.info("Updating status of existing order to SALE");
        existingTicketOrder.setStatus(TicketStatus.SALE);
        ticketOrderRepository.save(existingTicketOrder);
    }
}