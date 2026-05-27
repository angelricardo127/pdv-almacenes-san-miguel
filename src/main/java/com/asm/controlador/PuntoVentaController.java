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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class PuntoVentaController {

    // --- CONEXIONES CON LA INTERFAZ VISUAL (FXML) ---
    @FXML
    private TilePane contenedorProductos;
    @FXML
    private VBox emptyStateCarrito;
    @FXML
    private VBox contenedorCarrito;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnProcesarPago; // Nuestro nuevo botón

    private double totalCompra = 0.0;
    @FXML private TextField txtBuscarProducto;
    private List<Producto> listaProductos;


    // --- LA MEMORIA DEL CARRITO ---
    // Guarda el ID del producto y cuántos llevamos (Ej: ID 1 -> 3 piezas)
    private Map<Integer, Integer> cantidadesCarrito = new HashMap<>();
    // Guarda el ID y el objeto Producto completo para poder leer su nombre y precio
    private Map<Integer, Producto> productosCarrito = new HashMap<>();
    private VentaService servicioVentas;
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
            this.servicioVentas = new VentaService(factory);

            this.listaProductos = servicioVentas.obtenerProductos();
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

        try {
            // 1. Cargar el diseño de la nueva ventanita
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModalPago.fxml"));
            Parent root = loader.load();

            // 2. Obtener el cerebro del Modal para pasarle los datos
            ModalPagoController modalController = loader.getController();
            modalController.setTotalAPagar(totalCompra);

            // 3. Crear la ventana flotante
            Stage modalStage = new Stage();
            modalStage.setTitle("Procesar Pago");
            modalStage.setScene(new Scene(root));

            // 4. Hacer que bloquee la pantalla de atrás (MODAL)
            modalStage.initModality(Modality.APPLICATION_MODAL);
            // modalStage.setResizable(false); // Para que no le cambien el tamaño

            // 5. Mostrar la ventanita y ESPERAR a que el usuario la cierre
            modalStage.showAndWait();

            // 6. Cuando la ventana se cierra, le preguntamos si el pago fue exitoso
            if (modalController.isPagoAprobado()) {
                System.out.println("✅ ¡Venta confirmada! Abriendo ticket...");

                // Calculamos el cambio final pidiéndole el dinero recibido a la ventanita
                double recibido = modalController.getMontoRecibido();
                double cambio = recibido - totalCompra;

                // --- GUARDAR EN BASE DE DATOS MIENTRAS SE IMPRIME EL TICKET ---
                servicioVentas.registrarVenta(cantidadesCarrito, totalCompra);

                // --- MAGIA DEL TICKET ---
                FXMLLoader ticketLoader = new FXMLLoader(getClass().getResource("/ModalTicket.fxml"));
                Parent ticketRoot = ticketLoader.load();

                ModalTicketController ticketController = ticketLoader.getController();
                // Le inyectamos el carrito completo, los totales y el cambio
                ticketController.cargarDatosTicket(cantidadesCarrito, productosCarrito, totalCompra, recibido, cambio, "Efectivo");

                Stage ticketStage = new Stage();
                ticketStage.setTitle("Ticket de Venta");
                ticketStage.setScene(new Scene(ticketRoot));
                ticketStage.initModality(Modality.APPLICATION_MODAL);

                // Mostramos el ticket y ESPERAMOS a que el cajero lo cierre
                ticketStage.showAndWait();

                // --- LIMPIEZA DEL CARRITO ---
                // Una vez que el cajero termina de ver/imprimir el ticket, limpiamos el sistema
                cantidadesCarrito.clear();
                productosCarrito.clear();
                actualizarVistaCarrito();

                if (emptyStateCarrito != null) {
                    emptyStateCarrito.setVisible(true);
                    emptyStateCarrito.setManaged(true);
                }
                lblTotal.setText("$0.00");

            } else {
                System.out.println("❌ Pago cancelado por el cajero.");
            }
        } catch (Exception e) {
            System.err.println("Error al abrir la ventana de pago: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void filtrarProductos(String busqueda) {
        // 1. Limpiamos las tarjetas actuales usando tu nombre real
        contenedorProductos.getChildren().clear();

        String busquedaMinusculas = busqueda.toLowerCase();

        // 2. Volvemos a dibujar solo los que coincidan con el texto
        for (Producto prod : listaProductos) {
            if (prod.getNombreProducto().toLowerCase().contains(busquedaMinusculas)) {
                crearTarjetaProducto(prod);
            }
        }
    }
    @FXML
    private void abrirVentanaReimprimir() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ModalReimprimir.fxml"));
            javafx.scene.Parent root = loader.load();
            // --- ESTAS DOS LÍNEAS SON LA MAGIA ---
            // Le prestamos nuestro servicio de base de datos a la ventanita morada
            ModalReimprimirController controller = loader.getController();
            controller.setServicioVentas(this.servicioVentas);
            // -------------------------------------

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Reimprimir Ticket");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al abrir la ventana de reimpresión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}