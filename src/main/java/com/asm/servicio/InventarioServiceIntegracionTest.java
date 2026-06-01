package com.asm.servicio;

import com.asm.modelo.Producto;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventarioServiceIntegracionTest {

    private static SessionFactory factory;
    private InventarioService inventarioService;

    // Prendemos la conexión a la base de datos
    @BeforeAll
    public static void iniciarConexion() {
        Configuration config = new Configuration();
        config.configure("/com/asm/vista/hibernate.cfg.xml");
        config.addAnnotatedClass(com.asm.modelo.Producto.class);
        factory = config.buildSessionFactory();
    }

    @BeforeEach
    public void prepararServicio() {
        inventarioService = new InventarioService(factory);
    }

    @AfterAll
    public static void cerrarConexion() {
        if (factory != null) {
            factory.close();
        }
    }

    // 1. PROBAR: registrarNuevoProducto()
    @Test
    public void checarRegistroDeNuevoProducto() {
        // Creamos un producto de prueba para no ensuciar tu catálogo real
        Producto nuevoArticulo = new Producto();
        nuevoArticulo.setNombreProducto("Corbata Escolar Prueba");
        nuevoArticulo.setPrecio(85.50);
        nuevoArticulo.setStock(20);
        nuevoArticulo.setActivo(true);
        //  AGREGAMOS LOS CAMPOS OBLIGATORIOS
        nuevoArticulo.setIdTalla(1);
        nuevoArticulo.setIdGenero(1);

        boolean exito = inventarioService.registrarNuevoProducto(nuevoArticulo);

        assertTrue(exito, "Falló el registro del nuevo producto en la base de datos.");
        System.out.println("-> Prueba 1 Exitosa: Producto insertado en MySQL con éxito.");
    }
    // 2. PROBAR: obtenerCatalogoCompleto()
    @Test
    public void checarObtencionDelCatalogoActivo() {
        List<Producto> catalogo = inventarioService.obtenerCatalogoCompleto();

        assertNotNull(catalogo, "El catálogo devolvió un error de conexión (null).");
        assertFalse(catalogo.isEmpty(), "No se trajo ningún producto activo.");
        System.out.println("-> Prueba 2 Exitosa: Se recuperaron " + catalogo.size() + " productos activos.");
    }

    // 3. PROBAR: agregarStock()
    @Test
    public void checarAgregadoDeStockEnProductoExistente() {
        // Le agregamos 5 unidades al producto con ID 1
        boolean exito = inventarioService.agregarStock(1, 5);

        assertTrue(exito, "Ocurrió un error al intentar sumar el stock.");
        System.out.println("-> Prueba 3 Exitosa: Se sumaron 5 unidades al stock del producto 1.");
    }

    // 4. PROBAR: actualizarProducto()
    @Test
    public void checarActualizacionDeProducto() {
        // Obtenemos el primer producto que haya en el catálogo activo para probar
        List<Producto> catalogo = inventarioService.obtenerCatalogoCompleto();
        Producto productoPrueba = catalogo.get(0);

        // Le modificamos el precio ligeramente
        double precioOriginal = productoPrueba.getPrecio();
        productoPrueba.setPrecio(precioOriginal + 1.0);

        // No hay assert porque el método es 'void', si no truena una excepción, pasó.
        assertDoesNotThrow(() -> {
            inventarioService.actualizarProducto(productoPrueba);
        }, "Hibernate lanzó una excepción al hacer el UPDATE.");

        System.out.println("-> Prueba 4 Exitosa: Precio del producto " + productoPrueba.getIdProducto() + " actualizado.");
    }

    // 5. PROBAR: darDeBajaProducto() (Soft Delete)
    @Test
    public void checarBajaLogicaDeProducto() {
        // Para no desaparecer tu producto principal (ID 1), buscamos el de "Corbata" que acabamos de crear en la Prueba 1
        List<Producto> catalogo = inventarioService.obtenerCatalogoCompleto();
        Producto productoABorrar = catalogo.stream()
                .filter(p -> p.getNombreProducto().contains("Prueba"))
                .findFirst()
                .orElse(null);

        assertNotNull(productoABorrar, "No se encontró el producto de prueba para dar de baja.");

        // Ejecutamos la baja lógica
        inventarioService.darDeBajaProducto(productoABorrar);

        assertFalse(productoABorrar.getActivo(), "El switch 'activo' no cambió a false.");
        System.out.println("-> Prueba 5 Exitosa: Baja lógica (Soft Delete) aplicada correctamente al producto.");
    }
}