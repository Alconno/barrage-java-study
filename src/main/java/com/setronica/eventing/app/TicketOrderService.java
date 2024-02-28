package com.setronica.eventing.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setronica.eventing.dto.TicketOrderUpdate;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.*;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TicketOrderService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TicketOrderService.class);
    private final TicketOrderRepository ticketOrderRepository;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public TicketOrderService(TicketOrderRepository ticketOrderRepository) {
        this.ticketOrderRepository = ticketOrderRepository;
    }

    public List<TicketOrder> getAll() {
        log.info("Fetching all ticket orders from database");
        return ticketOrderRepository.findAll();
    }

    public TicketOrder getById(Integer id) {
        log.info("Fetching ticket order with id {}", id);
        return ticketOrderRepository.findById(id).orElseThrow(() -> new NotFoundException("Ticket order not found with id=" + id));
    }

    public TicketOrder save(TicketOrder ticketOrder, EventSchedule existingEventSchedule) {
        try {
            ticketOrder.setEventScheduleId(existingEventSchedule.getId());
            ticketOrder.setPrice(existingEventSchedule.getPrice().multiply(BigDecimal.valueOf(ticketOrder.getAmount())));
            ticketOrder.setStatus(TicketStatus.BOOKED);
            TicketOrder savedTicketOrder = ticketOrderRepository.save(ticketOrder);

            publishEvent("create", new EntityOperation<>("TicketOrder", savedTicketOrder.getId(), savedTicketOrder));

            return savedTicketOrder;
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed to save ticket order due to data integrity violation. Reason: " + e.getMessage(), e);
        }
    }

    public TicketOrder update(TicketOrder existingTicketOrder, TicketOrderUpdate ticketOrderUpdate) {
        existingTicketOrder.setFirstname(ticketOrderUpdate.getFirstname());
        existingTicketOrder.setLastname(ticketOrderUpdate.getLastname());
        existingTicketOrder.setEmail(ticketOrderUpdate.getEmail());
        existingTicketOrder.setAmount(ticketOrderUpdate.getAmount());
        try {
            TicketOrder updatedTicketOrder = ticketOrderRepository.save(existingTicketOrder);

            publishEvent("update", new EntityOperation<>("TicketOrder", updatedTicketOrder.getId(), updatedTicketOrder));

            return updatedTicketOrder;
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed to save ticket order due to data integrity violation. Reason: " + e.getMessage(), e);
        }
    }

    public void delete(Integer Id) {
        ticketOrderRepository.deleteById(Id);

        publishEvent("delete",  new EntityOperation<>("TicketOrder", Id));
    }

    private void publishEvent(String eventType, EntityOperation<TicketOrder> entityOperation) {
        log.info("Publishing event to RabbitMQ: {}", eventType);
        try {
            String jsonEntityOperation = objectMapper.writeValueAsString(entityOperation);
            rabbitTemplate.convertAndSend("entity-exchange", "entity." + eventType + ".event", jsonEntityOperation);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event to JSON: {}", e.getMessage());
        }
    }
}
