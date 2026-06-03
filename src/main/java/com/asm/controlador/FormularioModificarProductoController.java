package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.InventarioService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormularioModificarProductoController {

    @FXML private TextField txtSku;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtTalla;
    @FXML private TextField txtColor;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtStockActual;
    @FXML private Label lblEstado;

    private InventarioController controladorPadre;
    private InventarioService servicio;
    private Producto productoEdicion;

    @FXML
    public void initialize() {
        // llenamos la lista con opciones de ejemplo
        cmbCategoria.getItems().addAll("Uniformes Secundaria", "Uniformes Preparatoria", "Deportivos", "Accesorios");
    }

    // el controlador padre llama a esto antes de abrir la ventana
    public void cargarDatosProducto(Producto producto, InventarioController padre, InventarioService servicio) {
        this.productoEdicion = producto;
        this.controladorPadre = padre;
        this.servicio = servicio;

        // ponemos la info actual en las cajitas
        txtSku.setText(producto.getSku());
        txtNombre.setText(producto.getNombreProducto());
        cmbCategoria.setValue(producto.getCategoria());
        txtPrecioVenta.setText(String.valueOf(producto.getPrecio()));
        txtStockActual.setText(String.valueOf(producto.getStock()));

        // calculamos el color de la etiqueta de estado
        actualizarPildoraEstado(producto.getStock());

        // separamos la talla y el color
        if (producto.getVariantes() != null && !producto.getVariantes().isEmpty()) {
            txtTalla.setText(producto.getVariantes());
        }
    }

    private void actualizarPildoraEstado(int stock) {
        String estiloBase = "-fx-font-weight: bold; -fx-padding: 4 15; -fx-background-radius: 20; -fx-font-size: 12px; ";
        if (stock > 10) {
            lblEstado.setText("Disponible");
            lblEstado.setStyle(estiloBase + "-fx-background-color: #E6FFFA; -fx-text-fill: #38A169;");
        } else if (stock > 0) {
            lblEstado.setText("Stock Bajo");
            lblEstado.setStyle(estiloBase + "-fx-background-color: #FFFAF0; -fx-text-fill: #DD6B20;");
        } else {
            lblEstado.setText("Agotado");
            lblEstado.setStyle(estiloBase + "-fx-background-color: #FFF5F5; -fx-text-fill: #E53E3E;");
        }
    }

    @FXML
    public void guardarCambios() {
        try {
            // actualizamos los datos con lo que escribio el usuario
            productoEdicion.setSku(txtSku.getText());
            productoEdicion.setNombreProducto(txtNombre.getText());
            productoEdicion.setCategoria(cmbCategoria.getValue());
            productoEdicion.setPrecio(Double.parseDouble(txtPrecioVenta.getText()));
            productoEdicion.setStock(Integer.parseInt(txtStockActual.getText()));

            // juntamos la talla y color
            String tallaColorUnidos = "Talla: " + txtTalla.getText() + " | Color: " + txtColor.getText();
            productoEdicion.setVariantes(tallaColorUnidos);

            // guardamos los cambios en la base de datos
            servicio.actualizarProducto(productoEdicion);
            System.out.println(" Producto modificado exitosamente");

            // recargamos la tabla y cerramos
            controladorPadre.cargarDatosEnTabla();
            cerrarVentana();

        } catch (NumberFormatException e) {
            System.err.println("error: asegura que el precio y stock sean numeros validos");
        }
    }

    @FXML // metodo para borrar o dar de baja
    public void darDeBaja() {
        System.out.println("dando de baja el producto: " + productoEdicion.getNombreProducto());

        // borramos el producto
        servicio.darDeBajaProducto(productoEdicion);

        // refrescamos la tabla
        controladorPadre.cargarDatosEnTabla();

        // cerramos la ventana
        cerrarVentana();
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) txtSku.getScene().getWindow();
        stage.close();
    }
}