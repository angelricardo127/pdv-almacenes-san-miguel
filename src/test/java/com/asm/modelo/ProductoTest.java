package com.asm.modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @BeforeAll
    public static void setup() {
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    @BeforeEach
    public void openSession() {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @Test
    @DisplayName("Debería consultar el producto inyectado y actualizar su stock")
    public void testValidarYActualizarInventario() {
        // 1. Intentar recuperar el producto con ID 1 (inyectado por el script de pruebas)
        Producto producto = session.get(Producto.class, 1);

        // Verificar que el producto exista (Excepción E-2 del caso de uso)
        assertNotNull(producto, "El producto de prueba con ID 1 no fue localizado.");

        // 2. Simular validación de stock para una venta de 5 unidades (Excepción E-1)
        int cantidadAVender = 5;
        // ACTUALIZADO: Usamos getStockActual()
        assertTrue(producto.getStock() >= cantidadAVender, "Stock insuficiente para realizar la venta.");

        // 3. Modificar el stock (RF-20: Actualización automática de inventario)
        // ACTUALIZADO: Usamos getStockActual() y setStockActual()
        int stockInicial = producto.getStock();
        producto.setStock(stockInicial - cantidadAVender);

        session.merge(producto);
        transaction.commit();

        // 4. Comprobar que en la base de datos se guardó el nuevo inventario
        Producto productoActualizado = session.get(Producto.class, 1);
        // ACTUALIZADO: Usamos getStockActual()
        assertEquals(stockInicial - cantidadAVender, productoActualizado.getStock(), "El inventario no se descontó correctamente.");
    }

    @AfterEach
    public void closeSession() {
        if (session != null && session.isOpen()) session.close();
    }

    @AfterAll
    public static void tearDown() {
        if (sessionFactory != null) sessionFactory.close();
    }
}