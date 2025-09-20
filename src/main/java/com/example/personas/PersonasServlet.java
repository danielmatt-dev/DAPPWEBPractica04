package com.example.personas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/crud")
public class PersonasServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private void json(HttpServletResponse resp, int code, Object body) throws IOException {
        resp.setStatus(code);
        resp.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.print(gson.toJson(body));
        }
    }

    private String p(HttpServletRequest req, String k) {
        String v = req.getParameter(k);
        return v == null ? "" : v.trim();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = p(req, "action");
        try (Connection c = Db.get()) {
            switch (action) {
                case "list": {
                    String sql = "SELECT id, name, address, phone FROM personas ORDER BY id";
                    try (PreparedStatement st = c.prepareStatement(sql);
                        ResultSet rs = st.executeQuery()) {
                        List<Map<String, Object>> out = new ArrayList<>();
                        while (rs.next()) {
                            Map<String, Object> row = new LinkedHashMap<>();
                            row.put("clave", String.valueOf(rs.getObject("id")));
                            row.put("nombre", rs.getString("name"));
                            row.put("direccion", rs.getString("address"));
                            row.put("telefono", rs.getString("phone"));
                            out.add(row);
                        }
                        json(resp, 200, out);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                case "get": {
                    long clave = Long.parseLong(p(req, "clave"));
                    String sql = "SELECT id, name, address, phone FROM personas WHERE id = ?";
                    try (PreparedStatement st = c.prepareStatement(sql)) {
                        st.setLong(1, clave);
                        try (ResultSet rs = st.executeQuery()) {
                            if (!rs.next()) { json(resp, 404, Map.of("error", "No encontrado")); return; }
                            Map<String, Object> out = new LinkedHashMap<>();
                            out.put("clave", Long.parseLong(rs.getString("id")));
                            out.put("nombre", rs.getString("name"));
                            out.put("direccion", rs.getString("address"));
                            out.put("telefono", rs.getString("phone"));
                            json(resp, 200, out);
                        }
                    }
                    return;
                }
                default:
                    System.out.println("Respuesta: " + resp);
                    json(resp, 400, Map.of("error", "Acción no reconocida"));
            }
        } catch (SQLException e) {
            json(resp, 500, Map.of("error", "Error inesperado", "detail", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String action    = req.getParameter("action");
        long clave     = Long.parseLong(req.getParameter("clave"));
        String nombre    = req.getParameter("nombre");
        String direccion = req.getParameter("direccion");
        String telefono  = req.getParameter("telefono");

        System.out.println("[POST] action=" + action + " clave=" + clave +
                " nombre=" + nombre + " direccion=" + direccion + " telefono=" + telefono);

        try (Connection c = Db.get(); PrintWriter out = resp.getWriter()) {

            if ("create".equals(action)) {
                if (isBlank(nombre) || isBlank(direccion) || isBlank(telefono)) {
                    resp.setStatus(400);
                    out.print("{\"error\":\"Faltan datos (clave,nombre,direccion,telefono)\"}");
                    return;
                }
                try (PreparedStatement st = c.prepareStatement(
                        "INSERT INTO personas (id, name, address, phone) VALUES (?,?,?,?)")) {
                    st.setLong(1, clave);
                    st.setString(2, nombre.trim());
                    st.setString(3, direccion.trim());
                    st.setString(4, telefono.trim());
                    st.executeUpdate();
                } catch (SQLException e) {
                    // PostgreSQL: 23505 = unique_violation
                    if ("23505".equals(e.getSQLState())) {
                        resp.setStatus(409);
                        out.print("{\"error\":\"La clave ya existe (unique_violation)\"}");
                        return;
                    }
                    logSqlError(e, "create");
                    resp.setStatus(500);
                    out.print(jsonSqlError(e));
                    return;
                }
                resp.setStatus(201);
                out.print("{\"message\":\"Creado\"}");
                return;
            }

            if ("update".equals(action)) {
                try (PreparedStatement st = c.prepareStatement(
                        "UPDATE personas SET name=?, address=?, phone=? WHERE id=?")) {
                    st.setString(1, safe(nombre));
                    st.setString(2, safe(direccion));
                    st.setString(3, safe(telefono));
                    st.setLong(4, clave);
                    int n = st.executeUpdate();
                    if (n == 0) {
                        resp.setStatus(404);
                        out.print("{\"error\":\"Clave no encontrada\"}");
                    } else {
                        out.print("{\"message\":\"Actualizado\"}");
                    }
                } catch (SQLException e) {
                    logSqlError(e, "update");
                    resp.setStatus(500);
                    out.print(jsonSqlError(e));
                }
                return;
            }

            if ("delete".equals(action)) {
                try (PreparedStatement st = c.prepareStatement("DELETE FROM personas WHERE id=?")) {
                    st.setLong(1, clave);
                    int n = st.executeUpdate();
                    if (n == 0) {
                        resp.setStatus(404);
                        out.print("{\"error\":\"No encontrado\"}");
                    } else {
                        out.print("{\"message\":\"Eliminado\"}");
                    }
                } catch (SQLException e) {
                    logSqlError(e, "delete");
                    resp.setStatus(500);
                    out.print(jsonSqlError(e));
                }
                return;
            }

            resp.setStatus(400);
            resp.getWriter().print("{\"error\":\"Acción inválida\"}");

        } catch (SQLException e) {
            System.err.println("[DB] Conexión/otro error: " + e.getMessage());
            resp.setStatus(500);
            resp.getWriter().print(jsonSqlError(e));
        }
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private static void logSqlError(SQLException e, String op) {
        System.err.println("[SQL][" + op + "] state=" + e.getSQLState() + " code=" + e.getErrorCode() + " msg=" + e.getMessage());
    }

    private static String jsonSqlError(SQLException e) {
        String msg = e.getMessage() == null ? "" : e.getMessage().replace("\"","\\\"");
        String state = e.getSQLState();
        int code = e.getErrorCode();
        return "{\"error\":\"SQL Error\",\"state\":\"" + (state==null?"":state) + "\",\"code\":" + code + ",\"detail\":\"" + msg + "\"}";
    }

}
