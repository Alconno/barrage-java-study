package com.setronica.eventing.app;

import com.setronica.eventing.dto.EventScheduleUpdate;
import com.setronica.eventing.exceptions.EventScheduleAlreadyExists;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.Event;
import com.setronica.eventing.persistence.EventSchedule;
import com.setronica.eventing.persistence.EventScheduleRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventScheduleService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EventScheduleService.class);
    private final EventScheduleRepository eventScheduleRepository;

    public EventScheduleService(EventScheduleRepository eventRepository) {
        this.eventScheduleRepository = eventRepository;
    }

    public List<EventSchedule> getAll() {
        log.info("Fetching all event schedules from database");
        return eventScheduleRepository.findAll();
    }

    public EventSchedule getById(Integer id) {
        log.info("Fetching event schedule with id {}", id);
        return eventScheduleRepository.findById(id).orElseThrow(() -> new NotFoundException("Event schedule not found with id=" + id));
    }

    public EventSchedule save(EventSchedule eventSchedule, Event existingEvent) {
        log.info("Creating event schedule");
        List<EventSchedule> existingEventSchedules = eventScheduleRepository.findByEventId(existingEvent.getId());
        if (existingEventSchedules.isEmpty()) {
            eventSchedule.setEventId(existingEvent.getId());
            eventSchedule.setEventDate(existingEvent.getDate());
            return eventScheduleRepository.save(eventSchedule);
        }
        throw new EventScheduleAlreadyExists("Event schedule for this event already exists");
    }

    public EventSchedule update(EventSchedule existingEventSchedule, EventScheduleUpdate eventScheduleUpdate) {
        log.info("Updating event schedule with id {}", existingEventSchedule.getId());
        existingEventSchedule.setPrice(eventScheduleUpdate.getPrice());
        existingEventSchedule.setAvailableSeats(eventScheduleUpdate.getAvailableSeats());
        return eventScheduleRepository.save(existingEventSchedule);
    }

    public void delete(Integer id) {
        log.info("Deleting event schedule with id {}", id);
        eventScheduleRepository.deleteById(id);
    }

}