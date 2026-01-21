package org.example.bloggingapp.Database.DbInterfaces;

import java.util.List;
import java.util.function.Predicate;

public interface IDbSet<T> {


    T firstOrDefault(int id);
    public boolean add(T data) ;
    public boolean update(T data);
    public boolean delete(T data);
    List<T> findAll(int id );




}
