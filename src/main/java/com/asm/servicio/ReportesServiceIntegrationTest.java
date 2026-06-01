package com.asm.servicio;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportesServiceIntegrationTest {

    private static SessionFactory factory;
    private ReportesService reportesService;

    // prendemos el motor de base de datos antes de empezar a probar
    @BeforeAll
    public static void iniciarConexion() {
        Configuration config = new Configuration();
        config.configure("/com/asm/vista/hibernate.cfg.xml");

        // registramos todas las tablas que cruzan en tus hql de reportes
        config.addAnnotatedClass(com.asm.modelo.Venta.class);
        config.addAnnotatedClass(com.asm.modelo.Usuario.class);
        config.addAnnotatedClass(com.asm.modelo.MetodoPago.class);
        config.addAnnotatedClass(com.asm.modelo.DetalleVenta.class);

        factory = config.buildSessionFactory();
    }


    @BeforeEach
    public void prepararServicio() {
        reportesService = new ReportesService(factory);
    }

    // apagamos la conexion al final para no dejar procesos colgados
    @AfterAll
    public static void cerrarConexion() {
        if (factory != null) {
            factory.close();
        }
    }


    @Test
    public void checarQueTraigaReportePorFechas() {
        LocalDate fechaInicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fechaFin = LocalDate.now();

        List<Object[]> reporte = reportesService.obtenerVentasPorRango(fechaInicio, fechaFin);


        assertNotNull(reporte, "el reporte por fechas salio nulo. revisa la conexion.");
        System.out.println("-> Prueba 1 Exitosa: Se encontraron " + reporte.size() + " ventas en este rango de fechas.");
    }


    @Test
    public void checarCorteDeCajaDelDia() {
        double totalHoy = reportesService.obtenerTotalVentasHoy();

        // validamos que devuelva dinero real incluso si no han vendido nada, devuelve 0.0
        assertTrue(totalHoy >= 0.0, "el total de ventas de hoy no puede ser negativo.");
        System.out.println("-> Prueba 2 Exitosa: El dinero recaudado el dia de hoy es de $" + totalHoy);
    }


    @Test
    public void checarTopCajeros() {
        List<Object[]> topCajeros = reportesService.obtenerRendimientoCajeros();

        // validamos que no truene la consulta avanzada con group by
        assertNotNull(topCajeros, "error al consultar el rendimiento de los cajeros.");

        // comprobamos que nunca pase de ese limite logico
        assertTrue(topCajeros.size() <= 5, "el top no debe traer mas de 5 cajeros.");
        System.out.println("-> Prueba 3 Exitosa: Se recupero el ranking de los " + topCajeros.size() + " mejores cajeros.");
    }
}