package org.example.bloggingapp.Exceptions;

/**
 * Exception for service layer errors
 * Used when business logic operations fail
 */
public class ServiceException extends Exception {
    
    private final String errorCode;
    
    public ServiceException(String message) {
        super(message);
        this.errorCode = "SERVICE_ERROR";
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SERVICE_ERROR";
    }
    
    public ServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return "ServiceException{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
