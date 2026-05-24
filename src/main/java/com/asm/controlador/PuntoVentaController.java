package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PuntoVentaController {

    // 1. Enlazamos el contenedor donde irán los productos
    @FXML
    private TilePane contenedorProductos;

    // 2. Este método se ejecuta automáticamente cuando se abre la pantalla
    @FXML
    public void initialize() {
        System.out.println("Cargando el Punto de Venta...");

        // Aquí llamaremos a la base de datos después.
        // Por ahora, vamos a probar si podemos dibujar UNA tarjeta de prueba.
        crearTarjetaProducto("UNI-PG-001", "Pantalón Gala Hombre", "$450.00", 45);
    }

    /**
     * Este método "dibuja" una tarjeta visual en JavaFX y la pega en el TilePane.
     * Más adelante, lo meteremos en un ciclo 'for' para todos tus productos reales.
     */
    private void crearTarjetaProducto(String sku, String nombre, String precio, int stock) {
        // Creamos la "tarjetita" blanca (Un VBox)
        VBox tarjeta = new VBox();
        tarjeta.setPrefSize(200, 150);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setSpacing(10);
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Creamos los textos que van adentro
        Label lblNombre = new Label(nombre);
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setWrapText(true);

        Label lblSku = new Label(sku);
        lblSku.setTextFill(javafx.scene.paint.Color.GRAY);

        Label lblPrecio = new Label(precio);
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblPrecio.setTextFill(javafx.scene.paint.Color.web("#1abc9c")); // Verde esmeralda

        Label lblStock = new Label("Stock: " + stock);
        lblStock.setTextFill(javafx.scene.paint.Color.GRAY);
        lblStock.setFont(Font.font("System", 10));

        // Metemos los textos a la tarjeta
        tarjeta.getChildren().addAll(lblNombre, lblSku, lblPrecio, lblStock);

        // Pegamos la tarjeta terminada en el contenedor central de la pantalla
        contenedorProductos.getChildren().add(tarjeta);
    }
}