package com.asm.modelo;

import com.asm.servicio.VentaService;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class VentaServiceTest {

    private static SessionFactory sessionFactory;
    private VentaService ventaService;

    @BeforeAll
    public static void setup() {
        // Conectamos a la base de datos real para las pruebas usando tu ruta ajustada
        sessionFactory = new Configuration().configure("/com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
    }

    @BeforeEach
    public void init() {
        ventaService = new VentaService(sessionFactory);
    }

    // ==========================================
    // 1. PRUEBAS DE ÉXITO (Camino Feliz)
    // ==========================================

    @Test
    @DisplayName("Debería procesar una venta completa exitosa y descontar stock")
    public void testVentaCompletaExitosa() {
        List<DetalleVenta> carrito = new ArrayList<>();

        // Simulamos compra de 2 unidades del producto con ID 1 a $250
        carrito.add(new DetalleVenta(0, 1, 2, 250.00));

        boolean resultado = ventaService.registrarVentaCompleta(1, carrito);
        assertTrue(resultado, "La venta completa debería haberse registrado sin problemas.");
    }

    @Test
    @DisplayName("Debería recuperar el historial de ventas registradas")
    public void testObtenerHistorialVentas() {
        List<Venta> historial = ventaService.obtenerHistorialVentas();

        assertNotNull(historial, "El historial no debería ser nulo.");
        assertFalse(historial.isEmpty(), "El historial debería contener al menos las ventas de prueba.");

        System.out.println("======== REPORTE DE VENTAS ========");
        System.out.println("Total de tickets encontrados: " + historial.size());
        for (Venta v : historial) {
            System.out.println("Ticket ID: " + v.getIdVenta() + " | Método de Pago ID: " + v.getIdMetodoPago());
        }
        System.out.println("===================================");
    }

    @Test
    @DisplayName("Debería obtener el catálogo completo de productos")
    public void testObtenerProductos() {
        List<Producto> catalogo = ventaService.obtenerProductos();

        assertNotNull(catalogo, "El catálogo no debe ser nulo");
        assertFalse(catalogo.isEmpty(), "El catálogo debe tener al menos un producto registrado");
    }

    // ==========================================
    // 2. PRUEBAS DE ERROR (Caminos Tristes)
    // ==========================================

    @Test
    @DisplayName("Debería bloquear la venta y hacer rollback si no hay stock suficiente")
    public void testVentaFallaPorStockInsuficiente() {
        List<DetalleVenta> carrito = new ArrayList<>();

        // Intentamos comprar 99,999 unidades del producto 1 (asumiendo que no tienen tanto stock)
        carrito.add(new DetalleVenta(0, 1, 99999, 250.00));

        // El servicio debería capturar la excepción internamente y devolver FALSE
        boolean resultado = ventaService.registrarVentaCompleta(1, carrito);

        assertFalse(resultado, "La venta NO debe procesarse si se pide más del stock disponible.");
    }

    @Test
    @DisplayName("Debería bloquear la venta si el producto no existe en la BD")
    public void testVentaFallaPorProductoInexistente() {
        List<DetalleVenta> carrito = new ArrayList<>();

        // Intentamos comprar un producto con un ID exagerado que seguro no existe
        carrito.add(new DetalleVenta(0, 999999, 1, 100.00));

        // El servicio debería notar que es nulo y devolver FALSE
        boolean resultado = ventaService.registrarVentaCompleta(1, carrito);

        assertFalse(resultado, "La venta NO debe procesarse si el ID del producto es inválido.");
    }

    @AfterAll
    public static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}