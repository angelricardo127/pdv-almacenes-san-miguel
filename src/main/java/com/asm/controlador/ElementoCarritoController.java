package com.asm.controlador;

import com.asm.modelo.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ElementoCarritoController {

    @FXML private ImageView imgProducto;
    @FXML private Label lblNombre;
    @FXML private Label lblPrecio;
    @FXML private Label lblCantidad;
    @FXML private Button btnMenos;
    @FXML private Button btnMas;
    @FXML private Button btnEliminar;

    public void configurarElemento(Producto producto, int cantidad, PuntoVentaController padreController) {
        // ponemos la info en la pantalla
        lblNombre.setText(producto.getNombreProducto());
        lblPrecio.setText(String.format("$%.2f", producto.getPrecio()));
        lblCantidad.setText(String.valueOf(cantidad));

        // activamos los botones para que hagan su trabajo
        btnMas.setOnAction(e -> padreController.cambiarCantidadProducto(producto, 1));

        btnMenos.setOnAction(e -> padreController.cambiarCantidadProducto(producto, -1));

        btnEliminar.setOnAction(e -> padreController.eliminarProductoDelCarrito(producto));
    }
}