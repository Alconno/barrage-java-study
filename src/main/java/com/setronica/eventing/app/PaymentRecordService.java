package com.setronica.eventing.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setronica.eventing.exceptions.OrderInProcess;
import com.setronica.eventing.persistence.*;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentRecordService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PaymentRecordService.class);

    private final PaymentRecordRepository paymentRecordRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private EventService eventService;
    private EventScheduleService eventScheduleService;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public PaymentRecordService(
            PaymentRecordRepository paymentRecordRepository,
            TicketOrderRepository ticketOrderRepository,
            EventScheduleService eventScheduleService,
            EventService eventService
    ) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.ticketOrderRepository = ticketOrderRepository;
        this.eventScheduleService = eventScheduleService;
        this.eventService = eventService;
    }

    public void pay(TicketOrder existingTicketOrder) {
        PaymentRecord newPaymentRecord = new PaymentRecord();

        if (existingTicketOrder.getStatus() != TicketStatus.BOOKED) {
            throw new OrderInProcess("Order currently in Process");
        }
        log.info("Sending order to provider");
        try {
            newPaymentRecord.setId(existingTicketOrder.getId());

            double total = BigDecimal.valueOf(existingTicketOrder.getAmount()).multiply(existingTicketOrder.getPrice()).doubleValue();
            newPaymentRecord.setTotal(BigDecimal.valueOf(total));

            log.info("Saving payment record to DB");
            paymentRecordRepository.save(newPaymentRecord);

            log.info("Updating status of existing order to SALE");
            existingTicketOrder.setStatus(TicketStatus.SALE);
            ticketOrderRepository.save(existingTicketOrder);

            EventSchedule eventSchedule = eventScheduleService.getById(existingTicketOrder.getEventScheduleId());
            Event event = eventService.getById(eventSchedule.getEventId());

            rabbitSend(new PaymentNotification(existingTicketOrder.getId(), existingTicketOrder.getAmount(), "AUTHORIZED"));
        }catch(Exception e){
            log.error(e.toString());
            rabbitSend(new PaymentNotification(existingTicketOrder.getId(), existingTicketOrder.getAmount(), "FAILED"));
        }
    }

    private void rabbitSend(PaymentNotification notification){
        log.info("Sending payment notification to rabbitmq");
        /// Serialize PaymentNotification object to JSON string
        String jsonNotification;
        try {
            jsonNotification = objectMapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            log.error("Error serializing PaymentNotification to JSON: {}", e.getMessage());
            return;
        }

        // Send JSON string to RabbitMQ
        rabbitTemplate.convertAndSend("payment-exchange", "payment.notification", jsonNotification);
    }
}
