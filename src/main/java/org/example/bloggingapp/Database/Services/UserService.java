package org.example.bloggingapp.Database.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.DbInterfaces.Repository;
import org.example.bloggingapp.Database.Repositories.UserRepository;
import org.example.bloggingapp.Models.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

public class UserService implements IService<UserEntity> {
    
    private final Repository<UserEntity> userRepository;
    
    public UserService(Repository<UserEntity> userRepository) {
        this.userRepository = new UserRepository();
    }
    
    @Override
    public UserEntity create(UserEntity user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        userRepository.create(user);
        return user;
    }
    
    @Override
    public UserEntity findById(int id) {
        return userRepository.findByInteger(id);
    }
    
    @Override
    public UserEntity findByString(String identifier) {
        return userRepository.findByString(identifier);
    }
    
    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public UserEntity update(int id, UserEntity user) {
        UserEntity existingUser = findById(id);
        if (existingUser != null) {
            user.setUserId(id);
            userRepository.updateById(id);
            return user;
        }
        return null;
    }
    
    @Override
    public boolean delete(int id) {
        UserEntity existingUser = findById(id);
        if (existingUser != null) {
            userRepository.delete(id);
            return true;
        }
        return false;
    }
    
    public UserEntity findByEmail(String email) {
        return userRepository.findByString(email);
    }
    
    public UserEntity findByUsername(String username) {
        return userRepository.findByString(username);
    }
}
