package com.example.personas.service;

import java.util.List;
import java.util.Optional;

public interface ICrudService<T, I> {
    List<T> listar();
        Optional<T> obtener(I id);
    void crear(T p);
    boolean actualizar(T p, I id);
    boolean eliminar(I id);
}
