package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.InventarioService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class InventarioController {

    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private TextField txtIdTalla;
    @FXML private TextField txtIdGenero;

    @FXML private Button btnGuardarProducto;
    @FXML private TilePane contenedorCatalogo;

    private InventarioService servicio;

    @FXML
    public void initialize() {
        System.out.println("Iniciando el Módulo de Inventario...");

        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(com.asm.modelo.Producto.class);
            configuration.addAnnotatedClass(com.asm.modelo.Talla.class);
            configuration.addAnnotatedClass(com.asm.modelo.Genero.class);

            SessionFactory factory = configuration.buildSessionFactory();
            servicio = new InventarioService(factory);

            if (btnGuardarProducto != null) {
                btnGuardarProducto.setOnAction(event -> guardarNuevoProducto());
            }

            actualizarVistaCatalogo();

        } catch (Exception e) {
            System.err.println("Error al cargar el Inventario: " + e.getMessage());
        }
    }

    private void guardarNuevoProducto() {
        try {
            if (txtNombreProducto.getText().isEmpty() || txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
                System.out.println("⚠️ Por favor, llena todos los campos.");
                return;
            }

            String nombre = txtNombreProducto.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            int idTalla = Integer.parseInt(txtIdTalla.getText());
            int idGenero = Integer.parseInt(txtIdGenero.getText());

            Producto nuevoProducto = new Producto(nombre, stock, precio, idTalla, idGenero);
            boolean exito = servicio.registrarNuevoProducto(nuevoProducto);

            if (exito) {
                System.out.println("✅ ¡Producto registrado con éxito!");
                txtNombreProducto.clear();
                txtPrecio.clear();
                txtStock.clear();
                txtIdTalla.clear();
                txtIdGenero.clear();
                actualizarVistaCatalogo();
            }

        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error: Asegúrate de poner solo números en el precio, stock y IDs.");
        }
    }

    private void actualizarVistaCatalogo() {
        if (contenedorCatalogo != null) {
            contenedorCatalogo.getChildren().clear();
            List<Producto> listaProductos = servicio.obtenerCatalogoCompleto();

            if (listaProductos != null) {
                for (Producto prod : listaProductos) {
                    crearTarjetaProducto(prod);
                }
            }
        }
    }

    private void crearTarjetaProducto(Producto prod) {
        VBox tarjeta = new VBox();
        tarjeta.setPrefSize(180, 120);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setSpacing(8);
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label lblNombre = new Label(prod.getNombreProducto());
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setWrapText(true);

        Label lblPrecio = new Label(String.format("$%.2f", prod.getPrecio()));
        lblPrecio.setTextFill(javafx.scene.paint.Color.web("#2c3e50"));

        Label lblStock = new Label("Stock: " + prod.getStock());
        lblStock.setTextFill(javafx.scene.paint.Color.GRAY);

        tarjeta.getChildren().addAll(lblNombre, lblPrecio, lblStock);
        contenedorCatalogo.getChildren().add(tarjeta);
    }
}