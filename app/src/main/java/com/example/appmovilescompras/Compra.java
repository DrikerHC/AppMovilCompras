package com.example.appmovilescompras;

public class Compra {

    private long id;
    private String nombre;
    private String fecha;
    private String imagen;

    // Constructor vacío que le hace falta al Firebase/SQLite
    public Compra() {}

    // Constructor principal de la clase
    public Compra(String nombre, String fecha, String imagen) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.imagen = imagen;
    }

    // Getters y setters para los campos de la compra
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUrlImagen() {
        return imagen;
    }

    public void setUrlImagen(String imagen) {
        this.imagen = imagen;
    }
}