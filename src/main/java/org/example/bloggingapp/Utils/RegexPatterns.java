package org.example.bloggingapp.Utils;

import java.util.regex.Pattern;

/**
 * Utility class containing common regex patterns for validation
 */
public final class RegexPatterns {
    
    // Email pattern: standard email format
    public static final Pattern EMAIL = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Username pattern: alphanumeric with underscores, 3-20 characters
    public static final Pattern USERNAME = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );
    
    // Strong password pattern: 
    // At least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character
    public static final Pattern STRONG_PASSWORD = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    // Simple password pattern: at least 6 characters
    public static final Pattern SIMPLE_PASSWORD = Pattern.compile(
        ".{6,}"
    );
    
    // Tag name pattern: alphanumeric with spaces and hyphens, 1-50 characters
    public static final Pattern TAG_NAME = Pattern.compile(
        "^[a-zA-Z0-9\\s-]{1,50}$"
    );
    
    // Phone number pattern (international format)
    public static final Pattern PHONE_NUMBER = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );
    
    // URL pattern
    public static final Pattern URL = Pattern.compile(
        "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$"
    );
    
    // Numeric pattern for IDs
    public static final Pattern NUMERIC_ID = Pattern.compile(
        "^\\d+$"
    );
    
    // Text content pattern (allows most characters but limits length)
    public static final Pattern TEXT_CONTENT = Pattern.compile(
        "^.{1,5000}$"
    );
    
    // Prevent instantiation
    private RegexPatterns() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates if a string matches the given pattern
     * @param input the string to validate
     * @param pattern the regex pattern to match against
     * @return true if matches, false otherwise
     */
    public static boolean matches(String input, Pattern pattern) {
        return input != null && pattern.matcher(input).matches();
    }
    
    /**
     * Checks if a string is null or empty
     * @param input the string to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }
    
    /**
     * Checks if a string length is within bounds
     * @param input the string to check
     * @param minLength minimum length (inclusive)
     * @param maxLength maximum length (inclusive)
     * @return true if within bounds, false otherwise
     */
    public static boolean isLengthValid(String input, int minLength, int maxLength) {
        if (isNullOrEmpty(input)) {
            return minLength == 0;
        }
        int length = input.trim().length();
        return length >= minLength && length <= maxLength;
    }
}
