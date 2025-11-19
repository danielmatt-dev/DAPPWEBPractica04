package com.example.personas.service.impl;

import com.example.personas.dao.IDAO;
import com.example.personas.models.Departamento;
import com.example.personas.service.ICrudService;
import java.util.List;
import java.util.Optional;

public class DeptoServiceImpl implements ICrudService<Departamento, Long> {

    private final IDAO<Departamento, Long> dao;

    public DeptoServiceImpl(IDAO<Departamento, Long> dao) {
        this.dao = dao;
    }

    @Override
    public List<Departamento> listar() { return dao.findAll(); }

    @Override
    public Optional<Departamento> obtener(Long id) { return dao.findById(id); }
    @Override
    public void crear(Departamento p) {
        validar(p, true);
        dao.insert(p);
    }

    @Override
    public boolean actualizar(Departamento p, Long id) {
        validar(p, false);
        return dao.update(p, id);
    }

    @Override
    public boolean eliminar(Long id) {
        return dao.deleteById(id);
    }

    private void validar(Departamento p, boolean requireId) {
        if (p == null) throw new IllegalArgumentException("Departamento requerida");
        if (requireId && p.getId() == null) throw new IllegalArgumentException("Clave requerida");
        if (p.getNombre() == null || p.getNombre().trim().isEmpty())
            throw new IllegalArgumentException("Nombre es requerido");
    }

}
