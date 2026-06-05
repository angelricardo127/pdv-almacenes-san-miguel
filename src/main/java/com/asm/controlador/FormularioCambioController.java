package com.asm.controlador;

import com.asm.modelo.DetalleTicketPreview;
import com.asm.modelo.Producto;
import com.asm.servicio.DevolucionesService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.List;

public class FormularioCambioController {

    private DevolucionesService servicio;
    private Producto productoNuevoSeleccionado;
    private DetalleTicketPreview productoSaleSeleccionado;

    // Cajas de los pasos
    @FXML private VBox paso1;
    @FXML private VBox paso2;
    @FXML private VBox paso3;
    @FXML private VBox paso4;

    // Elementos del paso 1 y 2 (Búsqueda de Ticket)
    @FXML private TextField txtFolio;
    @FXML private Label lblInfoTicket;
    @FXML private ListView<DetalleTicketPreview> listaProductosTicket;

    // Elementos del paso 3 (Selección de nueva prenda)
    @FXML private Label lblProductoSale;
    @FXML private ListView<Producto> listaCatalogo; // Cambiado a objeto Producto para manejar mejor los datos

    // Elementos del paso 4 (Resumen y Cálculo de Caja)
    @FXML private Label lblResumenRegresa;
    @FXML private Label lblPrecioRegresa;
    @FXML private Label lblResumenNuevo;
    @FXML private Label lblPrecioNuevo;
    @FXML private Label lblDiferencia;

    // Método para recibir el servicio desde la ventana de Gestión
    public void setServicio(DevolucionesService servicio) {
        this.servicio = servicio;
    }

    @FXML
    public void initialize() {
        mostrarPaso(paso1);
        configurarDisenoListaProductos();
        configurarDisenoListaCatalogo();
    }

    // Configura la visualización de los productos que vienen en el ticket original
    private void configurarDisenoListaProductos() {
        listaProductosTicket.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DetalleTicketPreview item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    HBox tarjeta = new HBox(15);
                    tarjeta.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-padding: 15;");

                    Label icono = new Label("📦");
                    icono.setStyle("-fx-font-size: 24px;");

                    VBox info = new VBox(3);
                    Label lblNombre = new Label(item.getNombreProducto());
                    lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #0F172A;");
                    Label lblCant = new Label("Cantidad comprada: " + item.getCantidad());
                    lblCant.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
                    info.getChildren().addAll(lblNombre, lblCant);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Label lblSubtotal = new Label(String.format("$%.2f", item.getSubtotal()));
                    lblSubtotal.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #10B981;");

                    tarjeta.getChildren().addAll(icono, info, spacer, lblSubtotal);
                    setGraphic(tarjeta);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0 0 10 0;");
                }
            }
        });
    }

    // Configura la visualización de las prendas disponibles en el catálogo para el cambio
    private void configurarDisenoListaCatalogo() {
        listaCatalogo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    HBox tarjeta = new HBox(15);
                    tarjeta.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-padding: 12;");

                    VBox info = new VBox(3);
                    Label lblNombre = new Label(item.getNombreProducto());
                    lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Label lblSku = new Label("SKU: " + item.getSku() + " | Disponibles: " + item.getStock());
                    lblSku.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
                    info.getChildren().addAll(lblNombre, lblSku);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Label lblPrecio = new Label(String.format("$%.2f", item.getPrecio()));
                    lblPrecio.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #3B82F6;");

                    tarjeta.getChildren().addAll(info, spacer, lblPrecio);
                    setGraphic(tarjeta);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0 0 8 0;");
                }
            }
        });
    }

    // Manejo de la visibilidad de las capas del asistente
    private void mostrarPaso(VBox pasoMostrar) {
        paso1.setVisible(false); paso1.setManaged(false);
        paso2.setVisible(false); paso2.setManaged(false);
        paso3.setVisible(false); paso3.setManaged(false);
        paso4.setVisible(false); paso4.setManaged(false);

        pasoMostrar.setVisible(true);
        pasoMostrar.setManaged(true);
    }

    @FXML
    public void avanzarPaso2() {
        String folio = txtFolio.getText().trim();
        if (folio.isEmpty()) {
            mostrarAlerta("Campo requerido", "Por favor, ingresa el folio del ticket original.");
            return;
        }

        List<DetalleTicketPreview> productos = servicio.obtenerDetallesVenta(folio);

        if (productos == null || productos.isEmpty()) {
            mostrarAlerta("Ticket no encontrado", "No existe ningún registro de venta con el folio ingresado.");
            return;
        }

        lblInfoTicket.setText("Ticket: #" + folio + " | Productos en el ticket: " + productos.size());
        listaProductosTicket.getItems().clear();
        listaProductosTicket.getItems().addAll(productos);

        mostrarPaso(paso2);
    }

    @FXML
    public void avanzarPaso3() {
        productoSaleSeleccionado = listaProductosTicket.getSelectionModel().getSelectedItem();

        if (productoSaleSeleccionado == null) {
            mostrarAlerta("Selección requerida", "Debes seleccionar qué producto desea regresar el cliente.");
            return;
        }

        lblProductoSale.setText(productoSaleSeleccionado.getNombreProducto() + " ($" + productoSaleSeleccionado.getPrecioUnitario() + ")");

        // Cargar catálogo filtrando únicamente lo que sí tiene stock en Almacén
        listaCatalogo.getItems().clear();
        List<Producto> catalogoCompleto = servicio.obtenerProductosDisponibles();

        if (catalogoCompleto != null) {
            for (Producto prod : catalogoCompleto) {
                // Validación funcional real: solo listar si hay existencias físicas reales
                if (prod.getStock() > 0) {
                    listaCatalogo.getItems().add(prod);
                }
            }
        }

        mostrarPaso(paso3);
    }

    @FXML
    public void avanzarPaso4() {
        productoNuevoSeleccionado = listaCatalogo.getSelectionModel().getSelectedItem();

        if (productoNuevoSeleccionado == null) {
            mostrarAlerta("Selección requerida", "Selecciona el nuevo producto que se llevará el cliente.");
            return;
        }

        // Llenar datos en el resumen final del Paso 4
        lblResumenRegresa.setText(productoSaleSeleccionado.getNombreProducto());
        lblPrecioRegresa.setText(String.format("$%.2f", productoSaleSeleccionado.getPrecioUnitario()));

        lblResumenNuevo.setText(productoNuevoSeleccionado.getNombreProducto());
        lblPrecioNuevo.setText(String.format("$%.2f", productoNuevoSeleccionado.getPrecio()));

        // Calcular balances de caja
        double diferencia = productoNuevoSeleccionado.getPrecio() - productoSaleSeleccionado.getPrecioUnitario();

        if (diferencia > 0) {
            lblDiferencia.setText(String.format("El cliente paga una diferencia de: $%.2f", diferencia));
            lblDiferencia.setStyle("-fx-text-fill: #E53E3E; -fx-font-weight: bold;"); // Texto rojo si falta dinero
        } else if (diferencia < 0) {
            lblDiferencia.setText(String.format("Devolver al cliente en efectivo: $%.2f", Math.abs(diferencia)));
            lblDiferencia.setStyle("-fx-text-fill: #38A169; -fx-font-weight: bold;"); // Texto verde si sobra dinero
        } else {
            lblDiferencia.setText("Cambio de igual valor. No se generan movimientos de caja.");
            lblDiferencia.setStyle("-fx-text-fill: #4A5568; -fx-font-weight: bold;");
        }

        mostrarPaso(paso4);
    }

    @FXML public void volverPaso1() { mostrarPaso(paso1); }
    @FXML public void volverPaso2() { mostrarPaso(paso2); }
    @FXML public void volverPaso3() { mostrarPaso(paso3); }

    @FXML
    public void generarTicketFinal() {
        if (productoSaleSeleccionado == null || productoNuevoSeleccionado == null) {
            mostrarAlerta("Error de proceso", "Faltan datos para poder generar el ticket de cambio.");
            return;
        }

        try {
            // 1. Variables de cálculo
            double precioRegresa = productoSaleSeleccionado.getPrecioUnitario();
            double precioNuevo = productoNuevoSeleccionado.getPrecio();
            double diferencia = precioNuevo - precioRegresa;
            String folioOriginal = txtFolio.getText().trim();

            // 2. Persistencia en la base de datos MySQL mediante Hibernate
            int idVentaInt = Integer.parseInt(folioOriginal);
            String motivo = "Cambio por prenda: " + productoNuevoSeleccionado.getNombreProducto();
            servicio.registrarDevolucion(idVentaInt, productoSaleSeleccionado.getIdProducto(), motivo, diferencia);

            // 3. CARGAR E INVOCAR EL MODAL VISUAL DEL TICKET
            javafx.fxml.FXMLLoader ticketLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/ModalTicket.fxml"));
            javafx.scene.Parent ticketRoot = ticketLoader.load();

            // Recuperamos el controlador del ticket e inyectamos el desglose
            ModalTicketController ticketController = ticketLoader.getController();
            ticketController.cargarDatosCambio(
                    folioOriginal,
                    productoSaleSeleccionado.getNombreProducto(),
                    precioRegresa,
                    productoNuevoSeleccionado.getNombreProducto(),
                    precioNuevo,
                    diferencia
            );

            // Crear y configurar la escena emergente modal
            javafx.stage.Stage ticketStage = new javafx.stage.Stage();
            ticketStage.setTitle("Comprobante de Cambio #" + folioOriginal);
            ticketStage.setScene(new javafx.scene.Scene(ticketRoot));
            ticketStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Forzar a que se muestre el ticket visual en pantalla
            ticketStage.showAndWait();

            // Cerrar la ventana del asistente actual tras concluir
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Datos", "El folio del ticket original no es válido.");
        } catch (Exception e) {
            mostrarAlerta("Error de Carga", "No se pudo desplegar el ticket visual: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) txtFolio.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}