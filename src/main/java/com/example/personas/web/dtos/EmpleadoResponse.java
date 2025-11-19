package com.example.personas.web.dtos;

public class EmpleadoResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private Long claveDepto;
    private String nombreDepto;

    public EmpleadoResponse(Long id, String nombre, String direccion, String telefono, Long claveDepto, String nombreDepto) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.claveDepto = claveDepto;
        this.id = id;
        this.nombreDepto = nombreDepto;
    }

    public String getNombreDepto() {
        return nombreDepto;
    }

    public void setNombreDepto(String nombreDepto) {
        this.nombreDepto = nombreDepto;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
