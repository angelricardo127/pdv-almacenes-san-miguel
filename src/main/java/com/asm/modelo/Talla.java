package com.asm.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "tallas")
public class Talla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_talla")
    private int idTalla;

    @Column(name = "nombre_talla", nullable = false, length = 10)
    private String nombreTalla;

    public Talla() {}

    public Talla(String nombreTalla) {
        this.nombreTalla = nombreTalla;
    }
        //set y get
    public int getIdTalla() { return idTalla; }
    public void setIdTalla(int idTalla) { this.idTalla = idTalla; }

    public String getNombreTalla() { return nombreTalla; }
    public void setNombreTalla(String nombreTalla) { this.nombreTalla = nombreTalla; }
}