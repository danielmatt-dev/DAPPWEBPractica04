package com.example.personas.service;

import com.example.personas.model.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaService {
    List<Persona> listar();
    Optional<Persona> obtener(Long id);
    void crear(Persona p);
    boolean actualizar(Persona p);
    boolean eliminar(Long id);
}
