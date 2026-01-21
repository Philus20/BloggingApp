package org.example.bloggingapp.Exceptions;

/**
 * Exception for when an entity is not found in the database
 * Used for CRUD operations when entities don't exist
 */
public class EntityNotFoundException extends Exception {
    
    private final String errorCode;
    private final String entityType;
    private final Object entityId;
    
    public EntityNotFoundException(String entityType, Object entityId) {
        super(entityType + " with ID " + entityId + " not found");
        this.errorCode = "ENTITY_NOT_FOUND";
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public EntityNotFoundException(String entityType, Object entityId, Throwable cause) {
        super(entityType + " with ID " + entityId + " not found", cause);
        this.errorCode = "ENTITY_NOT_FOUND";
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public EntityNotFoundException(String errorCode, String entityType, Object entityId, String message) {
        super(message);
        this.errorCode = errorCode;
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public Object getEntityId() {
        return entityId;
    }
    
    @Override
    public String toString() {
        return "EntityNotFoundException{" +
                "errorCode='" + errorCode + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
