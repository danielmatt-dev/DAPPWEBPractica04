package com.example.personas.dao;

import com.example.personas.model.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaDAO {
    List<Persona> findAll();
    Optional<Persona> findById(Long id);
    void insert(Persona p);
    boolean update(Persona p);
    boolean deleteById(Long id);
}
