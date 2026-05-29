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
        // Configuramos las opciones del tipo de ajuste
        cmbTipoAjuste.getItems().addAll("Entrada (Suma al stock)", "Salida (Resta al stock)", "Merma / Daño (Resta al stock)");

        // Configuramos cómo se van a leer los productos en la lista desplegable
        cmbProducto.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto producto) {
                if (producto == null) return null;
                return producto.getSku() + " - " + producto.getNombreProducto();
            }

            @Override
            public Producto fromString(String string) {
                return null; // No lo necesitamos para este caso
            }
        });
    }

    public void setDependencias(InventarioController padre, InventarioService servicio) {
        this.controladorPadre = padre;
        this.servicio = servicio;
        cargarProductosEnLista();
    }

    private void cargarProductosEnLista() {
        // Traemos todos los productos activos de MySQL y los metemos al ComboBox
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
                return; // Aquí podrías poner una alerta visual después
            }

            // Hacemos las matemáticas según el tipo de ajuste
            int stockActual = productoSeleccionado.getStock();

            if (tipoAjuste.contains("Entrada")) {
                productoSeleccionado.setStock(stockActual + cantidadAjuste);
            } else {
                // Para salidas o mermas, restamos (verificando que no quede en negativo)
                if (stockActual - cantidadAjuste < 0) {
                    System.err.println("No puedes restar más stock del que existe.");
                    return;
                }
                productoSeleccionado.setStock(stockActual - cantidadAjuste);
            }

            // TODO: (Opcional a futuro) Guardar el txtMotivo en una tabla de 'Historial_Movimientos'

            // Mandamos el UPDATE a MySQL
            servicio.actualizarProducto(productoSeleccionado);
            System.out.println("✅ Ajuste aplicado exitosamente");

            // Recargamos la tabla principal y cerramos
            controladorPadre.cargarDatosEnTabla();
            cerrarVentana();

        } catch (NumberFormatException e) {
            System.err.println("La cantidad debe ser un número entero válido.");
        }
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) txtCantidad.getScene().getWindow();
        stage.close();
    }
}