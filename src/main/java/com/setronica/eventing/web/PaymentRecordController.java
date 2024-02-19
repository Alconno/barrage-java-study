package com.setronica.eventing.web;

import com.setronica.eventing.app.PaymentRecordService;
import com.setronica.eventing.app.TicketOrderService;
import com.setronica.eventing.persistence.TicketOrder;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("event/api/v1/events/event_schedules/ticket_orders")
public class PaymentRecordController {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PaymentRecordController.class);
    private final PaymentRecordService paymentRecordService;
    private final TicketOrderService ticketOrderService;

    public PaymentRecordController(TicketOrderService ticketOrderService, PaymentRecordService paymentRecordService) {
        this.ticketOrderService = ticketOrderService;
        this.paymentRecordService = paymentRecordService;
    }

    @PostMapping("{id}/pay")
    public void pay(@PathVariable Integer id) {
        log.info("Request to pay ticket order");
        TicketOrder existingTicketOrder = ticketOrderService.getById(id);
        paymentRecordService.pay(existingTicketOrder);
    }
}