package com.example.personas.web;

import com.example.personas.models.Departamento;
import com.example.personas.models.Empleado;
import com.example.personas.service.ICrudService;
import com.example.personas.web.dtos.EmpleadoRequest;
import com.example.personas.web.dtos.EmpleadoResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmpleadoController extends HttpServlet {

    private ICrudService<Empleado, Long> service;
    private ICrudService<Departamento, Long> deptoService;

    @Override
    public void init() {
        this.service = (ICrudService<Empleado, Long>) getServletContext().getAttribute("personaService");
        this.deptoService = (ICrudService<Departamento, Long>) getServletContext().getAttribute("deptoService");

        if (this.service == null) throw new IllegalStateException("personaService no inyectado");
        if (this.deptoService == null) throw new IllegalStateException("deptoService no inyectado");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action", "list");

        try {
            switch (action) {
                case "edit": {
                    Long id = Long.valueOf(param(req, "clave"));
                    Optional<Empleado> p = service.obtener(id);
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
        }

        forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = param(req, "action");

        try {
            if ("create".equals(action)) {
                EmpleadoRequest er = bind(req, false);
                Empleado emp = toEntity(er);
                service.crear(emp);
                req.setAttribute("status", "Creado");

            } else if ("update".equals(action)) {
                EmpleadoRequest er = bind(req, true);
                Empleado emp = toEntity(er);
                boolean ok = service.actualizar(emp, emp.getId());
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
        List<EmpleadoResponse> emps = service.listar().stream().map(empleado -> {
            Departamento depto = empleado.getDepto();
            return new EmpleadoResponse(
                    empleado.getId(),
                    empleado.getNombre(),
                    empleado.getDireccion(),
                    empleado.getTelefono(),
                    depto.getId(),
                    depto.getNombre()
            );
        }).collect(Collectors.toList());
        req.setAttribute("empleados", emps);
        req.setAttribute("departamentos", deptoService.listar()); // 👈 carga lista deptos
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/empleados.jsp");
        rd.forward(req, resp);
    }

    private static EmpleadoRequest bind(HttpServletRequest req, boolean requireId) {
        String sId = req.getParameter("clave");

        Long id = (sId == null || sId.isEmpty()) ? null : Long.valueOf(sId);
        if (requireId && id == null) throw new IllegalArgumentException("Clave requerida");

        String nombre = req.getParameter("nombre");
        String direccion = req.getParameter("direccion");
        String telefono = req.getParameter("telefono");
        Long claveDepto = Long.valueOf(req.getParameter("claveDepto"));

        return new EmpleadoRequest(id, nombre, direccion, telefono, claveDepto);
    }

    private Empleado toEntity(EmpleadoRequest er) {
        Empleado emp = new Empleado();
        emp.setId(er.getId());
        emp.setNombre(er.getNombre());
        emp.setDireccion(er.getDireccion());
        emp.setTelefono(er.getTelefono());

        Optional<Departamento> dep = deptoService.obtener(er.getClaveDepto());
        dep.ifPresent(emp::setDepto);

        return emp;
    }

    private static String param(HttpServletRequest req, String k) { return param(req, k, null); }
    private static String param(HttpServletRequest req, String k, String def) {
        String v = req.getParameter(k);
        return (v == null || v.isEmpty()) ? def : v.trim();
    }
}
