package com.example.personas.web;

import com.example.personas.models.Departamento;
import com.example.personas.service.ICrudService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public class DeptoController extends HttpServlet {

    private ICrudService<Departamento, Long> service;

    @Override
    public void init() {
        this.service = (ICrudService<Departamento, Long>) getServletContext().getAttribute("deptoService");
        if (this.service == null) throw new IllegalStateException("deptoService no inyectado");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action", "list");

        try {
            switch (action) {
                case "edit": {
                    Long id = Long.valueOf(param(req, "clave"));
                    Optional<Departamento> dep = service.obtener(id);
                    if (dep.isPresent()) {
                        req.setAttribute("departamento", dep.get());
                        forward(req, resp);
                    } else {
                        req.setAttribute("status", "No encontrado");
                        forward(req, resp);
                    }
                    return;
                }
                case "new": {
                    forward(req, resp); // muestra formulario vacío
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
                Departamento dep = bind(req, true);
                service.crear(dep);
                req.setAttribute("status", "Creado");
            } else if ("update".equals(action)) {
                Departamento dep = bind(req, false);
                boolean ok = service.actualizar(dep, dep.getId());
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

        forward(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Departamento> lista = service.listar();
        req.setAttribute("departamentos", lista);
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/departamentos.jsp");
        rd.forward(req, resp);
    }

    private static Departamento bind(HttpServletRequest req, boolean requireId) {
        String sId = req.getParameter("clave");
        Long id = (sId == null || sId.isEmpty()) ? null : Long.valueOf(sId);
        if (requireId && id == null) throw new IllegalArgumentException("Clave requerida");

        Departamento d = new Departamento();
        d.setId(id);
        d.setNombre(req.getParameter("nombre"));
        return d;
    }

    private static String param(HttpServletRequest req, String k) { return param(req, k, null); }
    private static String param(HttpServletRequest req, String k, String def) {
        String v = req.getParameter(k);
        return (v == null || v.isEmpty()) ? def : v.trim();
    }
}