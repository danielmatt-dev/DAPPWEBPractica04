package com.example.personas.web;

import com.example.personas.model.Persona;
import com.example.personas.service.PersonaService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

public class PersonasController extends HttpServlet {

    private PersonaService service;

    @Override
    public void init() {
        this.service = (PersonaService) getServletContext().getAttribute("personaService");
        if (this.service == null) throw new IllegalStateException("personaService no inyectado");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action", "list");

        try {
            switch (action) {
                case "edit": {
                    Long id = Long.valueOf(param(req, "clave"));
                    Optional<Persona> p = service.obtener(id);
                    if (p.isPresent()) {
                        req.setAttribute("persona", p.get());
                        forward(req, resp);
                        return;
                    } else {
                        req.setAttribute("status", "No encontrado");
                        forward(req, resp);
                        return;
                    }
                }
                case "new": {
                    // sólo muestra formulario vacío
                    forward(req, resp);
                    return;
                }
                case "list":
                default: {
                    forward(req, resp);
                }
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");
        String action = param(req, "action");

        try {
            if ("create".equals(action)) {
                Persona p = bind(req, true);
                service.crear(p);
                req.setAttribute("status", "Creado");
            } else if ("update".equals(action)) {
                Persona p = bind(req, false);
                boolean ok = service.actualizar(p);
                req.setAttribute("status", ok ? "Actualizado" : "Clave no encontrada");
            } else if ("delete".equals(action)) {
                Long id = Long.valueOf(param(req, "clave"));
                boolean ok = service.eliminar(id);
                req.setAttribute("status", ok ? "Eliminado" : "No encontrado");
            } else {
                req.setAttribute("status", "Acción inválida");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        // Siempre forward a la vista principal (PRG opcional)
        forward(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Cargamos la lista cada vez (simple y efectivo)
        req.setAttribute("personas", service.listar());
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/personas.jsp");
        rd.forward(req, resp);
    }

    private static Persona bind(HttpServletRequest req, boolean requireId) {
        String sId = req.getParameter("clave");
        Long id = (sId == null || sId.isEmpty()) ? null : Long.valueOf(sId);
        if (requireId && id == null) throw new IllegalArgumentException("Clave requerida");

        Persona p = new Persona();
        p.setId(id);
        p.setNombre(req.getParameter("nombre"));
        p.setDireccion(req.getParameter("direccion"));
        p.setTelefono(req.getParameter("telefono"));
        return p;
    }

    private static String param(HttpServletRequest req, String k) { return param(req, k, null); }
    private static String param(HttpServletRequest req, String k, String def) {
        String v = req.getParameter(k);
        return (v == null || v.isEmpty()) ? def : v.trim();
    }

}
