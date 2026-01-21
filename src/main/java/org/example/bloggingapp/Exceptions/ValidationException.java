package org.example.bloggingapp.Exceptions;

/**
 * Exception for validation-related errors
 * Used for input validation, format checking, and business rule validation
 */
public class ValidationException extends Exception {
    
    private final String errorCode;
    private final String fieldName;
    
    public ValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
        this.fieldName = null;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VALIDATION_ERROR";
        this.fieldName = null;
    }
    
    public ValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = null;
    }
    
    public ValidationException(String errorCode, String fieldName, String message) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }
    
    public ValidationException(String errorCode, String fieldName, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public String toString() {
        return "ValidationException{" +
                "errorCode='" + errorCode + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
