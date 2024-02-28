package com.setronica.eventing.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.setronica.eventing.persistence.EntityOperation;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityOperationListener {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EntityOperationListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "create-queue")
    public void handleCreateEvent(String jsonObject) {
        handleEvent(jsonObject, "create");
    }

    @RabbitListener(queues = "update-queue")
    public void handleUpdateEvent(String jsonObject) {
        handleEvent(jsonObject, "update");
    }

    @RabbitListener(queues = "delete-queue")
    public void handleDeleteEvent(String jsonObject) {
        handleEvent(jsonObject, "delete");
    }

    private void handleEvent(String jsonObject, String eventType) {
        try {
            EntityOperation<?> entityOperation = objectMapper.readValue(jsonObject, EntityOperation.class);
            log.info("Received {} event for {} with ID {} and entity: {}",
                    eventType, entityOperation.getEntityType(), entityOperation.getEntityId(), objectMapper.writeValueAsString(entityOperation.getEntity()));
        } catch (Exception e) {
            log.error("Error handling {} event: {}", eventType, e.getMessage());
        }
    }

}
