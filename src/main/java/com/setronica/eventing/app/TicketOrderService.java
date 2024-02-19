package com.setronica.eventing.app;

import com.setronica.eventing.dto.TicketOrderUpdate;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.EventSchedule;
import com.setronica.eventing.persistence.TicketOrder;
import com.setronica.eventing.persistence.TicketOrderRepository;
import com.setronica.eventing.persistence.TicketStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TicketOrderService {
    private static final Logger log = LoggerFactory.getLogger(TicketOrderService.class);
    private final TicketOrderRepository ticketOrderRepository;

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
        log.info("Saving ticket order");

        try {
            ticketOrder.setEventScheduleId(existingEventSchedule.getId());
            ticketOrder.setPrice(existingEventSchedule.getPrice().multiply(BigDecimal.valueOf(ticketOrder.getAmount())));
            ticketOrder.setStatus(TicketStatus.BOOKED);
            return ticketOrderRepository.save(ticketOrder);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed to save ticket order due to data integrity violation. Reason: " + e.getMessage(), e);
        }
    }

    public TicketOrder update(TicketOrder existingTicketOrder, TicketOrderUpdate ticketOrderUpdate) {
        log.info("Updating ticket order with id {}", existingTicketOrder.getId());
        existingTicketOrder.setFirstname(ticketOrderUpdate.getFirstname());
        existingTicketOrder.setLastname(ticketOrderUpdate.getLastname());
        existingTicketOrder.setEmail(ticketOrderUpdate.getEmail());
        existingTicketOrder.setAmount(ticketOrderUpdate.getAmount());
        try {
            return ticketOrderRepository.save(existingTicketOrder);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed to save ticket order due to data integrity violation. Reason: " + e.getMessage(), e);
        }
    }

    public void delete(Integer id) {
        log.info("Deleting ticket order with id {}", id);
        ticketOrderRepository.deleteById(id);
    }
}
