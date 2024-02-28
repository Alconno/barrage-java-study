package com.setronica.eventing.web;

import com.setronica.eventing.app.EventScheduleService;
import com.setronica.eventing.app.EventService;
import com.setronica.eventing.dto.EventScheduleUpdate;
import com.setronica.eventing.persistence.Event;
import com.setronica.eventing.persistence.EventSchedule;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("event/api/v1/events")
public class EventScheduleController {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EventController.class);
    private final EventScheduleService eventScheduleService;
    private final EventService eventService;

    public EventScheduleController(EventScheduleService eventScheduleService, EventService eventService) {
        this.eventScheduleService = eventScheduleService;
        this.eventService = eventService;
    }

    @Operation(summary = "Get all event schedules")
    @GetMapping("event_schedules")
    public List<EventSchedule> getAll() {
        log.info("Request all event schedules");
        return eventScheduleService.getAll();
    }

    @Operation(summary = "Get event schedule by ID")
    @GetMapping("event_schedules/{id}")
    public EventSchedule getById(@PathVariable Integer id) {
        log.info("Request event schedule with id {}", id);
        return eventScheduleService.getById(id);
    }

    @Operation(summary = "Create event schedule")
    @PostMapping("{id}/event_schedules")
    public EventSchedule create(@PathVariable Integer id, @Valid @RequestBody EventSchedule eventSchedule) {
        log.info("Request create event schedule");
        Event existingEvent = eventService.getById(id);
        return eventScheduleService.save(eventSchedule, existingEvent);
    }

    @Operation(summary = "Update event schedule")
    @PutMapping("event_schedules/{id}")
    public EventSchedule update(@PathVariable Integer id, @RequestBody EventScheduleUpdate eventScheduleUpdate) {
        log.info("Request update event shield with id {}", id);
        EventSchedule existingScheduleEvent = eventScheduleService.getById(id);
        return eventScheduleService.update(existingScheduleEvent, eventScheduleUpdate);
    }

    @Operation(summary = "Delete event schedule")
    @DeleteMapping("event_schedules/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Request delete event schedule with id {}", id);
        eventScheduleService.delete(id);
    }
}
