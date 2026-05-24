package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.VentaService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class PuntoVentaController {

    // --- CONEXIONES CON LA INTERFAZ VISUAL (FXML) ---
    @FXML private TilePane contenedorProductos; // El centro de la pantalla
    @FXML private VBox emptyStateCarrito;       // El ícono gris de "No hay artículos"
    @FXML private VBox contenedorCarrito;       // La lista donde irán cayendo los productos
    @FXML private Label lblTotal;               // El texto gigante del total

    // --- VARIABLES DE LÓGICA ---
    private double totalCompra = 0.0;           // Aquí llevamos la cuenta del dinero

    /**
     * Este método se ejecuta automáticamente cuando arranca la ventana
     */
    @FXML
    public void initialize() {
        System.out.println("Cargando el Punto de Venta desde la BD...");
        contenedorProductos.getChildren().clear();

        try {
            // 1. Configuramos y encendemos Hibernate leyendo tu hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            // 2. Registramos las tablas mapeadas
            configuration.addAnnotatedClass(com.asm.modelo.Producto.class);
            configuration.addAnnotatedClass(com.asm.modelo.Venta.class);
            configuration.addAnnotatedClass(com.asm.modelo.DetalleVenta.class);

            // 3. Creamos la conexión y el servicio
            SessionFactory factory = configuration.buildSessionFactory();
            VentaService servicio = new VentaService(factory);

            // 4. Traemos la lista completa de productos
            List<Producto> listaProductos = servicio.obtenerProductos();

            // 5. Dibujamos las tarjetas
            for (Producto prod : listaProductos) {
                // Creamos un SKU provisional usando el ID, ya que no hay SKU en la base de datos
                String skuProvisional = "ID-" + prod.getIdProducto();

                // OJO: Le pasamos el precio directamente como 'double' para poder hacer sumas después
                crearTarjetaProducto(
                        skuProvisional,
                        prod.getNombreProducto(),
                        prod.getPrecio(),
                        prod.getStock()
                );
            }

        } catch (Exception e) {
            System.err.println("Error fatal al cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Dibuja visualmente la tarjeta blanca por cada producto de la BD
     */
    private void crearTarjetaProducto(String sku, String nombre, double precio, int stock) {
        VBox tarjeta = new VBox();
        tarjeta.setPrefSize(200, 150);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setSpacing(10);
        // Le agregamos el cursor de "manita" para que el usuario sepa que le puede dar clic
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-cursor: hand;");

        Label lblNombre = new Label(nombre);
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setWrapText(true);

        Label lblSku = new Label(sku);
        lblSku.setTextFill(javafx.scene.paint.Color.GRAY);

        // Formateamos el precio para que se vea con el signo $ y decimales
        Label lblPrecio = new Label(String.format("$%.2f", precio));
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblPrecio.setTextFill(javafx.scene.paint.Color.web("#1abc9c"));

        Label lblStock = new Label("Stock: " + stock);
        lblStock.setTextFill(javafx.scene.paint.Color.GRAY);
        lblStock.setFont(Font.font("System", 10));

        tarjeta.getChildren().addAll(lblNombre, lblSku, lblPrecio, lblStock);

        // --- LÓGICA DEL CLIC ---
        // Cuando le den clic a esta tarjeta, mandamos los datos al carrito
        tarjeta.setOnMouseClicked(event -> {
            agregarAlCarrito(nombre, precio);
        });

        // Pegamos la tarjeta en la cuadrícula central
        contenedorProductos.getChildren().add(tarjeta);
    }

    /**
     * Toma los datos del producto seleccionado, desaparece el Empty State y actualiza el total
     */
    private void agregarAlCarrito(String nombreProducto, double precio) {
        // 1. Ocultamos el Empty State (El ícono del carrito gris)
        if (emptyStateCarrito != null) {
            emptyStateCarrito.setVisible(false);
            emptyStateCarrito.setManaged(false); // Para que no ocupe espacio invisible
        }

        // 2. Creamos un nuevo renglón para la lista del carrito
        Label item = new Label(nombreProducto + "   ---   " + String.format("$%.2f", precio));
        item.setFont(Font.font("System", 14));
        item.setPadding(new Insets(5, 0, 5, 0));

        // 3. Pegamos el renglón en el panel derecho
        if (contenedorCarrito != null) {
            contenedorCarrito.getChildren().add(item);
        } else {
            System.err.println("¡Cuidado! No enlazaste el fx:id 'contenedorCarrito' en Scene Builder");
        }

        // 4. Sumamos el dinero y actualizamos el gran Total
        totalCompra = totalCompra + precio;
        if (lblTotal != null) {
            lblTotal.setText(String.format("$%.2f", totalCompra));
        } else {
            System.err.println("¡Cuidado! No enlazaste el fx:id 'lblTotal' en Scene Builder");
        }

        System.out.println("✅ Agregado al carrito: " + nombreProducto + " | Nuevo Total: $" + totalCompra);
    }
}