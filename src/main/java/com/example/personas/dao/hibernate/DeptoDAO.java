package com.example.personas.dao.hibernate;

import com.example.personas.models.Departamento;
import org.hibernate.SessionFactory;

public class DeptoDAO extends DAOBase<Departamento> {

    public DeptoDAO(SessionFactory sessionFactory, Class<Departamento> clazz) {
        super(sessionFactory, clazz);
    }

}
