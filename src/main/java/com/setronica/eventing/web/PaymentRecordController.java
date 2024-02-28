package com.setronica.eventing.web;

import com.setronica.eventing.app.PaymentRecordService;
import com.setronica.eventing.app.TicketOrderService;
import com.setronica.eventing.persistence.TicketOrder;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

    @Operation(summary = "Pay for a ticket order")
    @ApiResponse(responseCode = "200", description = "Payment processed successfully")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @PostMapping("{id}/pay")
    public void pay(@PathVariable Integer id) {
        log.info("Request to pay ticket order");
        TicketOrder existingTicketOrder = ticketOrderService.getById(id);
        paymentRecordService.pay(existingTicketOrder);
    }
}
