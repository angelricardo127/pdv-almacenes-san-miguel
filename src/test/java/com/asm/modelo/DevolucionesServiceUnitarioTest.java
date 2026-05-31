package com.asm.modelo;


import com.asm.servicio.DevolucionesService;
import com.asm.modelo.DetalleTicketPreview;
import com.asm.modelo.TicketPreview;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DevolucionesServiceUnitarioTest {

    @Test
    public void testHistorialManejaErrorSinConexion() {
        // pasamos un null al servicio simulando que el servidor mysql esta caido
        DevolucionesService servicio = new DevolucionesService(null);

        // ejecutamos el metodo
        List<TicketPreview> historial = servicio.obtenerHistorialTickets();

        assertNotNull(historial, "la lista no debe ser nula");
        assertEquals(0, historial.size(), "la lista debe estar vacia si falla la conexion");
    }

    @Test
    public void testDetallesManejaDatosInvalidos() {
        // simulamos el servicio sin base de datos
        DevolucionesService servicio = new DevolucionesService(null);

        // pedimos los detalles enviando una palabra en lugar de un id numerico
        List<DetalleTicketPreview> detalles = servicio.obtenerDetallesVenta("id-falso-abc");

        // validamos que tu sistema atrape el error de sql y devuelva la lista vacia
        assertTrue(detalles.isEmpty(), "el sistema debe protegerse y regresar lista vacia");
    }

    @Test
    public void testRechazarMontoNegativoEnDevolucion() {
        DevolucionesService servicio = new DevolucionesService(null);

        // intentamos hacer una devolucion de fraude con un monto negativo (-500.0)
        // usamos assertthrows para decirle a java que esperamos que este metodo
        // lanze una alerta de seguridad (illegalargumentexception) bloqueando la operacion.
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarDevolucion(10, 5, "producto dañado", -500.0);
        }, "el servicio debio bloquear el monto negativo");
    }
}