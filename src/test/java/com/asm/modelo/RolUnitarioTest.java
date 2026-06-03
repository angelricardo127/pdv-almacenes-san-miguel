package com.asm.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RolUnitarioTest {

    @Test
    public void testConstructorInicializaRol() {
        // creamos un rol de administrador usando el constructor
        Rol rol = new Rol("administrador");

        // verificamos que el nombre no sea nulo y se asigne exactamente lo que pedimos
        assertNotNull(rol.getNombreRol(), "el nombre del rol no debe ser nulo");
        assertEquals("administrador", rol.getNombreRol(), "el nombre del rol debe coincidir");
    }

    @Test
    public void testLongitudNombreRolPermitida() {
        // creamos un rol con un nombre de uso comun en el sistema
        Rol rol = new Rol("cajero principal de turno matutino");

        // validamos logicamente que la longitud del texto no supere los 50 caracteres
        // para asegurar que respetara tu regla de base de datos cuando se intente guardar
        assertTrue(rol.getNombreRol().length() <= 50, "el nombre del rol debe medir 50 caracteres o menos");
    }

    @Test
    public void testIndependenciaDeInstanciasRol() {
        // creamos dos roles distintos para asegurar que no chocan en la memoria ram
        Rol rolAdmin = new Rol("admin");
        Rol rolCajero = new Rol("cajero");

        // comprobamos que ambos objetos son independientes y mantienen sus propios datos
        assertNotEquals(rolAdmin.getNombreRol(), rolCajero.getNombreRol(), "los roles deben mantener informacion independiente");
    }
}