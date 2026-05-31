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

    // cajas de los pasos
    @FXML private VBox paso1;
    @FXML private VBox paso2;
    @FXML private VBox paso3;
    @FXML private VBox paso4;

    // elementos del paso uno y dos
    @FXML private TextField txtFolio;
    @FXML private Label lblInfoTicket;

    // ojo aqui cambiamos string por tu modelo real
    @FXML private ListView<DetalleTicketPreview> listaProductosTicket;

    // elementos del paso tres
    @FXML private Label lblProductoSale;
    @FXML private ListView<String> listaCatalogo;

    // elementos del paso cuatro
    @FXML private Label lblResumenRegresa;
    @FXML private Label lblPrecioRegresa;
    @FXML private Label lblResumenNuevo;
    @FXML private Label lblPrecioNuevo;
    @FXML private Label lblDiferencia;

    // metodo para recibir el servicio de la ventana principal
    public void setServicio(DevolucionesService servicio) {
        this.servicio = servicio;
    }

    @FXML
    public void initialize() {
        mostrarPaso(paso1);
        configurarDisenoListaProductos();
    }

    private void configurarDisenoListaProductos() {
        // configuramos la lista para que se vea como en figma
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
        // mostramos el paso que queremos y escondemos los demas
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
            System.err.println("debe ingresar un folio");
            return;
        }

        // 1. vamos a mysql a buscar los detalles del ticket
        List<DetalleTicketPreview> productos = servicio.obtenerDetallesVenta(folio);

        if (productos.isEmpty()) {
            System.err.println("ticket no encontrado o esta vacio");
            return;
        }

        // 2. llenamos la pantalla con los datos reales
        lblInfoTicket.setText("Ticket: #" + folio + " | Productos encontrados: " + productos.size());
        listaProductosTicket.getItems().clear();
        listaProductosTicket.getItems().addAll(productos);

        mostrarPaso(paso2);
    }

    @FXML
    public void avanzarPaso3() {
        // vemos que producto selecciono el usuario
        DetalleTicketPreview productoSeleccionado = listaProductosTicket.getSelectionModel().getSelectedItem();

        if(productoSeleccionado == null) {
            System.err.println("debe seleccionar un producto para cambiar");
            return;
        }

        // lo mostramos en la etiqueta del paso tres
        lblProductoSale.setText(productoSeleccionado.getNombreProducto() + " - $" + productoSeleccionado.getPrecioUnitario());
        mostrarPaso(paso3);
    }

    @FXML
    public void avanzarPaso4() {
        // calculando diferencias
        System.out.println("calculando diferencias...");
        mostrarPaso(paso4);
    }

    @FXML public void volverPaso1() { mostrarPaso(paso1); }
    @FXML public void volverPaso2() { mostrarPaso(paso2); }
    @FXML public void volverPaso3() { mostrarPaso(paso3); }

    @FXML
    public void generarTicketFinal() {
        // imprimiendo ticket de cambio
        System.out.println("imprimiendo ticket negro de cambio...");
        cerrarVentana();
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) txtFolio.getScene().getWindow();
        stage.close();
    }
}