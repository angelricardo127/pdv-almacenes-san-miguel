package com.asm.servicio;

import com.asm.modelo.Venta;
import com.asm.modelo.DetalleVenta;
import com.asm.modelo.Producto;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VentaServiceIntegracionTest {

    private static SessionFactory factory;
    private VentaService ventaService;

    // prendemos el motor de base de datos antes de empezar a probar
    @BeforeAll
    public static void iniciarConexion() {
        Configuration config = new Configuration();
        config.configure("/com/asm/vista/hibernate.cfg.xml");

        config.addAnnotatedClass(com.asm.modelo.Venta.class);
        config.addAnnotatedClass(com.asm.modelo.DetalleVenta.class);
        config.addAnnotatedClass(com.asm.modelo.Producto.class);

        factory = config.buildSessionFactory();
    }

    // preparamos el servicio fresquecito antes de correr cada test
    @BeforeEach
    public void prepararServicio() {
        ventaService = new VentaService(factory);
    }

    // apagamos la conexion al final para no dejar procesos colgados en mysql
    @AfterAll
    public static void cerrarConexion() {
        if (factory != null) {
            factory.close();
        }
    }

    // 1. PROBAR: obtenerProductos()
    @Test
    public void checarQueTraigaElCatalogoDeUniformes() {
        List<Producto> catalogo = ventaService.obtenerProductos();
        assertNotNull(catalogo, "El catalogo salio nulo. Revisa la conexion.");
        assertFalse(catalogo.isEmpty(), "La tabla productos esta vacia.");
        System.out.println("-> Prueba 1 Exitosa: Nos trajimos " + catalogo.size() + " uniformes.");
    }

    // 2. PROBAR: obtenerProductoPorId(int id)
    @Test
    public void checarQueExistaElProductoUno() {
        Producto p = ventaService.obtenerProductoPorId(1);
        assertNotNull(p, "No se encontro el producto con el ID 1.");
        System.out.println("-> Prueba 2 Exitosa: El producto 1 es: " + p.getNombreProducto());
    }

    // 3. PROBAR: obtenerHistorialVentas()
    @Test
    public void checarQueTraigaElHistorialDeVentas() {
        List<Venta> historial = ventaService.obtenerHistorialVentas();
        assertNotNull(historial, "Error al consultar el historial de ventas.");
        System.out.println("-> Prueba 3 Exitosa: Se encontraron " + historial.size() + " ventas en el historial.");
    }

    // 4. PROBAR: obtenerDetallesPorVenta(int idVenta)
    @Test
    public void checarQueTraigaDetallesDeUnaVenta() {
        // Buscamos los detalles de la venta ID 1 (asumiendo que ya hay al menos una venta)
        List<DetalleVenta> detalles = ventaService.obtenerDetallesPorVenta(1);
        assertNotNull(detalles, "Error al consultar los detalles de la venta.");
        System.out.println("-> Prueba 4 Exitosa: La venta 1 tiene " + detalles.size() + " articulos comprados.");
    }

    // 5. PROBAR: registrarVentaCompleta(...) - LA PRUEBA REINA
    @Test
    public void checarRegistroDeVentaYDescuentoDeStock() {
        // Creamos un carrito virtual con un producto que sepamos que existe (ID: 1)
        // Le pediremos comprar 1 pieza para no vaciar tu inventario real
        List<DetalleVenta> carritoVirtual = new ArrayList<>();
        DetalleVenta articulo = new DetalleVenta();
        articulo.setIdProducto(1);
        articulo.setCantidad(1);
        articulo.setPrecioUnitario(180.00); // Precio simulado
        carritoVirtual.add(articulo);

        // Consultamos el stock antes de la venta para comparar
        Producto pAntes = ventaService.obtenerProductoPorId(1);
        int stockAntes = pAntes.getStock();

        // Ejecutamos la venta completa en MySQL (Metodo de pago 1 = Efectivo)
        boolean exito = ventaService.registrarVentaCompleta(1, carritoVirtual);

        // Verificamos que Hibernate nos regrese TRUE (Venta exitosa)
        assertTrue(exito, "La venta completa fallo al procesarse en la base de datos.");

        // Consultamos el producto otra vez para comprobar que el stock bajo de verdad
        Producto pDespues = ventaService.obtenerProductoPorId(1);
        assertEquals(stockAntes - 1, pDespues.getStock(), "El stock no se desconto correctamente en MySQL.");

        System.out.println("-> Prueba 5 Exitosa: Venta registrada y stock descontado de " + stockAntes + " a " + pDespues.getStock());
    }
}