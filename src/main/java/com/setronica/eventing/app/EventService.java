package com.setronica.eventing.app;

import com.setronica.eventing.dto.EventUpdate;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.Event;
import com.setronica.eventing.persistence.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAll() {
        log.info("Fetching all events from database");
        return eventRepository.findAll();
    }

    public Event getById(Integer id) {
        log.info("Fetching event with id {}", id);
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found with id=" + id));
    }

    public Event create(Event event) {
        log.info("Creating event");
        return eventRepository.save(event);
    }

    public Event update(EventUpdate eventUpdate, Event existingEvent) {
        log.info("Updating event with id {}", existingEvent.getId());
        existingEvent.setTitle(eventUpdate.getTitle());
        existingEvent.setDescription(eventUpdate.getDescription());
        existingEvent.setDate(eventUpdate.getDate());
        return eventRepository.save(existingEvent);
    }

    public void delete(int id) {
        log.info("Deleting event with id {}", id);
        eventRepository.deleteById(id);
    }
}
