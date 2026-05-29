package com.asm.modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DetalleVentaTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @BeforeAll
    public static void setup() {
        sessionFactory = new Configuration().configure("com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
    }

    @BeforeEach
    public void openSession() {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @Test
    @DisplayName("Debería registrar el detalle de un artículo vendido")
    public void testGuardarDetalleVenta() {
        // 1. Preparar: Para meter un detalle, primero registramos una cabecera de venta (Efectivo = 1)
        Venta cabeceraVenta = new Venta(1);
        session.persist(cabeceraVenta);

        // Forzamos a Hibernate a ir a MySQL para que se genere el id_venta real
        session.flush();

        // 2. Crear el detalle: Vinculamos la venta recién creada y el producto 1 (inyectado en el script)
        // Compramos 2 playeras polo a un precio unitario de $250.00
        DetalleVenta detalle = new DetalleVenta(cabeceraVenta.getIdVenta(), 1, 2, 250.00);

        // 3. Ejecutar: Guardamos el detalle
        session.persist(detalle);
        transaction.commit();

        // 4. Verificar: Validar que MySQL generó el id_detalle autoincrementable
        assertTrue(detalle.getIdDetalle() > 0, "El ID del detalle no se generó de forma correcta.");

        DetalleVenta guardado = session.get(DetalleVenta.class, detalle.getIdDetalle());
        assertNotNull(guardado);
        assertEquals(2, guardado.getCantidad(), "La cantidad registrada no coincide.");
    }

    @AfterEach
    public void closeSession() {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @AfterAll
    public static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}