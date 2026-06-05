package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MenuPrincipalController {

    @FXML private Button btnDashboard;
    @FXML private Button btnVentas;
    @FXML private Button btnInventario;
    @FXML private Button btnSeguridad;
    @FXML private VBox contenedorPrincipal;

    @FXML
    public void initialize() {
        // Acciones para cambiar de pestaña al hacer clic
        btnVentas.setOnAction(e -> intercambiarPantalla("ventas.fxml"));
        btnInventario.setOnAction(e -> intercambiarPantalla("inventario.fxml"));
        btnSeguridad.setOnAction(e -> intercambiarPantalla("Seguridad.fxml"));

        // Pantalla por defecto al abrir el sistema (por ejemplo, Seguridad o Dashboard)
        intercambiarPantalla("Seguridad.fxml");
    }

    private void intercambiarPantalla(String nombreFxml) {
        try {
            //  Limpiar lo que esté cargado actualmente a la derecha
            contenedorPrincipal.getChildren().clear();

            //  Cargar la nueva pestaña desde la ruta correcta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/asm/vista/" + nombreFxml));
            Parent nuevaVista = loader.load();

            //  Forzar a que la nueva vista se estire para ocupar todo el espacio
            VBox.setVgrow(nuevaVista, javafx.scene.layout.Priority.ALWAYS);

            //  Inyectar la vista en el contenedor
            contenedorPrincipal.getChildren().add(nuevaVista);

        } catch (IOException e) {
            System.err.println("Error al cargar la pestaña [" + nombreFxml + "]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}