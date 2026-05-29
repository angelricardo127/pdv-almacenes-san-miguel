package com.asm.modelo;

public class DetalleTicketPreview {
    private int idProducto; // El ID que faltaba
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    // Constructor actualizado para recibir el ID
    public DetalleTicketPreview(int idProducto, String nombreProducto, int cantidad, double precioUnitario, double subtotal) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // --- AQUÍ ESTÁ EL GETTER QUE TE MARCABA ERROR ---
    public int getIdProducto() { return idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
}