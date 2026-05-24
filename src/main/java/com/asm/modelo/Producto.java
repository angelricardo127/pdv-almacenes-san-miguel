package com.asm.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private int idProducto;

    @Column(name = "nombre_product", nullable = false, length = 100)
    private String nombreProducto;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "precio", nullable = false)
    private double precio;

    @Column(name = "id_talla", nullable = false)
    private int idTalla;

    @Column(name = "id_genero", nullable = false)
    private int idGenero;

    // Constructor vacío obligatorio para Hibernate
    public Producto() {}

    // Constructor completo para usar en la lógica
    public Producto(String nombreProducto, int stock, double precio, int idTalla, int idGenero) {
        this.nombreProducto = nombreProducto;
        this.stock = stock;
        this.precio = precio;
        this.idTalla = idTalla;
        this.idGenero = idGenero;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getIdTalla() { return idTalla; }
    public void setIdTalla(int idTalla) { this.idTalla = idTalla; }

    public int getIdGenero() { return idGenero; }
    public void setIdGenero(int idGenero) { this.idGenero = idGenero; }
}