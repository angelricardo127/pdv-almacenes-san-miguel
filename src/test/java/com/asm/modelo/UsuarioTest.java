package com.asm.modelo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @BeforeAll
    public static void setup() {
        // Lee tu archivo hibernate.cfg.xml y prepara el motor
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        // lee el archivo hibernate.cfg.xml y prepara el motor de hibernate
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    @BeforeEach
    public void openSession() {
        // Abre una conexión a MySQL antes de cada @Test
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @Test
    @DisplayName("Debería registrar un usuario administrador en la BD")
    public void testGuardarUsuario() {
        // 1. Preparar: Creamos un usuario de prueba (Usamos idRol = 1 asumiendo que es Administrador)
        Usuario nuevoAdmin = new Usuario("admin_jefe", "secreto123", "Angel", "Hernandez", 1);

        // 2. Ejecutar: Le decimos a Hibernate que lo guarde en MySQL
        session.persist(nuevoAdmin);
        transaction.commit();

        // 3. Verificar: Si se guardó, MySQL le debió asignar un ID mayor a 0
        assertTrue(nuevoAdmin.getIdUsuario() > 0, "El ID no se generó. Hubo un error al guardar.");

        // Comprobación extra: Lo buscamos directamente en la BD para estar 100% seguros
        Usuario usuarioGuardado = session.get(Usuario.class, nuevoAdmin.getIdUsuario());
        assertNotNull(usuarioGuardado, "El usuario no se encontró en la base de datos.");
        assertEquals("Angel", usuarioGuardado.getNombre(), "El nombre guardado no coincide.");
    }

    @AfterEach
    public void closeSession() {
        // Limpiamos la conexión para no saturar la memoria
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @AfterAll
    public static void tearDown() {
        // Apagamos el motor de Hibernate al terminar todas las pruebas
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
