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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

    @Operation(summary = "Get all ticket orders")
    @GetMapping("ticket_orders")
    public List<TicketOrder> getAll() {
        log.info("Request all ticket orders");
        return ticketOrderService.getAll();
    }

    @Operation(summary = "Get ticket order by ID")
    @GetMapping("ticket_orders/{id}")
    public TicketOrder getById(
            @PathVariable Integer id
    ) {
        log.info("Request ticket order with id {}", id);
        return ticketOrderService.getById(id);
    }

    @Operation(summary = "Create a new ticket order")
    @ApiResponse(responseCode = "200", description = "Ticket order created successfully")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @PostMapping("{id}/ticket_orders")
    public ResponseEntity<?> create(@PathVariable Integer id, @Valid @RequestBody TicketOrder ticketOrder) {
        log.info("Request create ticket order");
        EventSchedule existingEventSchedule = eventScheduleService.getById(id);
        try {
            TicketOrder savedTicketOrder = ticketOrderService.save(ticketOrder, existingEventSchedule);
            return ResponseEntity.ok(savedTicketOrder);
        } catch (RuntimeException e) {
            log.error("Failed to save ticket order. Reason: {}", extractExceptionMessage(e));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save ticket order. Reason: " + extractExceptionMessage(e));
        }
    }

    @Operation(summary = "Update a ticket order")
    @ApiResponse(responseCode = "200", description = "Ticket order updated successfully")
    @ApiResponse(responseCode = "404", description = "Ticket order not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
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

    @Operation(summary = "Delete a ticket order")
    @ApiResponse(responseCode = "200", description = "Ticket order deleted successfully")
    @ApiResponse(responseCode = "404", description = "Ticket order not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @DeleteMapping("ticket_orders/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            ticketOrderService.delete(id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            log.error("Failed to delete ticket order with id {}. Reason: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete ticket order with id {}. Reason: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
}
