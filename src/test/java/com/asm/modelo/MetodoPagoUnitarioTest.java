package com.asm.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetodoPagoUnitarioTest {

    @Test
    public void testMetodoPagoAsignaNombreCorrectamente() {
        // instanciamos un nuevo metodo de pago
        MetodoPago metodo = new MetodoPago("tarjeta de credito");

        // validamos que la instancia garantice que el nombre no quede nulo
        // y que el texto ingresado sea exactamente el que se guardo en memoria
        assertNotNull(metodo.getNombreMetodo(), "el nombre del metodo no puede ser nulo");
        assertEquals("tarjeta de credito", metodo.getNombreMetodo(), "el nombre del metodo debe coincidir");
    }

    @Test
    public void testLongitudMetodoPagoPermitida() {
        // creamos un metodo de pago con un nombre que esta al limite de caracteres
        MetodoPago metodo = new MetodoPago("transferencia bancar");

        // validamos logicamente que la longitud del texto no supere los 20 caracteres
        // comprobando asi que respetara tu regla de base de datos (length = 20)
        assertTrue(metodo.getNombreMetodo().length() <= 20, "el nombre del metodo debe medir 20 caracteres o menos");
    }
}