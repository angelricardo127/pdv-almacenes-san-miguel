package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.InventarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormularioProductoController {

    // conexiones con la pantalla
    @FXML private TextField txtSku;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtVariantes;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtCostoCompra;
    @FXML private TextField txtStockInicial;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtProveedor;
    @FXML private TextArea txtDescripcion;

    private InventarioController ventanaPrincipal;
    private InventarioService servicio;

    // se ejecuta al cargar la ventana
    @FXML
    public void initialize() {
        // llenamos la lista con las opciones
        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Pantalones", "Camisas", "Zapatos", "Accesorios", "Uniformes Escolares"
        ));
    }

    // recibimos las herramientas de la ventana principal
    public void setDependencias(InventarioController principal, InventarioService srv) {
        this.ventanaPrincipal = principal;
        this.servicio = srv;
    }

    @FXML
    private void guardarProducto() {
        try {
            // revisamos que no falte nada
            if (txtNombre.getText().isEmpty() || cmbCategoria.getValue() == null ||
                    txtPrecioVenta.getText().isEmpty() || txtStockInicial.getText().isEmpty()) {
                mostrarError("Por favor, llena todos los campos obligatorios marcados con *.");
                return;
            }

            // sacamos y acomodamos los datos
            String sku = txtSku.getText();
            String nombre = txtNombre.getText();
            String categoria = cmbCategoria.getValue();
            String variantes = txtVariantes.getText();

            // convertimos los textos a numeros
            double precioVenta = Double.parseDouble(txtPrecioVenta.getText().replace("$", "").trim());

            // datos opcionales
            double costoCompra = txtCostoCompra.getText().isEmpty() ? 0 :
                    Double.parseDouble(txtCostoCompra.getText().replace("$", "").trim());
            int stockInicial = Integer.parseInt(txtStockInicial.getText());
            int stockMinimo = txtStockMinimo.getText().isEmpty() ? 5 :
                    Integer.parseInt(txtStockMinimo.getText());

            String proveedor = txtProveedor.getText();
            String descripcion = txtDescripcion.getText();

            // creamos el producto nuevo
            // datos fijos para que jale la base de datos
            Producto nuevoProducto = new Producto(
                    sku, nombre, categoria, variantes, precioVenta,
                    costoCompra, stockInicial, stockMinimo, proveedor, descripcion, 1, 1
            );

            // guardamos en la base de datos
            boolean exito = servicio.registrarNuevoProducto(nuevoProducto);

            if (exito) {
                System.out.println(" producto '" + nombre + "' registrado con exito!");
                // refrescamos la tabla y cerramos
                ventanaPrincipal.cargarDatosEnTabla();
                cerrarVentana();
            } else {
                mostrarError("No se pudo guardar el producto en la base de datos. Revisa si el SKU ya existe.");
            }

        } catch (NumberFormatException e) {
            mostrarError("Asegurate de poner solo numeros en Precio, Costo y Stocks.\nError tecnico: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Ocurrio un error inesperado al guardar.\nError tecnico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarVentana() {
        // cerramos la ventana
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de captura");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}