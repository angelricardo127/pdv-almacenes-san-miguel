package com.asm.modelo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "corte_caja")
public class CorteCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corte")
    private int idCorte;

    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    @Column(name = "fecha_turno", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaTurno;

    @Column(name = "fondo_inicial", nullable = false)
    private double fondoInicial;

    @Column(name = "retiros_manuales")
    private double retirosManuales;

    @Column(name = "monto_fisico")
    private double montoFisico;

    // Constructor vacío obligatorio para Hibernate
    public CorteCaja() {}

    // Constructor para cumplir con los pasos de Apertura
    public CorteCaja(int idUsuario, double fondoInicial) {
        this.idUsuario = idUsuario;
        this.fechaTurno = new Date(); // Paso: Capturar fecha y hora actual del sistema
        this.fondoInicial = fondoInicial;
        this.retirosManuales = 0.0;    // Paso: Iniciar contador de retiros en cero
        this.montoFisico = 0.0;        // Paso: Se inicializa en cero para indicar que está abierta
    }

    // --- GETTERS Y SETTERS ---
    public int getIdCorte() { return idCorte; }
    public void setIdCorte(int idCorte) { this.idCorte = idCorte; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public Date getFechaTurno() { return fechaTurno; }
    public void setFechaTurno(Date fechaTurno) { this.fechaTurno = fechaTurno; }

    public double getFondoInicial() { return fondoInicial; }
    public void setFondoInicial(double fondoInicial) { this.fondoInicial = fondoInicial; }

    public double getRetirosManuales() { return retirosManuales; }
    public void setRetirosManuales(double retirosManuales) { this.retirosManuales = retirosManuales; }

    public double getMontoFisico() { return montoFisico; }
    public void setMontoFisico(double montoFisico) { this.montoFisico = montoFisico; }
}