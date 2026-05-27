package com.asm.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    // --- ATRIBUTOS ORIGINALES (Intocables por consistencia con el equipo) ---
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

    // --- ATRIBUTOS NUEVOS DE FIGMA (Se agregan como opcionales sin romper el script base) ---
    @Column(name = "sku", length = 50, unique = true)
    private String sku;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "variantes", length = 255)
    private String variantes;

    @Column(name = "costo_compra")
    private Double costoCompra;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "proveedor", length = 150)
    private String proveedor;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    public Producto() {}

    // Constructor actualizado
    public Producto(String sku, String nombreProducto, String categoria, String variantes, double precio, Double costoCompra, int stock, Integer stockMinimo, String proveedor, String descripcion, int idTalla, int idGenero) {
        this.sku = sku;
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.variantes = variantes;
        this.precio = precio;
        this.costoCompra = costoCompra;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.proveedor = proveedor;
        this.descripcion = descripcion;
        this.idTalla = idTalla; // Respetando la BD original
        this.idGenero = idGenero; // Respetando la BD original
    }

    // --- GETTERS Y SETTERS ---
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

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getVariantes() { return variantes; }
    public void setVariantes(String variantes) { this.variantes = variantes; }

    public Double getCostoCompra() { return costoCompra; }
    public void setCostoCompra(Double costoCompra) { this.costoCompra = costoCompra; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}