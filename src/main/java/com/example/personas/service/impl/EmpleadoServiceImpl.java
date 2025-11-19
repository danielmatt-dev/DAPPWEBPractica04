package com.example.personas.service.impl;

import com.example.personas.dao.IDAO;
import com.example.personas.models.Departamento;
import com.example.personas.models.Empleado;
import com.example.personas.service.ICrudService;

import java.util.List;
import java.util.Optional;

public class EmpleadoServiceImpl implements ICrudService<Empleado, Long> {

    private final IDAO<Empleado, Long> dao;

    public EmpleadoServiceImpl(IDAO<Empleado, Long> dao) {
        this.dao = dao;
    }

    @Override
    public List<Empleado> listar() { return dao.findAll(); }

    @Override
    public Optional<Empleado> obtener(Long id) { return dao.findById(id); }

    @Override
    public void crear(Empleado p) {
        validar(p, true);
        dao.insert(p);
    }

    @Override
    public boolean actualizar(Empleado p, Long id) {
        validar(p, false);
        return dao.update(p, id);
    }

    @Override
    public boolean eliminar(Long id) { return dao.deleteById(id); }

    private void validar(Empleado p, boolean requireId) {
        if (p == null) throw new IllegalArgumentException("Persona requerida");
        if (requireId && p.getId() == null) throw new IllegalArgumentException("Clave requerida");
        if (isBlank(p.getNombre()) || isBlank(p.getDireccion()) || isBlank(p.getTelefono()))
            throw new IllegalArgumentException("Nombre, Dirección y Teléfono son requeridos");
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}