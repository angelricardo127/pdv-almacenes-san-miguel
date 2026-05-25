package com.asm.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "generos")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero")
    private int idGenero;

    @Column(name = "nombre_genero", nullable = false, length = 50)
    private String nombreGenero;

    public Genero() {}

    public Genero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
    }

    public int getIdGenero() { return idGenero; }
    public void setIdGenero(int idGenero) { this.idGenero = idGenero; }

    public String getNombreGenero() { return nombreGenero; }
    public void setNombreGenero(String nombreGenero) { this.nombreGenero = nombreGenero; }
}