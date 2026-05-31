package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.InventarioService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class FormularioAjusteStockController {

    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private ComboBox<String> cmbTipoAjuste;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtMotivo;

    private InventarioController controladorPadre;
    private InventarioService servicio;

    @FXML
    public void initialize() {
        // configuramos las opciones del tipo de ajuste
        cmbTipoAjuste.getItems().addAll("Entrada (Suma al stock)", "Salida (Resta al stock)", "Merma / Daño (Resta al stock)");

        // configuramos como se van a ver los productos en la lista
        cmbProducto.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto producto) {
                if (producto == null) return null;
                return producto.getSku() + " - " + producto.getNombreProducto();
            }

            @Override
            public Producto fromString(String string) {
                return null; // no hace falta para este caso
            }
        });
    }

    public void setDependencias(InventarioController padre, InventarioService servicio) {
        this.controladorPadre = padre;
        this.servicio = servicio;
        cargarProductosEnLista();
    }

    private void cargarProductosEnLista() {
        // traemos todos los productos de la base de datos y los ponemos en la lista
        List<Producto> productos = servicio.obtenerCatalogoCompleto();
        if (productos != null) {
            cmbProducto.getItems().addAll(productos);
        }
    }

    @FXML
    public void aplicarAjuste() {
        try {
            Producto productoSeleccionado = cmbProducto.getValue();
            String tipoAjuste = cmbTipoAjuste.getValue();
            int cantidadAjuste = Integer.parseInt(txtCantidad.getText());

            if (productoSeleccionado == null || tipoAjuste == null || cantidadAjuste <= 0) {
                System.err.println("Por favor llena todos los campos correctamente.");
                return;
            }

            // hacemos las cuentas segun el tipo de ajuste
            int stockActual = productoSeleccionado.getStock();

            if (tipoAjuste.contains("Entrada")) {
                productoSeleccionado.setStock(stockActual + cantidadAjuste);
            } else {
                // para salidas o mermas restamos y revisamos que no quede negativo
                if (stockActual - cantidadAjuste < 0) {
                    System.err.println("No puedes restar más stock del que existe.");
                    return;
                }
                productoSeleccionado.setStock(stockActual - cantidadAjuste);
            }

            // pendiente: guardar el motivo en el historial

            // mandamos la actualizacion a la base de datos
            servicio.actualizarProducto(productoSeleccionado);
            System.out.println("✅ Ajuste aplicado exitosamente");

            // recargamos la tabla de la pantalla principal y cerramos
            controladorPadre.cargarDatosEnTabla();
            cerrarVentana();

        } catch (NumberFormatException e) {
            System.err.println("la cantidad tiene que ser un numero entero");
        }
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) txtCantidad.getScene().getWindow();
        stage.close();
    }
}