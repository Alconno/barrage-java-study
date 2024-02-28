package com.setronica.eventing.persistence;

import jakarta.persistence.Column;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntityOperation<T> {
    private String entityType;
    private Integer entityId;
    private T entity;

    public EntityOperation(String entityType, Integer entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
    }
}