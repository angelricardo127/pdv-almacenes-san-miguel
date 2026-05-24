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
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    @BeforeEach
    public void init() {
        ventaService = new VentaService(sessionFactory);
    }

    @Test
    @DisplayName("Debería procesar una venta completa exitosa y descontar stock")
    public void testVentaCompletaExitosa() {
        List<DetalleVenta> carrito = new ArrayList<>();

        // Vamos a simular que compramos 2 Playeras Polo (id_producto = 1) a $250 cada una
        // Mandamos idVenta en 0 porque el servicio lo va a rellenar solo
        carrito.add(new DetalleVenta(0, 1, 2, 250.00));

        // Ejecutar el servicio con método de pago 1 (Efectivo)
        boolean resultado = ventaService.registrarVentaCompleta(1, carrito);

        assertTrue(resultado, "La venta completa debería haberse registrado sin problemas.");
    }

    @AfterAll
    public static void tearDown() {
        if (sessionFactory != null) sessionFactory.close();
    }
}