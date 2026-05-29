package com.asm.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetodoPagoTest {

    @Test
    public void testConstructorVacioYSetters() {
        // Arrange
        MetodoPago metodo = new MetodoPago();

        // Act
        metodo.setIdMetodoPago(1);
        metodo.setNombreMetodo("Efectivo");

        // Assert
        assertEquals(1, metodo.getIdMetodoPago(), "El ID del método de pago no coincide");
        assertEquals("Efectivo", metodo.getNombreMetodo(), "El nombre del método de pago no coincide");
    }

    @Test
    public void testConstructorConParametros() {
        // Arrange & Act
        MetodoPago metodo = new MetodoPago("Tarjeta");

        // Assert
        assertEquals("Tarjeta", metodo.getNombreMetodo(), "El constructor con parámetros falló al asignar el nombre");
        // Comprobamos que el ID empiece en 0 al no haber pasado por la base de datos aún
        assertEquals(0, metodo.getIdMetodoPago());
    }
}