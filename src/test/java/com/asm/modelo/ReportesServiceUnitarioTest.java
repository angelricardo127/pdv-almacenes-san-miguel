package com.asm.modelo;

import com.asm.servicio.ReportesService;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReportesServiceUnitarioTest {

    @Test
    public void testTopUniformesRegresaListaVacia() {
        // pasamos null porque esta funcion aun no toca la base de datos
        ReportesService servicio = new ReportesService(null);

        // ejecutamos el metodo pendiente de los uniformes mas vendidos
        List<Object[]> topUniformes = servicio.obtenerTopProductos();

        // validamos que el sistema no truene y regrese la estructura vacia de forma segura
        assertNotNull(topUniformes, "la lista de uniformes no debe ser nula");
        assertTrue(topUniformes.isEmpty(), "la lista de uniformes debe estar vacia por ahora");
    }

    @Test
    public void testAlertaPorBaseDeDatosCaida() {
        // simulamos que el servidor mysql se apago pasando un motor nulo
        ReportesService servicio = new ReportesService(null);

        // le decimos a java que esperamos que el sistema detecte la falta de conexion
        // y lanze exactamente un nullpointerexception para avisarle al cajero
        assertThrows(NullPointerException.class, () -> {
            servicio.obtenerTotalVentasHoy();
        }, "el sistema debe lanzar una excepcion si no hay conexion a mysql");
    }

    @Test
    public void testBloquearRangoDeFechasIlogico() {
        ReportesService servicio = new ReportesService(null);

        // creamos un rango ilogico: pedir reporte desde junio hasta mayo (al reves)
        LocalDate fechaInicio = LocalDate.of(2026, 6, 1);
        LocalDate fechaFin = LocalDate.of(2026, 5, 1);

        // el sistema deberia bloquear la consulta y lanzar una alerta de argumento
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.obtenerVentasPorRango(fechaInicio, fechaFin);
        }, "el servicio debio bloquear un rango donde el inicio es mayor al fin");
    }
}