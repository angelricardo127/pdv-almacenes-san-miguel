package com.asm.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DetalleVentaUnitarioTest {

    @Test
    public void testConstructorDetalleVenta() {
        // creamos el detalle  agregar 2 productos de 250.50 al carrito
        DetalleVenta detalle = new DetalleVenta(10, 55, 2, 250.50);

        // verificamos que el constructor asigne los valores de forma exacta
        assertEquals(10, detalle.getIdVenta());
        assertEquals(55, detalle.getIdProducto());
        assertEquals(2, detalle.getCantidad());
        assertEquals(250.50, detalle.getPrecioUnitario());
    }

    @Test
    public void testSettersYGettersDetalleVenta() {
        // creamos un detalle vacio usando el constructor de hibernate
        DetalleVenta detalle = new DetalleVenta();

        // asignamos los datos manualmente con los metodos set
        detalle.setIdDetalle(1);
        detalle.setIdVenta(20);
        detalle.setIdProducto(88);
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(100.0);

        // validamos que los metodos get devuelvan lo que acabamos de meter
        assertEquals(1, detalle.getIdDetalle());
        assertEquals(20, detalle.getIdVenta());
        assertEquals(88, detalle.getIdProducto());
        assertEquals(5, detalle.getCantidad());
        assertEquals(100.0, detalle.getPrecioUnitario());
    }

    @Test
    public void testValidacionPrecioUnitario() {
        // armamos el detalle de venta con un precio cobrado en caja de 150.0
        DetalleVenta detalle = new DetalleVenta(5, 12, 1, 150.0);

        // validamos que el precio cobrado sea el mismo del catalogo
        assertEquals(200.0, detalle.getPrecioUnitario(), "el precio del carrito no cuadra con el catalogo");
    }
}