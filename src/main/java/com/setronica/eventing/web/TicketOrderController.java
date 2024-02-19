package com.setronica.eventing.web;

import com.setronica.eventing.app.EventScheduleService;
import com.setronica.eventing.app.TicketOrderService;
import com.setronica.eventing.dto.TicketOrderUpdate;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.EventSchedule;
import com.setronica.eventing.persistence.TicketOrder;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("event/api/v1/events/event_schedules")
public class TicketOrderController {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TicketOrderController.class);
    private final TicketOrderService ticketOrderService;
    private final EventScheduleService eventScheduleService;

    public TicketOrderController(TicketOrderService ticketOrderService, EventScheduleService eventScheduleService) {
        this.ticketOrderService = ticketOrderService;
        this.eventScheduleService = eventScheduleService;
    }

    @GetMapping("ticket_orders")
    public List<TicketOrder> getAll() {
        log.info("Request all ticket orders");
        return ticketOrderService.getAll();
    }

    @GetMapping("ticket_orders/{id}")
    public TicketOrder getById(
            @PathVariable Integer id
    ) {
        log.info("Request ticket order with id {}", id);
        return ticketOrderService.getById(id);
    }

    @PostMapping("{id}/ticket_orders")
    public ResponseEntity<?> create(@PathVariable Integer id, @Valid @RequestBody TicketOrder ticketOrder) {
        log.info("Request create ticket order");
        EventSchedule existingEventSchedule = eventScheduleService.getById(id);
        try {
            TicketOrder savedTicketOrder = ticketOrderService.save(ticketOrder, existingEventSchedule.getId());
            return ResponseEntity.ok(savedTicketOrder);
        } catch (RuntimeException e) {
            log.error("Failed to save ticket order. Reason: {}", extractExceptionMessage(e));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save ticket order. Reason: " + extractExceptionMessage(e));
        }
    }

    @PutMapping("ticket_orders/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody TicketOrderUpdate ticketOrderUpdate) {
        log.info("Request update ticket order with id {}", id);

        try {
            TicketOrder existingTicketOrder = ticketOrderService.getById(id);
            EventSchedule relatedEventSchedule = eventScheduleService.getById(existingTicketOrder.getEventScheduleId());
            TicketOrder updatedTicketOrder = ticketOrderService.update(existingTicketOrder, ticketOrderUpdate);
            return ResponseEntity.ok(updatedTicketOrder);
        } catch (NotFoundException e) {
            log.error("Failed to update ticket order with id {}. Reason: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to update ticket order with id " + id + ". Reason: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Failed to update ticket order with id {}. Reason: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update ticket order with id " + id + ". Reason: " + e.getMessage());
        }
    }



    private String extractExceptionMessage(Exception e) {
        String errorMessage = e.getMessage();
        int startIndex = errorMessage.indexOf("Reason:");
        if (startIndex != -1) {
            return errorMessage.substring(startIndex);
        } else {
            return errorMessage;
        }
    }



    @DeleteMapping("ticket_orders/{id}")
    public void delete(@PathVariable Integer id) {
        TicketOrder existingTicketOrder = ticketOrderService.getById(id);
        ticketOrderService.delete(existingTicketOrder.getId());
    }
}