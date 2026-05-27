package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.VentaService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuntoVentaController {

    // --- CONEXIONES CON LA INTERFAZ VISUAL (FXML) ---
    @FXML private TilePane contenedorProductos;
    @FXML private VBox emptyStateCarrito;
    @FXML private VBox contenedorCarrito;
    @FXML private Label lblTotal;
    @FXML private Button btnProcesarPago; // Nuestro nuevo botón conectado

    private double totalCompra = 0.0;

    // --- LA MEMORIA DEL CARRITO ---
    // Guarda el ID del producto y cuántos llevamos (Ej: ID 1 -> 3 piezas)
    private Map<Integer, Integer> cantidadesCarrito = new HashMap<>();
    // Guarda el ID y el objeto Producto completo para poder leer su nombre y precio
    private Map<Integer, Producto> productosCarrito = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("Cargando el Punto de Venta desde la BD...");
        contenedorProductos.getChildren().clear();

        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(com.asm.modelo.Producto.class);
            configuration.addAnnotatedClass(com.asm.modelo.Venta.class);
            configuration.addAnnotatedClass(com.asm.modelo.DetalleVenta.class);

            SessionFactory factory = configuration.buildSessionFactory();
            VentaService servicio = new VentaService(factory);

            List<Producto> listaProductos = servicio.obtenerProductos();

            for (Producto prod : listaProductos) {
                // Ahora le mandamos el objeto Producto completo a la tarjeta
                crearTarjetaProducto(prod);
            }

            // Le decimos al botón de pago qué hacer cuando le den clic
            if (btnProcesarPago != null) {
                btnProcesarPago.setOnAction(event -> procesarPago());
            }

        } catch (Exception e) {
            System.err.println("Error fatal al cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void crearTarjetaProducto(Producto prod) {
        VBox tarjeta = new VBox();
        tarjeta.setPrefSize(200, 150);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setSpacing(10);
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-cursor: hand;");

        Label lblNombre = new Label(prod.getNombreProducto());
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setWrapText(true);

        Label lblSku = new Label("ID-" + prod.getIdProducto());
        lblSku.setTextFill(javafx.scene.paint.Color.GRAY);

        Label lblPrecio = new Label(String.format("$%.2f", prod.getPrecio()));
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblPrecio.setTextFill(javafx.scene.paint.Color.web("#1abc9c"));

        Label lblStock = new Label("Stock: " + prod.getStock());
        lblStock.setTextFill(javafx.scene.paint.Color.GRAY);
        lblStock.setFont(Font.font("System", 10));

        tarjeta.getChildren().addAll(lblNombre, lblSku, lblPrecio, lblStock);

        // Al darle clic, mandamos el objeto Producto a nuestra lógica del carrito
        tarjeta.setOnMouseClicked(event -> {
            agregarAlCarrito(prod);
        });

        contenedorProductos.getChildren().add(tarjeta);
    }

    private void agregarAlCarrito(Producto prod) {
        int id = prod.getIdProducto();

        // Si el producto ya está en el carrito, le sumamos 1. Si es nuevo, empieza en 1.
        cantidadesCarrito.put(id, cantidadesCarrito.getOrDefault(id, 0) + 1);
        productosCarrito.put(id, prod);

        // Redibujamos la lista para que se vean los productos agrupados
        actualizarVistaCarrito();
    }

    private void actualizarVistaCarrito() {
        // 1. Limpiamos la lista visual y el total para recalcular desde cero
        contenedorCarrito.getChildren().clear();
        totalCompra = 0.0;

        // 2. Ocultamos el Empty State porque sabemos que hay artículos
        if (emptyStateCarrito != null) {
            emptyStateCarrito.setVisible(false);
            emptyStateCarrito.setManaged(false);
        }

        // 3. Recorremos nuestra memoria para dibujar los renglones
        for (Integer id : cantidadesCarrito.keySet()) {
            Producto p = productosCarrito.get(id);
            int cantidad = cantidadesCarrito.get(id);
            double subtotal = cantidad * p.getPrecio();

            totalCompra = totalCompra + subtotal;

            // Creamos el renglón agrupado (Ej: "3x Playera Tipo Polo   ---   $750.00")
            Label item = new Label(cantidad + "x " + p.getNombreProducto() + "   ---   " + String.format("$%.2f", subtotal));
            item.setFont(Font.font("System", 14));
            item.setPadding(new Insets(5, 0, 5, 0));

            contenedorCarrito.getChildren().add(item);
        }

        // 4. Actualizamos el texto gigante
        if (lblTotal != null) {
            lblTotal.setText(String.format("$%.2f", totalCompra));
        }
    }

    private void procesarPago() {
        // Si no hay nada, no hacemos nada
        if (cantidadesCarrito.isEmpty()) {
            System.out.println("El carrito está vacío. Agrega productos primero.");
            return;
        }

        System.out.println("💳 Procesando el pago por un total de: $" + totalCompra);
        // NOTA: Aquí es donde conectaremos tu VentaService para descontar el stock en MySQL.

        // Simulamos que la venta fue un éxito y limpiamos el sistema para el siguiente cliente
        cantidadesCarrito.clear();
        productosCarrito.clear();
        actualizarVistaCarrito();

        // Regresamos el Empty State a la pantalla y dejamos el total en ceros
        if (emptyStateCarrito != null) {
            emptyStateCarrito.setVisible(true);
            emptyStateCarrito.setManaged(true);
        }
        lblTotal.setText("$0.00");

        System.out.println(" ¡Venta completada con éxito! Sistema listo para el siguiente cliente.");
    }
}