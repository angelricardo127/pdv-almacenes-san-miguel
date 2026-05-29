package com.asm.modelo;
// define el paquete donde se encuentra la clase usuario

import jakarta.persistence.*;
// importa las anotaciones y clases necesarias de jpa para mapear objetos java a tablas de base de datos

@Entity
// indica que esta clase es una entidad jpa que se relaciona con una tabla en la base de datos

@Table(name = "usuario")
// especifica que la tabla en la base de datos se llama "usuario"

public class Usuario {
// declaración de la clase pública usuario

    @Id
    // define que el atributo siguiente es la clave primaria de la tabla

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // indica que el valor de la clave primaria se generará automáticamente por la base de datos (auto incremental)

    @Column(name = "id_usuario")
    // mapea el atributo idUsuario con la columna id_usuario en la tabla

    private int idUsuario;
    // atributo privado que representa la columna id_usuario

    @Column(name = "usuario", unique = true, nullable = false)
    // mapea el atributo username con la columna usuario, debe ser único y no puede ser nulo

    private String username;
    // atributo privado que representa el nombre de usuario

    @Column(name = "contrasena", nullable = false)
    // mapea el atributo contrasena con la columna contrasena, no puede ser nulo

    private String contrasena;
    // atributo privado que representa la contraseña del usuario

    @Column(name = "nombre", nullable = false)
    // mapea el atributo nombre con la columna nombre, no puede ser nulo

    private String nombre;
    // atributo privado que representa el nombre del usuario

    @Column(name = "apellido_paterno", nullable = false)
    // mapea el atributo apellidoPaterno con la columna apellido_paterno, no puede ser nulo

    private String apellidoPaterno;
    // atributo privado que representa el apellido paterno del usuario

    @Column(name = "id_rol", nullable = false)
    // mapea el atributo idRol con la columna id_rol, no puede ser nulo

    private int idRol;
    // atributo privado que representa el rol asignado al usuario

    @Column(name = "estatus")
    // mapea el atributo estatus con la columna estatus

    private int estatus;
    // atributo privado que representa el estado del usuario (activo, inactivo, etc.)

    // constructor vacío obligatorio para hibernate
    public Usuario() {}
    // constructor sin parámetros requerido por hibernate para instanciar objetos al recuperar datos

    // constructor para crear usuarios desde el código
    public Usuario(String username, String contrasena, String nombre, String apellidoPaterno, int idRol) {
        this.username = username;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.idRol = idRol;
        this.estatus = 1;
    }
    // constructor que inicializa un usuario con valores básicos y asigna estatus por defecto en 1

    // getters y setters
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
