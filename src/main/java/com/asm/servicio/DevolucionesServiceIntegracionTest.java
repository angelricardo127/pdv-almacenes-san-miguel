package com.asm.servicio;

import com.asm.modelo.TicketPreview;
import com.asm.modelo.DetalleTicketPreview;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DevolucionesServiceIntegracionTest {

    private static SessionFactory factory;
    private DevolucionesService devolucionesService;

    // Prendemos la conexión a la base de datos
    @BeforeAll
    public static void iniciarConexion() {
        Configuration config = new Configuration();
        config.configure("/com/asm/vista/hibernate.cfg.xml");

        // Aunque usamos SQL Nativo, inicializamos las clases base para que Hibernate no se queje
        config.addAnnotatedClass(com.asm.modelo.Venta.class);
        config.addAnnotatedClass(com.asm.modelo.Producto.class);
        config.addAnnotatedClass(com.asm.modelo.Usuario.class);

        factory = config.buildSessionFactory();
    }

    @BeforeEach
    public void prepararServicio() {
        devolucionesService = new DevolucionesService(factory);
    }

    @AfterAll
    public static void cerrarConexion() {
        if (factory != null) {
            factory.close();
        }
    }

    // obtenerHistorialTickets()
    @Test
    public void checarObtencionDelHistorialDeTickets() {
        // Ejecutamos el JOIN masivo
        List<TicketPreview> historial = devolucionesService.obtenerHistorialTickets();

        assertNotNull(historial, "El método devolvió null. Revisa tu consulta SQL y la conexión.");
        System.out.println("-> Prueba 1 Exitosa: Se armaron " + historial.size() + " vistas previas de tickets usando SQL Nativo.");
    }

    //  obtenerDetallesVenta(String idVenta)
    @Test
    public void checarObtencionDeDetallesDelTicket() {
        // Consultamos los detalles de la venta ID 1 (como un String, ya que así lo pide tu método)
        List<DetalleTicketPreview> detalles = devolucionesService.obtenerDetallesVenta("24");

        assertNotNull(detalles, "La lista de detalles regresó nula.");
        System.out.println("-> Prueba 2 Exitosa: El ticket 1 tiene " + detalles.size() + " artículos desglosados.");
    }

    //  registrarDevolucion()
    @Test
    public void checarRegistroDeDevolucionEnBaseDeDatos() {
        // Vamos a simular que el cliente devolvió la playera (Producto ID 1) del Ticket 1 por un defecto
        int idVenta = 1;
        int idProducto = 1;
        String motivo = "Talla incorrecta - Cambio de uniforme";
        double montoRetornado = 180.00;

        // Como el metodo es 'void' usamos assertDoesNotThrow para garantizar que el INSERT en MySQL no explote
        assertDoesNotThrow(() -> {
            devolucionesService.registrarDevolucion(idVenta, idProducto, motivo, montoRetornado);
        }, "Hibernate lanzó una excepción al intentar hacer el INSERT en la tabla devolucion.");

        System.out.println("-> Prueba 3 Exitosa: Devolución registrada correctamente en MySQL por: " + motivo);
    }
}