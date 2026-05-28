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
    private Producto productoEdicion; // El producto que estamos modificando

    @FXML
    public void initialize() {
        // Llenamos el ComboBox con opciones de prueba (puedes ajustar esto luego)
        cmbCategoria.getItems().addAll("Uniformes Secundaria", "Uniformes Preparatoria", "Deportivos", "Accesorios");
    }

    // Este método lo llama el InventarioController justo antes de mostrar la ventana
    public void cargarDatosProducto(Producto producto, InventarioController padre, InventarioService servicio) {
        this.productoEdicion = producto;
        this.controladorPadre = padre;
        this.servicio = servicio;

        // Rellenamos los campos con la información actual de la base de datos
        txtSku.setText(producto.getSku());
        txtNombre.setText(producto.getNombreProducto());
        cmbCategoria.setValue(producto.getCategoria());
        txtPrecioVenta.setText(String.valueOf(producto.getPrecio()));
        txtStockActual.setText(String.valueOf(producto.getStock()));

        // Calculamos el estado visual para la píldora
        actualizarPildoraEstado(producto.getStock());

        // Como Talla y Color están guardados juntos en "variantes", los separamos para mostrarlos
        if (producto.getVariantes() != null && !producto.getVariantes().isEmpty()) {
            txtTalla.setText(producto.getVariantes());
            // Podrías usar un split("-") si los guardaste con un guión para separar talla y color
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
            // 1. Actualizamos el objeto con lo que el usuario escribió
            productoEdicion.setSku(txtSku.getText());
            productoEdicion.setNombreProducto(txtNombre.getText());
            productoEdicion.setCategoria(cmbCategoria.getValue());
            productoEdicion.setPrecio(Double.parseDouble(txtPrecioVenta.getText()));
            productoEdicion.setStock(Integer.parseInt(txtStockActual.getText()));

            // Unimos Talla y Color en el campo "variantes" para respetar el script MySQL original
            String tallaColorUnidos = "Talla: " + txtTalla.getText() + " | Color: " + txtColor.getText();
            productoEdicion.setVariantes(tallaColorUnidos);

            // llamada al metodo de tu servicio para hacer el UPDATE en Hibernate
            servicio.actualizarProducto(productoEdicion);
            System.out.println(" Producto modificado exitosamente");

            // 3. Le avisamos a la tabla principal que se recargue y cerramos
            controladorPadre.cargarDatosEnTabla();
            cerrarVentana();

        } catch (NumberFormatException e) {
            System.err.println("Error: Asegúrate de que el precio y stock sean números válidos.");
        }
    }

    @FXML
    public void darDeBaja() {
        // Aquí iría la lógica para borrar o desactivar el producto (DELETE)
        System.out.println(" Producto enviado a la papelera: " + productoEdicion.getNombreProducto());
        // servicio.eliminarProducto(productoEdicion.getIdProducto());
        controladorPadre.cargarDatosEnTabla();
        cerrarVentana();
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) txtSku.getScene().getWindow();
        stage.close();
    }
}