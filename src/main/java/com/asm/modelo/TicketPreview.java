package com.asm.modelo;

public class TicketPreview {
    private String numeroTicket;
    private String fecha;
    private String nombreCajero;
    private double total;
    private int cantidadProductos;
    private String metodoPago;

    public TicketPreview(String numeroTicket, String fecha, String nombreCajero, double total, int cantidadProductos, String metodoPago) {
        this.numeroTicket = numeroTicket;
        this.fecha = fecha;
        this.nombreCajero = nombreCajero;
        this.total = total;
        this.cantidadProductos = cantidadProductos;
        this.metodoPago = metodoPago;
    }

    // Getters
    public String getNumeroTicket() { return numeroTicket; }
    public String getFecha() { return fecha; }
    public String getNombreCajero() { return nombreCajero; }
    public double getTotal() { return total; }
    public int getCantidadProductos() { return cantidadProductos; }
    public String getMetodoPago() { return metodoPago; }
}