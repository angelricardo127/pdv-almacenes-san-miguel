package com.asm.modelo;
// define el paquete donde se encuentra la clase, organiza el código dentro del proyecto
import jakarta.persistence.*;
// importa todas las anotaciones y clases necesarias de jpa para mapear objetos java a tablas de base de datos
@Entity// indica que esta clase es una entidad jpa, se relaciona con una tabla en la base de datos
@Table(name = "rol")// especifica que la tabla en la base de datos se llama "rol"

public class Rol {// declaración de la clase pública rol

    @Id// define que el atributo siguiente es la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY)// indica que el valor de la clave primaria se generará automáticamente por la base de datos (auto incremental)
    @Column(name = "id_rol")// mapea el atributo idRol con la columna id_rol en la tabla

    private int idRol;// atributo privado que representa la columna id_rol
    @Column(name = "nombre_rol", nullable = false, length = 50)// mapea el atributo nombreRol con la columna nombre_rol, no permite valores nulos y limita la longitud a 50 caracteres
    private String nombreRol;// atributo privado que representa la columna nombre_rol

    // constructor vacío obligatorio
    public Rol() {}// constructor sin parámetros requerido por hibernate para instanciar objetos al recuperar datos

    // constructor para usar en el código
    public Rol(String nombreRol) {
        this.nombreRol = nombreRol;
    }// constructor que permite crear un objeto rol inicializando el campo nombreRol

    // getters y setters
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}
