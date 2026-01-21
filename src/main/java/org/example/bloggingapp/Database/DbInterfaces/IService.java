package org.example.bloggingapp.Database.DbInterfaces;

import java.util.List;

public interface IService<T> {
    T create(T entity);
    T findById(int id);
    T findByString(String identifier);
    List<T> findAll();
    T update(int id, T entity);
    boolean delete(int id);
}
