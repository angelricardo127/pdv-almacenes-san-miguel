package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class ContenedorBaseController {

    @FXML
    private StackPane areaTrabajo;

    @FXML
    public void initialize() {
        // Apenas abras el sistema, cargamos el Dashboard por defecto
        mostrarDashboard();
    }

    // --- EL MÉTODO MÁGICO DE INYECCIÓN ---
    private void cargarVista(String archivoFxml) {
        try {
            // Buscamos el FXML solicitado
            Node vista = FXMLLoader.load(getClass().getResource("/com/asm/vista/" + archivoFxml));

            // Limpiamos el centro y metemos la nueva pantalla
            areaTrabajo.getChildren().clear();
            areaTrabajo.getChildren().add(vista);

        } catch (IOException e) {
            System.err.println("❌ Error al inyectar la vista: " + archivoFxml);
            e.printStackTrace();
        }
    }

    // --- ACCIONES DE LOS BOTONES ---

    @FXML
    public void mostrarDashboard() {
        cargarVista("Dashboard.fxml");
    }

    @FXML
    public void mostrarVentas() {
        cargarVista("PuntoVenta.fxml");
    }

    @FXML
    public void mostrarInventario() {
        cargarVista("inventario.fxml");
    }

    @FXML
    public void mostrarCaja() {
        // Apuntando correctamente al archivo que acabamos de crear
        cargarVista("GestionCaja.fxml");
    }

    @FXML
    public void mostrarCambios() {
        cargarVista("cambios.fxml");
    }

    @FXML
    public void mostrarReportes() {
        cargarVista("Reportes.fxml");
    }

    @FXML
    public void mostrarSeguridad() {
        cargarVista("Seguridad.fxml");
    }

    @FXML
    public void cerrarSesion() {
        System.out.println("Cerrando sesión... Redirigiendo al Login.");
    }
}