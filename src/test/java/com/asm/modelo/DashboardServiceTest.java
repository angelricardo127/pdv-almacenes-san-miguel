package com.asm.servicio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DashboardServiceTest {

    @Test
    public void testObtenerTotalStockConCeroProductos() {
        // dado que no hemos inyectado una sessionFactory real (por ser prueba unitaria)
        // probamos la logica de respuesta vacia
        DashboardService service = new DashboardService(null);

        // si el servicio esta bien programado, deberia manejar el nulo y regresar 0
        long stock = service.obtenerTotalStock();

        assertEquals(0, stock, "si no hay conexion, el stock debe reportar 0");
    }
}