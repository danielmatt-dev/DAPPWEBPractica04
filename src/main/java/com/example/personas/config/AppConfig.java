package com.example.personas.config;

import com.example.personas.dao.IDAO;
import com.example.personas.dao.hibernate.DeptoDAO;
import com.example.personas.dao.hibernate.EmpleadoDAO;
import com.example.personas.models.Departamento;
import com.example.personas.models.Empleado;
import com.example.personas.service.ICrudService;
import com.example.personas.service.impl.DeptoServiceImpl;
import com.example.personas.service.impl.EmpleadoServiceImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppConfig implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext ctx = sce.getServletContext();

        // 1. Inicializar Hibernate
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        // 2. Crear DAO genérico para Persona
        IDAO<Empleado, Long> personaDao = new EmpleadoDAO(sessionFactory, Empleado.class); // PersonaDao extiende DaoBase<Persona>
        IDAO<Departamento, Long> deptoDAO = new DeptoDAO(sessionFactory, Departamento.class);

        // 3. Crear Service usando el DAO
        ICrudService<Empleado, Long> empleadoService = new EmpleadoServiceImpl(personaDao);
        ICrudService<Departamento, Long> deptoService = new DeptoServiceImpl(deptoDAO);

        // 4. Inyectar Service en el contexto de la aplicación
        ctx.setAttribute("personaService", empleadoService);
        ctx.setAttribute("deptoService", deptoService);

        // Opcional: si quieres inyectar el DAO directamente
        ctx.setAttribute("personaDao", personaDao);
    }

}
