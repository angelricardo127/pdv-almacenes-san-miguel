package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.InventarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class FormularioVariantesController {

    // cajas de los pasos
    @FXML private VBox paso1;
    @FXML private VBox paso2;

    // cosas del paso uno
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colSku;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Void> colAccion;

    // cosas del paso dos
    @FXML private Label lblNombreProducto;
    @FXML private Label lblDetallesProducto;
    @FXML private TextField txtTalla;
    @FXML private TextField txtColor;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private Label lblVariantesAcumuladas;

    private InventarioController controladorPadre;
    private InventarioService servicio;
    private Producto productoSeleccionado;
    private StringBuilder bufferVariantes;

    // metodo para recibir el servicio de la ventana principal
    public void setDependencias(InventarioController padre, InventarioService servicio) {
        this.controladorPadre = padre;
        this.servicio = servicio;
        this.bufferVariantes = new StringBuilder();

        configurarTabla();
        cargarProductosEnTabla();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        // boton para seleccionar en la tabla
        colAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btnSeleccionar = new Button("Seleccionar");
            {
                btnSeleccionar.setStyle("-fx-background-color: #9F7AEA; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-weight: bold;");
                btnSeleccionar.setOnAction(event -> {
                    productoSeleccionado = getTableView().getItems().get(getIndex());
                    avanzarAlPaso2();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else { setGraphic(btnSeleccionar); setStyle("-fx-alignment: center;"); }
            }
        });
    }

    private void cargarProductosEnTabla() {
        // traemos todos los productos de la base de datos
        List<Producto> productos = servicio.obtenerCatalogoCompleto();
        if (productos != null) {
            ObservableList<Producto> lista = FXCollections.observableArrayList(productos);
            tablaProductos.setItems(lista);
        }
    }

    private void avanzarAlPaso2() {
        // llenamos los textos de la tarjeta
        lblNombreProducto.setText(productoSeleccionado.getNombreProducto());
        lblDetallesProducto.setText("SKU: " + productoSeleccionado.getSku() + " • Categoría: " + productoSeleccionado.getCategoria());

        // si el producto ya tenia variantes las cargamos
        if (productoSeleccionado.getVariantes() != null) {
            bufferVariantes = new StringBuilder(productoSeleccionado.getVariantes());
            lblVariantesAcumuladas.setText("Variantes actuales:\n" + bufferVariantes.toString());
        } else {
            bufferVariantes = new StringBuilder();
            lblVariantesAcumuladas.setText("Variantes por guardar: Ninguna");
        }

        // cambiamos de paso
        paso1.setVisible(false);
        paso1.setManaged(false);
        paso2.setVisible(true);
        paso2.setManaged(true);
    }

    @FXML
    public void volverAlPaso1() {
        paso2.setVisible(false);
        paso2.setManaged(false);
        paso1.setVisible(true);
        paso1.setManaged(true);
        productoSeleccionado = null;
    }

    @FXML
    public void agregarVariante() {
        // juntamos los datos
        String nueva = String.format("[%s - %s | $%s | Stock: %s]",
                txtTalla.getText(), txtColor.getText(), txtPrecio.getText(), txtStock.getText());

        if (bufferVariantes.length() > 0) {
            bufferVariantes.append(", ");
        }
        bufferVariantes.append(nueva);

        // actualizamos la etiqueta para que se vea el cambio
        lblVariantesAcumuladas.setText("Variantes acumuladas:\n" + bufferVariantes.toString());

        // limpiamos los campos
        txtTalla.clear(); txtColor.clear(); txtPrecio.clear(); txtStock.clear();
    }

    @FXML
    public void guardarEnBaseDeDatos() {
        if (productoSeleccionado != null) {
            // guardamos el texto en el objeto
            productoSeleccionado.setVariantes(bufferVariantes.toString());

            // mandamos a guardar
            servicio.actualizarProducto(productoSeleccionado);
            System.out.println("✅ Variantes guardadas en texto: " + bufferVariantes.toString());

            controladorPadre.cargarDatosEnTabla();
            cerrarVentana();
        }
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) paso1.getScene().getWindow();
        stage.close();
    }
}