package org.example.bloggingapp.Utils.Exceptions;

/**
 * Exception for authentication-related errors
 * Used in login, signup, and session management
 */
public class AuthenticationException extends Exception {
    
    private final String errorCode;
    
    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "AUTH_ERROR";
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AUTH_ERROR";
    }
    
    public AuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AuthenticationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return "AuthenticationException{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
