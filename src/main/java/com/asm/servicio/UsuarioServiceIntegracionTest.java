package com.asm.servicio;

import com.asm.modelo.Usuario;
import com.asm.modelo.Rol;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de Integración para el Módulo de Seguridad (Backend).
 * Proyecto: Almacenes San Miguel

 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioServiceIntegracionTest {

    private SessionFactory factory;
    private UsuarioService usuarioService;
    private int idUsuarioPrueba; // Para guardar el ID del usuario creado en la prueba 4

    @BeforeAll
    public void setup() {
        System.out.println("⚙️ Iniciando configuración de Hibernate para Pruebas de Seguridad...");
        try {
            Configuration configuration = new Configuration();
            configuration.configure("com/asm/vista/hibernate.cfg.xml");
            configuration.addAnnotatedClass(Usuario.class);
            configuration.addAnnotatedClass(Rol.class);

            factory = configuration.buildSessionFactory();
            usuarioService = new UsuarioService(factory);
            System.out.println("✅ Conexión a BD de pruebas establecida.");
        } catch (Exception e) {
            fail("❌ Fallo crítico al iniciar Hibernate: " + e.getMessage());
        }
    }

    @AfterAll
    public void tearDown() {
        System.out.println("🧹 Limpiando recursos y cerrando conexión...");
        if (factory != null) {
            factory.close();
        }
    }

    // =========================================================================
    // PRUEBA 1: ACCESO EXITOSO
    // =========================================================================
    @Test
    @DisplayName("TS-01: Validar acceso con credenciales correctas")
    public void testValidarUsuario_CredencialesCorrectas_RetornaObjeto() {
        // Asume que el usuario 'admin' con clave '1234' existe y está activo
        try {
            Usuario usuario = usuarioService.validarUsuarioDetallado("admin", "mangel123");

            assertNotNull(usuario, "El usuario no debería ser null");
            assertEquals("admin", usuario.getUsername(), "El username recuperado debe coincidir");
            assertEquals(1, usuario.getEstatus(), "El usuario debe estar activo");

            System.out.println("✅ Prueba 1 superada: Acceso concedido al admin.");
        } catch (Exception e) {
            fail("No debió lanzar excepción con credenciales correctas. Error: " + e.getMessage());
        }
    }

    // =========================================================================
    // PRUEBA 2: CONTRASEÑA INCORRECTA
    // =========================================================================
    @Test
    @DisplayName("TS-02: Bloqueo por contraseña incorrecta")
    public void testValidarUsuario_ContrasenaIncorrecta_LanzaExcepcion() {
        Exception excepcion = assertThrows(Exception.class, () -> {
            usuarioService.validarUsuarioDetallado("admin", "claveFalsa123");
        });

        // Verificamos que el error sea exactamente el que lanza tu método 2
        assertEquals("Contraseña incorrecta. Inténtelo de nuevo.", excepcion.getMessage());
        System.out.println("✅ Prueba 2 superada: Excepción atrapada correctamente (" + excepcion.getMessage() + ")");
    }

    // =========================================================================
    // PRUEBA 3: USUARIO INACTIVO / INEXISTENTE
    // =========================================================================
    @Test
    @DisplayName("TS-03: Bloqueo por usuario que no existe en BD")
    public void testValidarUsuario_UsuarioInexistente_LanzaExcepcion() {
        Exception excepcion = assertThrows(Exception.class, () -> {
            usuarioService.validarUsuarioDetallado("fantasma", "1234");
        });

        assertEquals("El usuario no existe o es incorrecto.", excepcion.getMessage());
        System.out.println("✅ Prueba 3 superada: Sistema bloqueó al usuario fantasma.");
    }

    // =========================================================================
    // PRUEBA 4: PERSISTENCIA HIBERNATE (NUEVO USUARIO)
    // =========================================================================
    @Test
    @DisplayName("TS-04: Guardar nuevo usuario con session.persist()")
    public void testRegistrarUsuario_NuevoUsuario_GuardaEnBD() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Test");
        nuevoUsuario.setApellidoPaterno("QA");
        nuevoUsuario.setUsername("testqa_" + System.currentTimeMillis()); // Username único para que no choque
        nuevoUsuario.setContrasena("pass123");
        nuevoUsuario.setIdRol(2); // Cajero
        nuevoUsuario.setEstatus(1); // Activo

        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(nuevoUsuario);
            tx.commit();

            idUsuarioPrueba = nuevoUsuario.getIdUsuario(); // Guardamos el ID autogenerado

            assertTrue(idUsuarioPrueba > 0, "Hibernate debió generar un ID mayor a 0");
            System.out.println("✅ Prueba 4 superada: Usuario guardado con ID " + idUsuarioPrueba);

            // Limpieza: Borramos al usuario de prueba para no ensuciar tu BD real
            Transaction txDelete = session.beginTransaction();
            session.remove(nuevoUsuario);
            txDelete.commit();

        } catch (Exception e) {
            fail("Falló el guardado en la base de datos: " + e.getMessage());
        }
    }
}