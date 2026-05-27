package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.InventarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormularioProductoController {

    // --- CONEXIONES FXML (Nuevas matching Figma) ---
    @FXML private TextField txtSku;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbCategoria; // Usamos ComboBox
    @FXML private TextField txtVariantes;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtCostoCompra;
    @FXML private TextField txtStockInicial;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtProveedor;
    @FXML private TextArea txtDescripcion; // Usamos TextArea

    private InventarioController ventanaPrincipal;
    private InventarioService servicio;

    // Se ejecuta automáticamente al cargar la ventana
    @FXML
    public void initialize() {
        // Llenamos el ComboBox con categorías de prueba (esto debería venir de BD luego)
        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Pantalones", "Camisas", "Zapatos", "Accesorios", "Uniformes Escolares"
        ));
    }

    // Recibimos las herramientas de la ventana grande
    public void setDependencias(InventarioController principal, InventarioService srv) {
        this.ventanaPrincipal = principal;
        this.servicio = srv;
    }

    @FXML
    private void guardarProducto() {
        try {
            // 1. VALIDACIÓN BÁSICA (Campos obligatorios * en Figma)
            if (txtNombre.getText().isEmpty() || cmbCategoria.getValue() == null ||
                    txtPrecioVenta.getText().isEmpty() || txtStockInicial.getText().isEmpty()) {
                mostrarError("Por favor, llena todos los campos obligatorios marcados con *.");
                return;
            }

            // 2. EXTRACCIÓN Y CONVERSIÓN DE DATOS
            String sku = txtSku.getText();
            String nombre = txtNombre.getText();
            String categoria = cmbCategoria.getValue();
            String variantes = txtVariantes.getText();

            // Conversión de números (manejando errores)
            double precioVenta = Double.parseDouble(txtPrecioVenta.getText().replace("$", "").trim());

            // Campos opcionales (por defecto 0 si están vacíos)
            double costoCompra = txtCostoCompra.getText().isEmpty() ? 0 :
                    Double.parseDouble(txtCostoCompra.getText().replace("$", "").trim());
            int stockInicial = Integer.parseInt(txtStockInicial.getText());
            int stockMinimo = txtStockMinimo.getText().isEmpty() ? 5 : // 5 por defecto si no ponen
                    Integer.parseInt(txtStockMinimo.getText());

            String proveedor = txtProveedor.getText();
            String descripcion = txtDescripcion.getText();

            // 3. CREACIÓN DEL OBJETO PRODUCTO (Usando el nuevo constructor)
            // Pasamos un 1 directo a idTalla e idGenero para cumplir con la BD de Víctor y Andre
            Producto nuevoProducto = new Producto(
                    sku, nombre, categoria, variantes, precioVenta,
                    costoCompra, stockInicial, stockMinimo, proveedor, descripcion, 1, 1
            );

            // 4. MANDAR A MYSQL USANDO EL SERVICIO
            boolean exito = servicio.registrarNuevoProducto(nuevoProducto);

            if (exito) {
                System.out.println(" ¡Producto '" + nombre + "' registrado con éxito!");
                // Actualizamos la tabla grande y cerramos esta
                ventanaPrincipal.cargarDatosEnTabla();
                cerrarVentana();
            } else {
                mostrarError("No se pudo guardar el producto en la base de datos. Revisa si el SKU ya existe.");
            }

        } catch (NumberFormatException e) {
            mostrarError("Asegúrate de poner solo números en Precio, Costo y Stocks.\nError técnico: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Ocurrió un error inesperado al guardar.\nError técnico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarVentana() {
        // Obtenemos la ventana actual y la cerramos
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