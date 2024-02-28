package com.setronica.eventing.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setronica.eventing.dto.EventUpdate;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.EntityOperation;
import com.setronica.eventing.persistence.Event;
import com.setronica.eventing.persistence.EventRepository;
import com.setronica.eventing.persistence.EventSchedule;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
        Event savedEvent = eventRepository.save(event);

        publishEvent("create", new EntityOperation<>("Event", savedEvent.getId(), savedEvent));

        return savedEvent;
    }

    public Event update(EventUpdate eventUpdate, Event existingEvent) {
        existingEvent.setTitle(eventUpdate.getTitle());
        existingEvent.setDescription(eventUpdate.getDescription());
        existingEvent.setDate(eventUpdate.getDate());
        Event updatedEvent = eventRepository.save(existingEvent);

        publishEvent("update", new EntityOperation<>("Event", updatedEvent.getId(), updatedEvent));

        return updatedEvent;
    }

    public void delete(int Id) {
        publishEvent("delete", new EntityOperation<>("Event", Id));

        eventRepository.deleteById(Id);
    }


    private void publishEvent(String eventType, EntityOperation<Event> entityOperation) {
        log.info("Publishing event to RabbitMQ: {}", eventType);
        try {
            String jsonEntityOperation = objectMapper.writeValueAsString(entityOperation);
            rabbitTemplate.convertAndSend("entity-exchange", "entity." + eventType + ".event", jsonEntityOperation);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event to JSON: {}", e.getMessage());
        }
    }
}
