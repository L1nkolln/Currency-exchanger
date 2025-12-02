package com.petprj.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<K,T> {

    List<T> findAll();

    Optional<T> findById(K id);

    void update (T entity);

    T create (T entity);

}
