package com.example.personas.dao.hibernate;

import com.example.personas.dao.IDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public abstract class DAOBase<T> implements IDAO<T, Long> {

    protected final SessionFactory sessionFactory;
    private final Class<T> clazz;

    protected DAOBase(SessionFactory sessionFactory, Class<T> clazz) {
        this.sessionFactory = sessionFactory;
        this.clazz = clazz;
    }

    @Override
    public void insert(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(entity);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Insert failed", e);
        }
    }

    @Override
    public boolean update(T entity, Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            T entity = session.get(clazz, id);
            session.delete(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            T entity = session.get(clazz, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + clazz.getName(), clazz).list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Find all failed", e);
        }
    }

}
