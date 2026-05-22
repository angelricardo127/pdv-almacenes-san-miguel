package com.asm.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // El nombre exacto en MySQL
    private int idUsuario;       // El nombre correcto en Java

    @Column(name = "usuario", unique = true, nullable = false)
    private String username;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @Column(name = "id_rol", nullable = false)
    private int idRol;

    @Column(name = "estatus")
    private int estatus;

    // 1. Constructor vacío obligatorio para Hibernate
    public Usuario() {}

    // 2. Constructor para crear usuarios desde el código
    public Usuario(String username, String contrasena, String nombre, String apellidoPaterno, int idRol) {
        this.username = username;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.idRol = idRol;
        this.estatus = 1;
    }

    // 3. Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public int getEstatus() { return estatus; }
    public void setEstatus(int estatus) { this.estatus = estatus; }
}