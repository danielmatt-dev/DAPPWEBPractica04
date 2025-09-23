package com.example.personas.dao.jdbc;

import com.example.personas.dao.PersonaDAO;
import com.example.personas.model.Persona;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonaDaoJdbc implements PersonaDAO {

    private final DataSource ds;

    public PersonaDaoJdbc(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<Persona> findAll() {
        String sql = "SELECT id, name, address, phone FROM personas ORDER BY id";
        List<Persona> out = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
        return out;
    }

    @Override
    public Optional<Persona> findById(Long id) {
        String sql = "SELECT id, name, address, phone FROM personas WHERE id = ?";
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed", e);
        }
        return Optional.empty();
    }

    @Override
    public void insert(Persona p) {
        String sql = "INSERT INTO personas (id, name, address, phone) VALUES (?,?,?,?)";
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, p.getId());
            st.setString(2, safe(p.getNombre()));
            st.setString(3, safe(p.getDireccion()));
            st.setString(4, safe(p.getTelefono()));
            st.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalStateException("La clave ya existe", e);
            }
            throw new RuntimeException("insert failed", e);
        }
    }

    @Override
    public boolean update(Persona p) {
        String sql = "UPDATE personas SET name=?, address=?, phone=? WHERE id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, safe(p.getNombre()));
            st.setString(2, safe(p.getDireccion()));
            st.setString(3, safe(p.getTelefono()));
            st.setLong(4, p.getId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("update failed", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM personas WHERE id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("delete failed", e);
        }
    }

    private static Persona map(ResultSet rs) throws SQLException {
        return new Persona(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("phone")
        );
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }

}
