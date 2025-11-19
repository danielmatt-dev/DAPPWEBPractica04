package com.example.personas.dao;

import java.util.List;
import java.util.Optional;

public interface IDAO<T, I> {

    List<T> findAll();
    Optional<T> findById(I i);
    void insert(T t);
    boolean update(T t, I i);
    boolean deleteById(I i);

}
