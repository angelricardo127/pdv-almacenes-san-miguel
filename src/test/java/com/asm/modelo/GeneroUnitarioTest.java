package com.asm.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeneroUnitarioTest {

    @Test
    public void testGeneroAsignaNombreCorrectamente() {
        // instanciamos un nuevo genero pensado para uniformes escolares
        Genero genero = new Genero("unisex escolar");

        // validamos que el nombre no sea nulo y corresponda al ingresado
        assertNotNull(genero.getNombreGenero(), "el nombre del genero no debe ser nulo");
        assertEquals("unisex escolar", genero.getNombreGenero(), "el nombre del genero debe coincidir exacto");
    }

    @Test
    public void testLongitudGeneroPermitida() {
        // creamos un genero con un nombre largo pero valido para tu inventario escolar
        Genero genero = new Genero("uniforme deportivo para ninas de nivel secundaria");

        // comprobamos logicamente que la longitud respete el limite de 50 caracteres
        // que estableciste en la anotacion de tu modelo
        assertTrue(genero.getNombreGenero().length() <= 50, "el nombre del genero debe medir 50 caracteres o menos");
    }
}
