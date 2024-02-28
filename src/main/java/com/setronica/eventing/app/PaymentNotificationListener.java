package com.setronica.eventing.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setronica.eventing.persistence.PaymentNotification;
import com.setronica.eventing.persistence.PaymentRecordRepository;
import com.setronica.eventing.persistence.TicketOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentNotificationListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentNotificationListener.class);

    private final TicketOrderRepository ticketOrderRepository;
    private final ObjectMapper objectMapper;

    public PaymentNotificationListener(
            TicketOrderRepository ticketOrderRepository,
            ObjectMapper objectMapper) {
        this.ticketOrderRepository = ticketOrderRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "payment-notifications")
    public void handlePaymentNotification(String jsonNotification) {
        log.info("Received payment notification: {}", jsonNotification);

        try {
            // Deserialize JSON string to PaymentNotification object
            PaymentNotification paymentNotification = objectMapper.readValue(jsonNotification, PaymentNotification.class);
            log.info("Deserialized payment notification: {}", paymentNotification);

            // Handle the payment notification as needed
            if (paymentNotification.getState().equals("AUTHORIZED")) {
                log.info("Tickets successfully purchased");

            } else if (paymentNotification.getState().equals("FAILED")) {
                log.info("Failed to purchase tickets");

                ticketOrderRepository.deleteById(paymentNotification.getOrderId());
            }

        } catch (JsonProcessingException e) {
            log.error("Error deserializing payment notification JSON: {}", e.getMessage());
        }
    }
}
