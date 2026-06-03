package com.asm.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductoUnitarioTest {

    @Test
    public void testConstructorProducto() {
        // instanciamos un producto con todos sus datos completos
        Producto producto = new Producto("sku-100", "tenis campus", "calzado", "negro", 1500.0, 900.0, 50, 10, "adidas", "tenis clasicos", 40, 1);

        // verificamos que los datos principales se asignen bien
        assertEquals("sku-100", producto.getSku());
        assertEquals("tenis campus", producto.getNombreProducto());
        assertEquals(1500.0, producto.getPrecio());

        // comprobamos que el producto este activo por defecto
        assertTrue(producto.getActivo(), "el producto debe estar activo al crearse");
    }

    @Test
    public void testSettersYGettersProducto() {
        // creamos un producto vacio
        Producto producto = new Producto();

        // le metemos datos usando los setters
        producto.setIdProducto(5);
        producto.setStock(20);
        producto.setCategoria("deportes");
        producto.setActivo(false); // lo desactivamos manual

        // sacamos los datos con los getters para ver si coinciden
        assertEquals(5, producto.getIdProducto());
        assertEquals(20, producto.getStock());
        assertEquals("deportes", producto.getCategoria());
        assertFalse(producto.getActivo(), "el producto deberia estar inactivo");
    }

    @Test
    public void testValidacionDeStock() {
        // creamos un producto con stock de 50 y minimo de 10
        Producto producto = new Producto("sku-102", "tenis campus", "calzado", "azul", 1500.0, 900.0, 50, 10, "adidas", "tenis clasicos", 40, 1);

        // comprobamos logicamente que el stock actual cubra el minimo requerido
        assertTrue(producto.getStock() >= producto.getStockMinimo(), "el stock debe ser mayor o igual al minimo");
    }
}