package com.example.personas.config;

import com.example.personas.dao.PersonaDAO;
import com.example.personas.dao.jdbc.PersonaDaoJdbc;
import com.example.personas.db.DataSourceFactory;
import com.example.personas.service.PersonaService;
import com.example.personas.service.PersonaServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

public class AppConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        String url  = get(ctx, "DB_URL",  "jdbc:postgresql://127.0.0.1:5432/personas");
        String user = get(ctx, "DB_USER", "postgres");
        String pass = get(ctx, "DB_PASS", "armand99");

        DataSource ds = DataSourceFactory.create(url, user, pass);
        PersonaDAO personaDao = new PersonaDaoJdbc(ds);
        PersonaService personaService = new PersonaServiceImpl(personaDao);

        // Inyección “manual” en el contexto
        ctx.setAttribute("personaService", personaService);
    }

    private static String get(ServletContext ctx, String key, String def) {
        String v = ctx.getInitParameter(key);
        return (v == null || v.isEmpty()) ? def : v;
    }

}
