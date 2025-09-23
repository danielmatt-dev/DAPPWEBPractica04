package com.example.personas.service;

import com.example.personas.dao.PersonaDAO;
import com.example.personas.model.Persona;

import java.util.List;
import java.util.Optional;

public class PersonaServiceImpl implements PersonaService {

    private final PersonaDAO dao;

    public PersonaServiceImpl(PersonaDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Persona> listar() { return dao.findAll(); }

    @Override
    public Optional<Persona> obtener(Long id) { return dao.findById(id); }

    @Override
    public void crear(Persona p) {
        validar(p, true);
        dao.insert(p);
    }

    @Override
    public boolean actualizar(Persona p) {
        validar(p, false);
        return dao.update(p);
    }

    @Override
    public boolean eliminar(Long id) { return dao.deleteById(id); }

    private void validar(Persona p, boolean requireId) {
        if (p == null) throw new IllegalArgumentException("Persona requerida");
        if (requireId && p.getId() == null) throw new IllegalArgumentException("Clave requerida");
        if (isBlank(p.getNombre()) || isBlank(p.getDireccion()) || isBlank(p.getTelefono()))
            throw new IllegalArgumentException("Nombre, Dirección y Teléfono son requeridos");
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}