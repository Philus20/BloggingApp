package org.example.bloggingapp.Database.DbInterfaces;

import java.util.List;

public interface Repository<T> {

        public abstract void create(T obj);
        public abstract T findByInteger(int id);
        public abstract T findByString(String str);
        public abstract List<T> findAll();
        public abstract void updateById(int id);
        public abstract void delete(int id);

}
