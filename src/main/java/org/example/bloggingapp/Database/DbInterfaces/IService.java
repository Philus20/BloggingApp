package org.example.bloggingapp.Database.DbInterfaces;

import org.example.bloggingapp.Utils.Exceptions.DatabaseException;
import org.example.bloggingapp.Utils.Exceptions.EntityNotFoundException;
import org.example.bloggingapp.Utils.Exceptions.ServiceException;
import org.example.bloggingapp.Utils.Exceptions.ValidationException;

import java.util.List;

public interface IService<T> {
    T create(T entity) throws DatabaseException, ServiceException, ValidationException;
    T findById(int id) throws DatabaseException, EntityNotFoundException, ValidationException;
    T findByString(String identifier) throws DatabaseException, ValidationException;
    List<T> findAll() throws DatabaseException;
    T update(int id, T entity) throws DatabaseException, EntityNotFoundException, ValidationException;
    boolean delete(int id) throws DatabaseException, EntityNotFoundException, ValidationException;
}
