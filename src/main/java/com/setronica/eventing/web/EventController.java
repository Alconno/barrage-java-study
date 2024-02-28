package com.setronica.eventing.web;

import com.setronica.eventing.app.EventService;
import com.setronica.eventing.dto.EventDto;
import com.setronica.eventing.dto.EventUpdate;
import com.setronica.eventing.persistence.Event;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("event/api/v1/events")
public class EventController {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Get all events", description = "Retrieve a list of all events")
    @ApiResponse(responseCode = "200", description = "Success")
    @GetMapping()
    public List<Event> getAll(){
        log.info("Request all events");
        return eventService.getAll();
    }

    @Operation(summary = "Get event by ID", description = "Retrieve an event by its ID")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @GetMapping("/{id}")
    public Event getById(@PathVariable Integer id) {
        log.info("Request event with id {}", id);
        return eventService.getById(id);
    }

    @Operation(summary = "Create event", description = "Create a new event")
    @ApiResponse(responseCode = "200", description = "Success")
    @PostMapping("")
    public Event createEvent(@Valid @RequestBody Event event) {
        log.info("Request create new event");
        return eventService.create(event);
    }

    @Operation(summary = "Update event", description = "Update an existing event")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable int id, @RequestBody EventUpdate newEvent) {
        log.info("Request update event with id {}", id);
        Event existingEvent = eventService.getById(id);
        return eventService.update(newEvent, existingEvent);
    }

    @Operation(summary = "Delete event", description = "Delete an event by its ID")
    @ApiResponse(responseCode = "204", description = "Event deleted")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Integer id) {
        log.info("Request delete with id {}", id);
        eventService.delete(id);
    }
}
