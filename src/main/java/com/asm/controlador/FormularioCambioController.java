package com.asm.controlador;

import com.asm.modelo.DetalleTicketPreview;
import com.asm.servicio.DevolucionesService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class FormularioCambioController {

    private DevolucionesService servicio;

    // --- CONTENEDORES DE LOS PASOS ---
    @FXML private VBox paso1;
    @FXML private VBox paso2;
    @FXML private VBox paso3;
    @FXML private VBox paso4;

    // --- ELEMENTOS DEL PASO 1 y 2 ---
    @FXML private TextField txtFolio;
    @FXML private Label lblInfoTicket;

    // ¡Ojo aquí! Cambiamos String por tu modelo real
    @FXML private ListView<DetalleTicketPreview> listaProductosTicket;

    // --- ELEMENTOS DEL PASO 3 ---
    @FXML private Label lblProductoSale;
    @FXML private ListView<String> listaCatalogo;

    // --- ELEMENTOS DEL PASO 4 ---
    @FXML private Label lblResumenRegresa;
    @FXML private Label lblPrecioRegresa;
    @FXML private Label lblResumenNuevo;
    @FXML private Label lblPrecioNuevo;
    @FXML private Label lblDiferencia;

    // Método para recibir el servicio desde la ventana principal
    public void setServicio(DevolucionesService servicio) {
        this.servicio = servicio;
    }

    @FXML
    public void initialize() {
        mostrarPaso(paso1);
        configurarDisenoListaProductos();
    }

    private void configurarDisenoListaProductos() {
        // Le enseñamos a la lista cómo dibujar la tarjeta igual a tu diseño de Figma
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
                    Label lblCant = new Label("Cantidad: " + item.getCantidad());
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
        String folio = txtFolio.getText();
        if (folio.isEmpty()) {
            System.err.println("Debe ingresar un folio.");
            return;
        }

        // 1. Vamos a MySQL a buscar los detalles del ticket
        List<DetalleTicketPreview> productos = servicio.obtenerDetallesVenta(folio);

        if (productos.isEmpty()) {
            System.err.println("Ticket no encontrado o está vacío.");
            return;
        }

        // 2. Llenamos la pantalla con los datos reales
        lblInfoTicket.setText("Ticket: #" + folio + " | Productos encontrados: " + productos.size());
        listaProductosTicket.getItems().clear();
        listaProductosTicket.getItems().addAll(productos);

        mostrarPaso(paso2);
    }

    @FXML
    public void avanzarPaso3() {
        // Obtenemos qué producto seleccionó el usuario de la lista
        DetalleTicketPreview productoSeleccionado = listaProductosTicket.getSelectionModel().getSelectedItem();

        if(productoSeleccionado == null) {
            System.err.println("Debe seleccionar un producto para cambiar.");
            return;
        }

        // Lo mostramos en la etiqueta amarilla del Paso 3
        lblProductoSale.setText(productoSeleccionado.getNombreProducto() + " - $" + productoSeleccionado.getPrecioUnitario());
        mostrarPaso(paso3);
    }

    @FXML
    public void avanzarPaso4() {
        System.out.println("Calculando diferencias...");
        mostrarPaso(paso4);
    }

    @FXML public void volverPaso1() { mostrarPaso(paso1); }
    @FXML public void volverPaso2() { mostrarPaso(paso2); }
    @FXML public void volverPaso3() { mostrarPaso(paso3); }

    @FXML
    public void generarTicketFinal() {
        System.out.println("🖨️ Imprimiendo ticket negro de cambio...");
        cerrarVentana();
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) txtFolio.getScene().getWindow();
        stage.close();
    }
}