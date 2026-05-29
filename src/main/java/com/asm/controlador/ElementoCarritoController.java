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
        // Llenar los textos visuales
        lblNombre.setText(producto.getNombreProducto());
        lblPrecio.setText(String.format("$%.2f", producto.getPrecio()));
        lblCantidad.setText(String.valueOf(cantidad));

        // Asignarle la acción a cada botón usando los métodos nuevos de PuntoVentaController
        btnMas.setOnAction(e -> padreController.cambiarCantidadProducto(producto, 1));

        btnMenos.setOnAction(e -> padreController.cambiarCantidadProducto(producto, -1));

        btnEliminar.setOnAction(e -> padreController.eliminarProductoDelCarrito(producto));
    }
}