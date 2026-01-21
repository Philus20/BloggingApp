package org.example.bloggingapp.Exceptions;

/**
 * Base exception for all database-related errors
 * Follows clean architecture principles for exception handling
 */
public class DatabaseException extends Exception {
    
    private final String errorCode;
    
    public DatabaseException(String message) {
        super(message);
        this.errorCode = "DB_ERROR";
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DB_ERROR";
    }
    
    public DatabaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public DatabaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return "DatabaseException{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
