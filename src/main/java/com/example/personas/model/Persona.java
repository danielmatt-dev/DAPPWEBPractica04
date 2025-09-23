package com.example.personas.model;

public class Persona {

    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;

    public Persona() {}
    public Persona(Long id, String nombre, String direccion, String telefono) {
        this.id = id; this.nombre = nombre; this.direccion = direccion; this.telefono = telefono;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

}
