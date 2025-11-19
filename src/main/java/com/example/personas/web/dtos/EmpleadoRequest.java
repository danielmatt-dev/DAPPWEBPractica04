package com.example.personas.web.dtos;

public class EmpleadoRequest {

    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private Long claveDepto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmpleadoRequest(Long id, String nombre, String direccion, String telefono, Long claveDepto) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.claveDepto = claveDepto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Long getClaveDepto() {
        return claveDepto;
    }

    public void setClaveDepto(Long claveDepto) {
        this.claveDepto = claveDepto;
    }
}
