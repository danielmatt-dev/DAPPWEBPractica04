package com.example.personas.dao.hibernate;

import com.example.personas.models.Empleado;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class EmpleadoDAO extends DAOBase<Empleado> {

    public EmpleadoDAO(SessionFactory sessionFactory, Class<Empleado> clazz) {
        super(sessionFactory, clazz);
    }

    @Override
    public List<Empleado> findAll() {
        try (Session session = this.sessionFactory.openSession()) {
            List<Empleado> results = session.createQuery("FROM " + Empleado.class.getName(), Empleado.class).list();
            results.forEach(e -> Hibernate.initialize((e).getDepto()));
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Find all failed", e);
        }
    }
}
