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
        // En tu captura vi un PuntoVenta.fxml y un ReporteVenta.fxml,
        // ajusta este nombre si tu pantalla de ventas se llama diferente.
        cargarVista("PuntoVenta.fxml");
    }

    @FXML
    public void mostrarInventario() {
        cargarVista("inventario.fxml"); // Ajustado a minúscula según tu captura
    }

    @FXML
    public void mostrarCaja() {
        // Asumiendo que se llama Caja.fxml (ajusta si es necesario)
        cargarVista("Caja.fxml");
    }

    @FXML
    public void mostrarCambios() {
        cargarVista("cambios.fxml"); // Ajustado a minúscula según tu captura
    }

    @FXML
    public void mostrarReportes() {
        cargarVista("Reportes.fxml"); // Ajustado a mayúscula según tu captura
    }

    @FXML
    public void mostrarSeguridad() {
        cargarVista("Seguridad.fxml"); // Ajustado a mayúscula según tu captura
    }

    @FXML
    public void cerrarSesion() {
        // Por ahora lo dejamos como un mensaje en consola
        // Más adelante conectaremos esto para que destruya el chasis y cargue Login.fxml
        System.out.println("Cerrando sesión... Redirigiendo al Login.");
    }
}