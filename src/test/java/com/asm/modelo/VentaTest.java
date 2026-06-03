package com.asm.modelo;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class VentaTest {

    @Test
    public void testConstructorVacioYSetters() {
        // Arrange
        Venta venta = new Venta();
        LocalDateTime fechaActual = LocalDateTime.now();

        // Act
        venta.setIdVenta(150);
        venta.setFecha(fechaActual);
        venta.setIdMetodoPago(2); // Suponiendo que 2 es Tarjeta

        // Assert
        assertEquals(150, venta.getIdVenta(), "El ID de la venta no coincide");
        assertEquals(fechaActual, venta.getFecha(), "La fecha de la venta no coincide");
        assertEquals(2, venta.getIdMetodoPago(), "El ID del método de pago no coincide");
    }

    @Test
    public void testConstructorConParametros() {
        // Arrange & Act
        Venta venta = new Venta(1); // Suponiendo que 1 es Efectivo

        // Assert
        assertEquals(1, venta.getIdMetodoPago(), "El constructor con parámetros falló al asignar el método de pago");
        // Comprobamos que lo demás se inicialice vacío o nulo de forma correcta
        assertEquals(0, venta.getIdVenta());
        assertNull(venta.getFecha());
    }
}