package com.setronica.eventing.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.setronica.eventing.dto.EventScheduleUpdate;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.*;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class EventScheduleService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EventScheduleService.class);
    private final EventScheduleRepository eventScheduleRepository;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
        eventSchedule.setEventDate(existingEvent.getDate());
        eventSchedule.setEventId(existingEvent.getId());
        EventSchedule savedEventSchedule = eventScheduleRepository.save(eventSchedule);

        publishEvent("create", new EntityOperation<>("EventSchedule", savedEventSchedule.getId(), savedEventSchedule));

        return savedEventSchedule;
    }

    public EventSchedule update(EventSchedule existingEventSchedule, EventScheduleUpdate eventScheduleUpdate) {
        existingEventSchedule.setPrice(eventScheduleUpdate.getPrice());
        existingEventSchedule.setAvailableSeats(eventScheduleUpdate.getAvailableSeats());
        EventSchedule updatedEventSchedule = eventScheduleRepository.save(existingEventSchedule);

        publishEvent("update", new EntityOperation<>("EventSchedule", updatedEventSchedule.getId(), updatedEventSchedule));

        return updatedEventSchedule;
    }

    public void delete(Integer Id) {
        publishEvent("delete", new EntityOperation<>("EventSchedule", Id));

        eventScheduleRepository.deleteById(Id);
    }

    private void publishEvent(String eventType, EntityOperation<EventSchedule> entityOperation) {
        log.info("Publishing event to RabbitMQ: {}", eventType);
        try {
            String jsonEntityOperation = objectMapper.writeValueAsString(entityOperation);
            rabbitTemplate.convertAndSend("entity-exchange", "entity." + eventType + ".event", jsonEntityOperation);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event to JSON: {}", e.getMessage());
        }
    }

}