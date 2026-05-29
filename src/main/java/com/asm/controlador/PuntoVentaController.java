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
    @FXML private TilePane contenedorProductos;
    @FXML private VBox emptyStateCarrito;
    @FXML private VBox contenedorCarrito;
    @FXML private Label lblTotal;
    @FXML private Button btnProcesarPago;
    @FXML private TextField txtBuscarProducto;

    private double totalCompra = 0.0;
    private List<Producto> listaProductos;

    // --- LA MEMORIA DEL CARRITO ---
    private Map<Integer, Integer> cantidadesCarrito = new HashMap<>();
    private Map<Integer, Producto> productosCarrito = new HashMap<>();
    private VentaService servicioVentas;

    @FXML
    public void initialize() {
        System.out.println("Cargando el Punto de Venta desde la BD...");
        contenedorProductos.getChildren().clear();

        try {
            Configuration configuration = new Configuration();
            configuration.configure("com/asm/vista/hibernate.cfg.xml");
            configuration.addAnnotatedClass(com.asm.modelo.Producto.class);
            configuration.addAnnotatedClass(com.asm.modelo.Venta.class);
            configuration.addAnnotatedClass(com.asm.modelo.DetalleVenta.class);

            SessionFactory factory = configuration.buildSessionFactory();
            this.servicioVentas = new VentaService(factory);

            this.listaProductos = servicioVentas.obtenerProductos();
            for (Producto prod : listaProductos) {
                crearTarjetaProducto(prod);
            }

            // --- LÓGICA DEL BOTÓN DE PAGO (Hover y Click) ---
            if (btnProcesarPago != null) {
                btnProcesarPago.setOnAction(event -> procesarPago());
                actualizarEstadoBotonPago(); // Lo apagamos de inicio

                // Efecto Hover
                btnProcesarPago.setOnMouseEntered(e -> {
                    if (!btnProcesarPago.isDisable()) {
                        btnProcesarPago.setStyle("-fx-background-color: #149178; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;");
                    }
                });

                // Se apaga al quitar el mouse
                btnProcesarPago.setOnMouseExited(e -> {
                    if (!btnProcesarPago.isDisable()) {
                        btnProcesarPago.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold;");
                    }
                });
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

        tarjeta.setOnMouseClicked(event -> agregarAlCarrito(prod));
        contenedorProductos.getChildren().add(tarjeta);
    }

    private void agregarAlCarrito(Producto prod) {
        int id = prod.getIdProducto();
        cantidadesCarrito.put(id, cantidadesCarrito.getOrDefault(id, 0) + 1);
        productosCarrito.put(id, prod);
        actualizarVistaCarrito();
    }

    public void cambiarCantidadProducto(Producto p, int cambio) {
        int id = p.getIdProducto();
        int nuevaCantidad = cantidadesCarrito.getOrDefault(id, 0) + cambio;

        if (nuevaCantidad <= 0) {
            eliminarProductoDelCarrito(p);
        } else {
            cantidadesCarrito.put(id, nuevaCantidad);
            actualizarVistaCarrito();
        }
    }

    public void eliminarProductoDelCarrito(Producto p) {
        int id = p.getIdProducto();
        cantidadesCarrito.remove(id);
        productosCarrito.remove(id);
        actualizarVistaCarrito();
    }

    private void actualizarVistaCarrito() {
        contenedorCarrito.getChildren().clear();
        totalCompra = 0.0;

        boolean carritoVacio = cantidadesCarrito.isEmpty();

        if (emptyStateCarrito != null) {
            emptyStateCarrito.setVisible(carritoVacio);
            emptyStateCarrito.setManaged(carritoVacio);
        }

        for (Integer id : cantidadesCarrito.keySet()) {
            Producto p = productosCarrito.get(id);
            int cantidad = cantidadesCarrito.get(id);
            double subtotal = cantidad * p.getPrecio();
            totalCompra += subtotal;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ElementoCarrito.fxml"));
                Parent filaProducto = loader.load();

                ElementoCarritoController controller = loader.getController();
                controller.configurarElemento(p, cantidad, this);

                contenedorCarrito.getChildren().add(filaProducto);
            } catch (Exception e) {
                System.err.println("Error al cargar diseño del carrito: " + e.getMessage());
            }
        }

        if (lblTotal != null) {
            lblTotal.setText(String.format("$%.2f", totalCompra));
        }

        actualizarEstadoBotonPago();
    }

    private void actualizarEstadoBotonPago() {
        if (btnProcesarPago != null) {
            boolean carritoVacio = cantidadesCarrito.isEmpty();
            btnProcesarPago.setDisable(carritoVacio);

            if (carritoVacio) {
                btnProcesarPago.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: #7f8c8d; -fx-background-radius: 6; -fx-font-weight: bold;");
            } else {
                btnProcesarPago.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold;");
            }
        }
    }

    private void procesarPago() {
        if (cantidadesCarrito.isEmpty()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModalPago.fxml"));
            Parent root = loader.load();

            ModalPagoController modalController = loader.getController();
            modalController.setTotalAPagar(totalCompra);

            Stage modalStage = new Stage();
            modalStage.setTitle("Procesar Pago");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.showAndWait();

            if (modalController.isPagoAprobado()) {
                double recibido = modalController.getMontoRecibido();
                double cambio = recibido - totalCompra;

                servicioVentas.registrarVenta(cantidadesCarrito, totalCompra);

                FXMLLoader ticketLoader = new FXMLLoader(getClass().getResource("/ModalTicket.fxml"));
                Parent ticketRoot = ticketLoader.load();

                ModalTicketController ticketController = ticketLoader.getController();
                ticketController.cargarDatosTicket(cantidadesCarrito, productosCarrito, totalCompra, recibido, cambio, modalController.getMetodoPagoFinal());
                Stage ticketStage = new Stage();
                ticketStage.setTitle("Ticket de Venta");
                ticketStage.setScene(new Scene(ticketRoot));
                ticketStage.initModality(Modality.APPLICATION_MODAL);
                ticketStage.showAndWait();

                cantidadesCarrito.clear();
                productosCarrito.clear();
                actualizarVistaCarrito();

                System.out.println(" ¡Venta completada con éxito! Sistema listo para el siguiente cliente.");
            }
        } catch (Exception e) {
            System.err.println("Error al abrir la ventana de pago: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filtrarProductos(String busqueda) {
        contenedorProductos.getChildren().clear();
        String busquedaMinusculas = busqueda.toLowerCase();

        for (Producto prod : listaProductos) {
            if (prod.getNombreProducto().toLowerCase().contains(busquedaMinusculas)) {
                crearTarjetaProducto(prod);
            }
        }
    }

    @FXML
    private void abrirVentanaReimprimir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModalReimprimir.fxml"));
            Parent root = loader.load();

            ModalReimprimirController controller = loader.getController();
            controller.setServicioVentas(this.servicioVentas);

            Stage stage = new Stage();
            stage.setTitle("Reimprimir Ticket");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al abrir la ventana de reimpresión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}