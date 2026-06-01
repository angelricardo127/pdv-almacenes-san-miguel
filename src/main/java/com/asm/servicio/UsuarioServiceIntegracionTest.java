package com.asm.servicio;

import com.asm.modelo.Usuario;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioServiceIntegracionTest {

    private static SessionFactory factory;
    private UsuarioService usuarioService;

    // Prendemos la conexión a la base de datos
    @BeforeAll
    public static void iniciarConexion() {
        Configuration config = new Configuration();
        config.configure("/com/asm/vista/hibernate.cfg.xml");

        // Avisamos qué clases mapeadas vamos a usar
        config.addAnnotatedClass(com.asm.modelo.Usuario.class);
        // Descomenta la siguiente línea si tu clase Usuario está ligada a un Rol con llaves foráneas
        // config.addAnnotatedClass(com.asm.modelo.Rol.class);

        factory = config.buildSessionFactory();
    }

    @BeforeEach
    public void prepararServicio() {
        usuarioService = new UsuarioService(factory);
    }

    @AfterAll
    public static void cerrarConexion() {
        if (factory != null) {
            factory.close();
        }
    }

    // 1. PROBAR: Login exitoso (El camino feliz)
    @Test
    public void checarLoginConCredencialesCorrectas() {
        // ⚠️ OJO: Cambia "admin" y "1234" por un usuario y contraseña que SÍ existan en tu tabla
        Usuario user = usuarioService.validarUsuario("admin", "mangel123");

        // Verificamos que sí nos traiga al usuario de MySQL
        assertNotNull(user, "El usuario regresó nulo. Revisa si la contraseña es correcta y si estatus = 1");
        System.out.println("-> Prueba 1 Exitosa: Bienvenido al sistema, " + user.getUsername());
    }

    // 2. PROBAR: Login fallido por mala contraseña
    @Test
    public void checarLoginConContrasenaIncorrecta() {
        // Le pasamos un usuario real pero una contraseña inventada
        Usuario user = usuarioService.validarUsuario("admin", "clave_equivocada");

        // El sistema DEBE regresarnos null porque no coinciden
        assertNull(user, "¡Peligro! El sistema dejó entrar a alguien con mala contraseña.");
        System.out.println("-> Prueba 2 Exitosa: El sistema bloqueó el acceso por contraseña incorrecta.");
    }

    // 3. PROBAR: Login fallido por usuario inexistente
    @Test
    public void checarLoginConUsuarioInexistente() {
        // Le pasamos datos de alguien que no está en la base
        Usuario user = usuarioService.validarUsuario("hacker_anonimo", "1234");

        assertNull(user, "El sistema encontró un usuario que no debería existir en MySQL.");
        System.out.println("-> Prueba 3 Exitosa: El sistema ignoró correctamente a un usuario inexistente.");
    }
}