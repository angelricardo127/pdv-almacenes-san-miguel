package com.asm.modelo;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class VentaUnitarioTest {

    @Test
    public void testConstructorYValoresPorDefecto() {
        // creamos la venta en memoria con el metodo de pago 2
        Venta venta = new Venta(2);

        // verificamos que el metodo de pago se asigno correctamente
        assertEquals(2, venta.getIdMetodoPago());

        // verificamos que el usuario por defecto sea 1 para no romper el resto del sistema
        assertEquals(1, venta.getIdUsuario(), "el id de usuario por defecto debe ser 1");

        // la fecha debe ser nula aqui porque mysql es quien la pone sola
        assertNull(venta.getFecha(), "la fecha inicial debe ser nula en memoria");
    }

    @Test
    public void testSettersYGettersVenta() {
        // creamos una venta vacia
        Venta venta = new Venta();
        Date fechaPrueba = new Date(); // creamos una fecha temporal

        // metemos datos manuales con los setters
        venta.setIdVenta(100);
        venta.setFecha(fechaPrueba);
        venta.setIdMetodoPago(3);
        venta.setIdUsuario(5);

        // validamos que los getters devuelvan lo exacto
        assertEquals(100, venta.getIdVenta());
        assertEquals(fechaPrueba, venta.getFecha());
        assertEquals(3, venta.getIdMetodoPago());
        assertEquals(5, venta.getIdUsuario());
    }

    @Test
    public void testMetodoDePago() {
        // registramos una venta con pago en efectivo (id 1)
        Venta venta = new Venta(1);

        // validamos que el metodo de pago registrado sea el correcto
        assertEquals(2, venta.getIdMetodoPago(), "el metodo de pago no coincide con el registro");
    }
}