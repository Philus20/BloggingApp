package org.example.bloggingapp.Database.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.DbInterfaces.Repository;
import org.example.bloggingapp.Database.Repositories.UserRepository;
import org.example.bloggingapp.Models.UserEntity;
import org.example.bloggingapp.Exceptions.DatabaseException;
import org.example.bloggingapp.Exceptions.EntityNotFoundException;
import org.example.bloggingapp.Exceptions.ServiceException;
import org.example.bloggingapp.Exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class UserService implements IService<UserEntity> {
    
    private final Repository<UserEntity> userRepository;
    
    public UserService(Repository<UserEntity> userRepository) {
        this.userRepository = new UserRepository();
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
            return user;
        } catch (ValidationException | ServiceException e) {
            throw e; // Re-throw our custom exceptions
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
            
            UserEntity user = userRepository.findByInteger(id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
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
            return userRepository.findByString(identifier);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_FIND_STRING_ERROR", "Failed to find user by identifier: " + identifier, e);
        }
    }
    
    @Override
    public List<UserEntity> findAll() throws DatabaseException {
        try {
            return userRepository.findAll();
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
            
            UserEntity existingUser = findById(id);
            if (existingUser != null) {
                userRepository.delete(id);
                return true;
            }
            return false;
        } catch (ValidationException | EntityNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_DELETE_ERROR", "Failed to delete user: " + id, e);
        }
    }
    
    // ==================== BUSINESS METHODS ===================
    
    /**
     * Find user by email
     */
    public UserEntity findByEmail(String email) throws DatabaseException, ValidationException {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new ValidationException("EMAIL_REQUIRED", "email", "Email is required");
            }
            
            List<UserEntity> users = findAll();
            System.out.println("Searching for email: " + email);
            System.out.println(users);


            return users.stream()
                    .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                    .findFirst()
                    .orElse(null);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_FIND_EMAIL_ERROR", "Failed to find user by email: " + email, e);
        }
    }
    
    /**
     * Find user by username
     */
    public UserEntity findByUsername(String username) throws DatabaseException, ValidationException {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new ValidationException("USERNAME_REQUIRED", "username", "Username is required");
            }
            
            List<UserEntity> users = findAll();
            return users.stream()
                    .filter(user -> username.equalsIgnoreCase(user.getUserName()))
                    .findFirst()
                    .orElse(null);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("USER_FIND_USERNAME_ERROR", "Failed to find user by username: " + username, e);
        }
    }
}
