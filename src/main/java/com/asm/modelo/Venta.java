package com.asm.modelo;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private int idVenta;

    @Column(name = "fecha", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(name = "id_metodo_pago", nullable = false)
    private int idMetodoPago;

    @Column(name = "id_usuario", nullable = false)
    private int idUsuario = 1;

    //  La columna que agregamos a la BD
    @Column(name = "total")
    private Double total;

    public Venta() {}

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
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}