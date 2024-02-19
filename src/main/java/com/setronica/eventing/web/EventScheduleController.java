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

    @GetMapping("event_schedules")
    public List<EventSchedule> getAll() {
        log.info("Request all event schedules");
        return eventScheduleService.getAll();
    }

    @GetMapping("event_schedules/{id}")
    public EventSchedule getById(
            @PathVariable Integer id
    ) {
        log.info("Request event schedule with id {}", id);
        return eventScheduleService.getById(id);
    }

    @PostMapping("{id}/event_schedules")
    public EventSchedule create(@PathVariable Integer id, @Valid @RequestBody EventSchedule eventSchedule) {
        log.info("Request create event schedule");
        Event existingEvent = eventService.getById(id);
        return eventScheduleService.save(eventSchedule, existingEvent);
    }

    @PutMapping("event_schedules/{id}")
    public EventSchedule update(@PathVariable Integer id, @RequestBody EventScheduleUpdate eventScheduleUpdate) {
        log.info("Request update event shield with id {}", id);
        EventSchedule existingScheduleEvent = eventScheduleService.getById(id);
        return eventScheduleService.update(existingScheduleEvent, eventScheduleUpdate);
    }

    @DeleteMapping("event_schedules/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Request delete event schedule with id {}", id);
        EventSchedule existingEvent = eventScheduleService.getById(id);
        eventScheduleService.delete(existingEvent.getId());
    }
}