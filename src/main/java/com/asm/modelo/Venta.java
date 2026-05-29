package com.asm.modelo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private int idVenta;

    // Marcado como no insertable ni actualizable para que MySQL use su DEFAULT CURRENT_TIMESTAMP
    @Column(name = "fecha", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(name = "id_metodo_pago", nullable = false)
    private int idMetodoPago;

    // Constructor vacío obligatorio
    public Venta() {}

    // Constructor para inicializar transacciones desde el controlador
    public Venta(int idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    // Getters y Setters
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public int getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(int idMetodoPago) { this.idMetodoPago = idMetodoPago; }

    // --- Agregado para el Módulo de Devoluciones ---
    @Column(name = "id_usuario", nullable = false)
    private int idUsuario = 1; // Por defecto ponemos 1 (Admin) por si Víctor no lo manda en su código aún

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    // -----------------------------------------------
}