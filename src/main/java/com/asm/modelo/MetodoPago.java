package com.asm.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "metodo_pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private int idMetodoPago;

    @Column(name = "nombre_metodo", nullable = false, length = 20)
    private String nombreMetodo;

    // Constructor vacío obligatorio para Hibernate
    public MetodoPago() {}

    // Constructor para usar en el código
    public MetodoPago(String nombreMetodo) {
        this.nombreMetodo = nombreMetodo;
    }

    // Getters y Setters
    public int getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(int idMetodoPago) { this.idMetodoPago = idMetodoPago; }

    public String getNombreMetodo() { return nombreMetodo; }
    public void setNombreMetodo(String nombreMetodo) { this.nombreMetodo = nombreMetodo; }
}