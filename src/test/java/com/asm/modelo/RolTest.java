package com.asm.modelo;// define el paquete donde se encuentra la clase roltest
import org.hibernate.Session;// importa la clase session de hibernate para manejar la conexión con la base de datos
import org.hibernate.SessionFactory;// importa la clase sessionfactory que crea sesiones para conectarse a la base de datos
import org.hibernate.Transaction;// importa la clase transaction para manejar transacciones en la base de datos
import org.hibernate.cfg.Configuration;// importa la clase configuration para leer el archivo de configuración de hibernate
import org.junit.jupiter.api.*;// importa las anotaciones de junit para definir pruebas unitarias
import static org.junit.jupiter.api.Assertions.*;// importa los métodos de aserción para verificar resultados en las pruebas

public class RolTest {
    private static SessionFactory sessionFactory;// atributo estático que representa la fábrica de sesiones de hibernate
    private Session session;// atributo que representa una sesión activa con la base de datos
    private Transaction transaction;// atributo que representa una transacción en la base de datos

    @BeforeAll// anotación que indica que el metodo se ejecuta una sola vez antes de todas las pruebas
    public static void setup() {
        // lee el archivo hibernate.cfg.xml y prepara el motor de hibernate
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    @BeforeEach// anotación que indica que el metodo se ejecuta antes de cada prueba
    public void openSession() {
        // abre una conexión a mysql antes de cada prueba
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @Test// anotación que indica que el metodo es una prueba unitaria

    @DisplayName("Debería registrar un nuevo rol en la BD")// nombre descriptivo de la prueba

    public void testGuardarRol() {// prueba para guardar un rol en la base de datos

        // preparar: creamos un rol de prueba con nombre cajero
        Rol nuevoRol = new Rol("CAJERO");

        // ejecutar: le decimos a hibernate que lo guarde en mysql
        session.persist(nuevoRol);
        transaction.commit();

        // verificar: comprobamos que se generó un id mayor a 0
        assertTrue(nuevoRol.getIdRol() > 0, "el id del rol no se generó.");

        // comprobación extra: buscamos el rol directamente en la base de datos
        Rol rolGuardado = session.get(Rol.class, nuevoRol.getIdRol());
        assertNotNull(rolGuardado, "el rol no se encontró en la bd.");
        assertEquals("CAJERO", rolGuardado.getNombreRol(), "el nombre del rol no coincide.");
    }

    @AfterEach// anotación que indica que el metodo se ejecuta después de cada prueba
    public void closeSession() {
        // cerramos la sesión para liberar recursos
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @AfterAll// anotación que indica que el metodo se ejecuta una sola vez después de todas las pruebas

    public static void tearDown() {
        // apagamos el motor de hibernate al terminar todas las pruebas
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
