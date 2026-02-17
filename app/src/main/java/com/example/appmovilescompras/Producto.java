package com.example.appmovilescompras;

public class Producto {
    private int id;
    private String nombre;
    private int cantidad;

    // Constructor para el FireBase
    public Producto() {}

    // Constructor sin ID (para crear antes de guardar)
    public Producto(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    // Constructor con ID (para cargar desde el Sqlite)
    public Producto(int id, String nombre, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    // Geters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}