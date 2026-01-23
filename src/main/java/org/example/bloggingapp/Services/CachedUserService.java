package org.example.bloggingapp.Services;

import org.example.bloggingapp.Database.DbInterfaces.CacheService;
import org.example.bloggingapp.Cache.InMemoryCacheService;
import org.example.bloggingapp.Cache.CacheManager;
import org.example.bloggingapp.Database.DbInterfaces.Repository;
import org.example.bloggingapp.Database.Repositories.UserRepository;
import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Utils.Exceptions.DatabaseException;
import org.example.bloggingapp.Utils.Exceptions.EntityNotFoundException;
import org.example.bloggingapp.Utils.Exceptions.ServiceException;
import org.example.bloggingapp.Utils.Exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Cached version of UserService that provides fast loading through in-memory caching
 */
public class CachedUserService implements org.example.bloggingapp.Database.DbInterfaces.IService<UserEntity> {
    
    private final Repository<UserEntity> userRepository;
    private final CacheService<Integer, UserEntity> userCache;
    private final CacheService<String, UserEntity> userByEmailCache;
    private final CacheService<String, UserEntity> userByUsernameCache;
    private final CacheService<String, List<UserEntity>> allUsersCache;
    
    public CachedUserService() {
        this.userRepository = new UserRepository();
        // Initialize caches with different configurations for different use cases
        // These caches will store real database values in memory for fast access
        this.userCache = new InMemoryCacheService<>(1000, 15 * 60 * 1000); // 1000 users, 15 minutes
        this.userByEmailCache = new InMemoryCacheService<>(500, 20 * 60 * 1000); // 500 emails, 20 minutes
        this.userByUsernameCache = new InMemoryCacheService<>(500, 20 * 60 * 1000); // 500 usernames, 20 minutes
        this.allUsersCache = new InMemoryCacheService<>(5, 5 * 60 * 1000); // 5 lists, 5 minutes
        
        // Register caches with CacheManager for centralized management
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.registerCache("users", (InMemoryCacheService<?, ?>) userCache);
        cacheManager.registerCache("userEmails", (InMemoryCacheService<?, ?>) userByEmailCache);
        cacheManager.registerCache("usernames", (InMemoryCacheService<?, ?>) userByUsernameCache);
        cacheManager.registerCache("allUsers", (InMemoryCacheService<?, ?>) allUsersCache);
        
        // Pre-populate cache with real database values
        prepopulateCacheFromDatabase();
    }
    
    @Override
    public UserEntity create(UserEntity user) throws DatabaseException, ServiceException, ValidationException {
        try {
            // Validation
            if (user == null) {
                throw new ValidationException("USER_NULL", "user", "User entity cannot be null");
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                throw new ValidationException("EMAIL_REQUIRED", "email", "Email is required");
            }
            if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
                throw new ValidationException("USERNAME_REQUIRED", "userName", "Username is required");
            }
            
            // Check if user already exists
            UserEntity existingUser = findByEmail(user.getEmail());
            if (existingUser != null) {
                throw new ServiceException("USER_EXISTS", "User with email " + user.getEmail() + " already exists");
            }
            
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDateTime.now());
            }
            
            userRepository.create(user);
            
            // Cache the newly created user
            userCache.put(user.getUserId(), user);
            userByEmailCache.put(user.getEmail(), user);
            userByUsernameCache.put(user.getUserName(), user);
            
            // Invalidate all users cache
            allUsersCache.remove("all");
            
            return user;
        } catch (ValidationException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_CREATE_ERROR", "Failed to create user", e);
        }
    }
    
    @Override
    public UserEntity findById(int id) throws DatabaseException, EntityNotFoundException, ValidationException {
        try {
            if (id <= 0) {
                throw new ValidationException("INVALID_ID", "id", "User ID must be positive");
            }
            
            // Try cache first
            Optional<UserEntity> cachedUser = userCache.get(id);
            if (cachedUser.isPresent()) {
                return cachedUser.get();
            }
            
            // Cache miss - fetch from database
            UserEntity user = userRepository.findByInteger(id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
            
            // Cache the result
            userCache.put(id, user);
            userByEmailCache.put(user.getEmail(), user);
            userByUsernameCache.put(user.getUserName(), user);
            
            return user;
        } catch (ValidationException | EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_FIND_ERROR", "Failed to find user by ID: " + id, e);
        }
    }
    
    @Override
    public UserEntity findByString(String identifier) throws DatabaseException, ValidationException {
        try {
            if (identifier == null || identifier.trim().isEmpty()) {
                throw new ValidationException("IDENTIFIER_REQUIRED", "identifier", "Identifier cannot be null or empty");
            }
            
            // Try cache first
            Optional<UserEntity> cachedUser = userByEmailCache.get(identifier);
            if (cachedUser.isPresent()) {
                return cachedUser.get();
            }
            
            // Cache miss - fetch from database
            UserEntity user = userRepository.findByString(identifier);
            
            if (user != null) {
                // Cache the result
                userCache.put(user.getUserId(), user);
                userByEmailCache.put(user.getEmail(), user);
                userByUsernameCache.put(user.getUserName(), user);
            }
            
            return user;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_FIND_STRING_ERROR", "Failed to find user by identifier: " + identifier, e);
        }
    }
    
    @Override
    public List<UserEntity> findAll() throws DatabaseException {
        try {
            // Try cache first
            Optional<List<UserEntity>> cachedUsers = allUsersCache.get("all");
            if (cachedUsers.isPresent()) {
                return cachedUsers.get();
            }
            
            // Cache miss - fetch from database
            List<UserEntity> users = userRepository.findAll();
            
            // Cache the result
            allUsersCache.put("all", users);
            
            return users;
        } catch (Exception e) {
            throw new DatabaseException("USER_FIND_ALL_ERROR", "Failed to find all users", e);
        }
    }
    
    @Override
    public UserEntity update(int id, UserEntity user) throws DatabaseException, EntityNotFoundException, ValidationException {
        try {
            // Validation
            if (user == null) {
                throw new ValidationException("USER_NULL", "user", "User entity cannot be null");
            }
            
            UserEntity existingUser = findById(id);
            if (existingUser != null) {
                user.setUserId(id);
                userRepository.updateById(id);
                
                // Update caches
                userCache.put(id, user);
                userByEmailCache.put(user.getEmail(), user);
                userByUsernameCache.put(user.getUserName(), user);
                
                // Invalidate all users cache
                allUsersCache.remove("all");
                
                return user;
            }
            return null;
        } catch (ValidationException | EntityNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_UPDATE_ERROR", "Failed to update user: " + id, e);
        }
    }
    
    @Override
    public boolean delete(int id) throws DatabaseException, EntityNotFoundException, ValidationException {
        try {
            if (id <= 0) {
                throw new ValidationException("INVALID_ID", "id", "User ID must be positive");
            }
            
            // Get user before deletion to invalidate caches
            UserEntity userToDelete = findById(id);
            userRepository.delete(id);
            
            // Remove from caches
            userCache.remove(id);
            userByEmailCache.remove(userToDelete.getEmail());
            userByUsernameCache.remove(userToDelete.getUserName());
            
            // Invalidate all users cache
            allUsersCache.remove("all");
            
            return true;
        } catch (ValidationException | EntityNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_DELETE_ERROR", "Failed to delete user: " + id, e);
        }
    }
    
    // ==================== BUSINESS METHODS ===================
    
    /**
     * Find user by email (cached version)
     */
    public UserEntity findByEmail(String email) throws DatabaseException, ValidationException {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new ValidationException("EMAIL_REQUIRED", "email", "Email is required");
            }
            
            // Try cache first
            Optional<UserEntity> cachedUser = userByEmailCache.get(email);
            if (cachedUser.isPresent()) {
                return cachedUser.get();
            }
            
            // Cache miss - fetch from database
            List<UserEntity> users = findAll();
            UserEntity user = users.stream()
                    .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                    .findFirst()
                    .orElse(null);
            
            if (user != null) {
                // Cache the result
                userCache.put(user.getUserId(), user);
                userByEmailCache.put(email, user);
                userByUsernameCache.put(user.getUserName(), user);
            }
            
            return user;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_FIND_EMAIL_ERROR", "Failed to find user by email: " + email, e);
        }
    }
    
    /**
     * Find user by username (cached version)
     */
    public UserEntity findByUsername(String username) throws DatabaseException, ValidationException {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new ValidationException("USERNAME_REQUIRED", "username", "Username is required");
            }
            
            // Try cache first
            Optional<UserEntity> cachedUser = userByUsernameCache.get(username);
            if (cachedUser.isPresent()) {
                return cachedUser.get();
            }
            
            // Cache miss - fetch from database
            List<UserEntity> users = findAll();
            UserEntity user = users.stream()
                    .filter(u -> username.equalsIgnoreCase(u.getUserName()))
                    .findFirst()
                    .orElse(null);
            
            if (user != null) {
                // Cache the result
                userCache.put(user.getUserId(), user);
                userByEmailCache.put(user.getEmail(), user);
                userByUsernameCache.put(username, user);
            }
            
            return user;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_FIND_USERNAME_ERROR", "Failed to find user by username: " + username, e);
        }
    }
    
    /**
     * Pre-populates cache with real database values for better initial performance
     */
    private void prepopulateCacheFromDatabase() {
        try {
            // Load all users from database and cache them
            List<UserEntity> allUsers = userRepository.findAll();
            if (!allUsers.isEmpty()) {
                allUsersCache.put("all", allUsers);
                
                // Cache individual users with multiple lookup keys
                for (UserEntity user : allUsers) {
                    userCache.put(user.getUserId(), user);
                    userByEmailCache.put(user.getEmail(), user);
                    userByUsernameCache.put(user.getUserName(), user);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to pre-populate cache from database: " + e.getMessage());
            // Continue without pre-population - cache will be populated on-demand
        }
    }
    
    /**
     * Clears all caches
     */
    public void clearAllCaches() {
        userCache.clear();
        userByEmailCache.clear();
        userByUsernameCache.clear();
        allUsersCache.clear();
    }
    
    /**
     * Returns cache statistics for monitoring
     */
    public String getCacheStats() {
        return String.format(
            "User Cache Stats:\n" +
            "  Individual Users: %s\n" +
            "  Users by Email: %s\n" +
            "  Users by Username: %s\n" +
            "  All Users: %s",
            userCache.getStats(),
            userByEmailCache.getStats(),
            userByUsernameCache.getStats(),
            allUsersCache.getStats()
        );
    }
    
    /**
     * Performs cleanup of expired entries
     */
    public void cleanupCaches() {
        if (userCache instanceof InMemoryCacheService) {
            ((InMemoryCacheService<Integer, UserEntity>) userCache).cleanupExpired();
        }
        if (userByEmailCache instanceof InMemoryCacheService) {
            ((InMemoryCacheService<String, UserEntity>) userByEmailCache).cleanupExpired();
        }
        if (userByUsernameCache instanceof InMemoryCacheService) {
            ((InMemoryCacheService<String, UserEntity>) userByUsernameCache).cleanupExpired();
        }
        if (allUsersCache instanceof InMemoryCacheService) {
            ((InMemoryCacheService<String, List<UserEntity>>) allUsersCache).cleanupExpired();
        }
    }
}
