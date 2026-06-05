package com.asm.servicio;

import com.asm.modelo.CorteCaja;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CorteCajaServiceIntegrationTest {

    private static SessionFactory factory;
    private CorteCajaService corteCajaService;

    // prendemos el motor de base de datos apuntando a la configuracion de hibernate
    @BeforeAll
    public static void iniciarConexion() {
        Configuration config = new Configuration();
        config.configure("/com/asm/vista/hibernate.cfg.xml");

        // le avisamos a hibernate que mapee la tabla de corte de caja
        config.addAnnotatedClass(com.asm.modelo.CorteCaja.class);

        factory = config.buildSessionFactory();
    }

    // preparamos el servicio fresquecito antes de correr cada test
    @BeforeEach
    public void prepararServicio() {
        corteCajaService = new CorteCajaService(factory);
    }

    // apagamos la conexion al final
    @AfterAll
    public static void cerrarConexion() {
        if (factory != null) {
            factory.close();
        }
    }

    //
    @Test
    public void checarQueExistaHistorialDeCortes() {
        CorteCaja ultimoCorte = corteCajaService.obtenerCorteActual();

        // validamos que la base de datos no rechace la consulta hql
        assertDoesNotThrow(() -> corteCajaService.obtenerCorteActual(), "error al consultar el ultimo corte de caja");

        if (ultimoCorte != null) {
            System.out.println("-> Prueba 1 Exitosa: El ultimo corte registrado fue operado por el usuario ID: " + ultimoCorte.getIdUsuario());
        } else {
            System.out.println("-> Prueba 1 Exitosa: La tabla de cortes de caja esta limpia/vacia.");
        }
    }

    //
    @Test
    public void checarBloqueoDeFondoInicialNegativo() {
        // simulamos que un cajero intenta abrir su turno con dinero negativo
        double fondoInvalido = -500.0;

        // comprobamos que tu servicio detenga el proceso y lance la excepcion exacta
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            corteCajaService.abrirCaja(1, fondoInvalido);
        });

        assertEquals("El fondo inicial no puede ser una cantidad negativa.", excepcion.getMessage());
        System.out.println("-> Prueba 2 Exitosa: El sistema bloqueo correctamente un fondo negativo de $" + fondoInvalido);
    }


    @Test
    public void checarFlujoCompletoDeCaja() {
        CorteCaja cortePendiente = corteCajaService.obtenerCorteActual();
        if (cortePendiente != null && cortePendiente.getMontoFisico() == 0.0) {
            corteCajaService.cerrarCaja(1.0); // la cerramos simbolicamente
        }


        boolean abierta = corteCajaService.abrirCaja(1, 1000.0);
        assertTrue(abierta, "fallo al intentar abrir la caja en mysql");

        //  Registrar un retiro para comprar garrafones de agua ($80)
        boolean retiroExitoso = corteCajaService.registrarRetiro(80.0);
        assertTrue(retiroExitoso, "fallo al registrar el retiro manual");

        //  Cerrar la caja al final del turno contando $2500 en fisico
        boolean cerrada = corteCajaService.cerrarCaja(2500.0);
        assertTrue(cerrada, "fallo al intentar cerrar la caja en mysql");

        //  Traemos el corte para comprobar que los datos se guardaron
        CorteCaja corteFinal = corteCajaService.obtenerCorteActual();
        assertEquals(2500.0, corteFinal.getMontoFisico(), "el monto fisico final no cuadra");
        assertEquals(80.0, corteFinal.getRetirosManuales(), "el retiro manual no se acumulo bien");

        System.out.println("-> Prueba 3 Exitosa: El flujo de caja (apertura, retiro y cierre) se proceso perfecto en la BD.");
    }
}