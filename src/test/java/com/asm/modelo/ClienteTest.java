package com.asm.modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

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
    @DisplayName("Debería registrar un cliente de Almacenes San Miguel en MySQL")
    public void testGuardarCliente() {
        // Preparar con la nueva firma del DDL
        Cliente cliente = new Cliente("Victor", "Ojeda", "Cabrera", "4777654321", "victor@asm.com");

        // Guardar
        session.persist(cliente);
        transaction.commit();

        // Validar éxito de la persistencia
        assertTrue(cliente.getIdCliente() > 0, "Error: La clave primaria autoincrementable falló.");

        Cliente recuperado = session.get(Cliente.class, cliente.getIdCliente());
        assertNotNull(recuperado);
        assertEquals("Victor", recuperado.getNombre());
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