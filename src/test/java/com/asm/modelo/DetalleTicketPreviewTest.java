package com.asm.modelo;

// Importaciones vitales de JUnit 5
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class DetalleTicketPreviewTest {

    // La etiqueta @Test es la que le dice a IntelliJ: "Oye, esto no es código normal, es una prueba que debes ejecutar"
    @Test
    @DisplayName("Verificar que la previsualización del ticket guarde y retorne los datos correctamente")
    public void testCreacionYGetters() { //metodo para la prueba

        // ==========================================
        // 1. ARRANGE (Preparar)
        // ==========================================
        // Inventamos los datos de prueba
        int idEsperado = 5;
        String nombreEsperado = "Pantalón Escolar";
        int cantidadEsperada = 2;
        double precioEsperado = 350.00;
        double subtotalEsperado = 700.00;

        // ==========================================
        // 2. ACT (Actuar)
        // ==========================================
        // Instanciamos el objeto usando el constructor que queremos probar
        DetalleTicketPreview productoPrueba = new DetalleTicketPreview(
                idEsperado,
                nombreEsperado,
                cantidadEsperada,
                precioEsperado,
                subtotalEsperado
        );

        // ==========================================
        // 3. ASSERT (Afirmar)
        // ==========================================
        // Verificamos que lo que el objeto guardó sea exactamente lo que le mandamos.
        // La estructura es: assertEquals(lo_que_yo_espero, lo_que_responde_el_codigo, "Mensaje de error opcional");

        assertEquals(idEsperado, productoPrueba.getIdProducto(), "El ID del producto no coincide");
        assertEquals(nombreEsperado, productoPrueba.getNombreProducto(), "El nombre del producto se guardó mal");
        assertEquals(cantidadEsperada, productoPrueba.getCantidad(), "La cantidad no es la esperada");

        // Para los decimales (double), JUnit nos pide un "margen de error" o delta (0.01) por la forma en que Java maneja los centavos
        assertEquals(precioEsperado, productoPrueba.getPrecioUnitario(), 0.01, "El precio unitario falló");
        assertEquals(subtotalEsperado, productoPrueba.getSubtotal(), 0.01, "El subtotal no es correcto");
    }
}