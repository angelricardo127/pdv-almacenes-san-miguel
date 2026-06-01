package com.asm.servicio;

import com.asm.modelo.Usuario;
import com.asm.modelo.Producto;
import com.asm.modelo.Venta;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardServiceIntegrationTest {

    private static SessionFactory factory;
    private DashboardService dashboardService;

    // prendemos el motor de base de datos antes de empezar a probar
    @BeforeAll
    public static void iniciarConexion() {
        Configuration config = new Configuration();
        // apuntamos directo al archivo de configuracion
        config.configure("/com/asm/vista/hibernate.cfg.xml");

        // le avisamos a hibernate que tablas vamos a usar en el dashboard
        config.addAnnotatedClass(com.asm.modelo.Usuario.class);
        config.addAnnotatedClass(com.asm.modelo.Producto.class);
        config.addAnnotatedClass(com.asm.modelo.Venta.class);

        factory = config.buildSessionFactory();
    }

    // preparamos el servicio fresquecito antes de correr cada test
    @BeforeEach
    public void prepararServicio() {
        dashboardService = new DashboardService(factory);
    }

    // apagamos la conexion al final para no dejar procesos colgados en sql server
    @AfterAll
    public static void cerrarConexion() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    public void checarQueCuenteUsuariosActivos() {
        long totalUsuarios = dashboardService.obtenerTotalUsuariosActivos();

        // validamos que devuelva un valor real (no negativo)
        assertTrue(totalUsuarios >= 0, "El total de usuarios activos no puede ser negativo.");

        System.out.println("-> Prueba 1 Exitosa: Tenemos " + totalUsuarios + " cajeros/usuarios activos en el sistema.");
    }


    @Test
    public void checarQueSumeStockTotal() {
        long totalStock = dashboardService.obtenerTotalStock();

        assertTrue(totalStock >= 0, "El stock fisico no puede ser negativo.");
        System.out.println("-> Prueba 2 Exitosa: El inventario total de uniformes es de " + totalStock + " piezas.");
    }


    @Test
    public void checarQueCuenteVentasDelDia() {
        long ventasHoy = dashboardService.obtenerVentasDelDia();

        assertTrue(ventasHoy >= 0, "Las ventas del dia no pueden ser negativas.");
        System.out.println("-> Prueba 3 Exitosa: Hoy se han realizado " + ventasHoy + " tickets de venta.");
    }
}