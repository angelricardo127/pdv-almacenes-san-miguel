package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ContenedorBaseController {

    @FXML private StackPane areaTrabajo;

    // Declaramos los botones para poder cambiarles el color
    @FXML private Button btnDashboard, btnVentas, btnInventario, btnCaja, btnCambios, btnReportes, btnSeguridad;

    @FXML
    public void initialize() {
        mostrarDashboard();
    }

    // --- LÓGICA PARA PINTAR EL BOTÓN ACTIVO ---
    private void resaltarBoton(Button botonActivo) {
        String estiloInactivo = "-fx-background-color: transparent; -fx-alignment: BASELINE_LEFT; -fx-cursor: hand; -fx-text-fill: #9ca3af; -fx-font-size: 14px;";
        String estiloActivo = "-fx-background-color: #10b981; -fx-alignment: BASELINE_LEFT; -fx-background-radius: 8; -fx-cursor: hand; -fx-text-fill: WHITE; -fx-font-size: 14px; -fx-font-weight: bold;";

        // Apagamos todos primero (evitamos NullPointerException por si Ángel olvidó ponerle fx:id a alguno)
        Button[] botones = {btnDashboard, btnVentas, btnInventario, btnCaja, btnCambios, btnReportes, btnSeguridad};
        for (Button btn : botones) {
            if (btn != null) btn.setStyle(estiloInactivo);
        }

        // Encendemos solo el seleccionado
        if (botonActivo != null) botonActivo.setStyle(estiloActivo);
    }

    private void cargarVista(String archivoFxml) {
        try {
            // Quitamos la ruta completa y usamos el ClassLoader del controlador
            // Esto busca el archivo relativo al paquete donde está este controlador
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/asm/vista/" + archivoFxml));
            Node vista = loader.load();

            areaTrabajo.getChildren().clear();
            areaTrabajo.getChildren().add(vista);
        } catch (Exception e) {
            System.err.println("❌ ERROR: No se encontró el archivo: /com/asm/vista/" + archivoFxml);
            e.printStackTrace(); // Esto te dirá exactamente qué línea falla
        }
    }

    // --- ACCIONES DE LOS BOTONES ---
    @FXML
    public void mostrarDashboard() {
        resaltarBoton(btnDashboard);
        cargarVista("Dashboard.fxml");
    }

    @FXML
    public void mostrarVentas() {
        resaltarBoton(btnVentas);
        cargarVista("PuntoVenta.fxml");
    }

    @FXML
    public void mostrarInventario() {
        resaltarBoton(btnInventario);
        cargarVista("inventario.fxml");
    }

    @FXML
    public void mostrarCaja() {
        resaltarBoton(btnCaja);
        cargarVista("GestionCaja.fxml");
    }

    @FXML
    public void mostrarCambios() {
        resaltarBoton(btnCambios);
        cargarVista("cambios.fxml"); // O el nombre que tenga el tuyo
    }

    @FXML
    public void mostrarReportes() {
        resaltarBoton(btnReportes);
        cargarVista("Reportes.fxml");
    }

    @FXML
    public void mostrarSeguridad() {
        resaltarBoton(btnSeguridad);
        cargarVista("Seguridad.fxml");
    }

    @FXML
    public void cerrarSesion() {
        try {
            // 1. Cargamos el FXML del Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/asm/vista/Login.fxml"));
            Parent root = loader.load();

            // 2. Preparamos la ventanita del Login
            Stage stageLogin = new Stage();
            stageLogin.setTitle("Almacenes San Miguel - Iniciar Sesión");
            stageLogin.setScene(new Scene(root));
            stageLogin.setResizable(false); // Evita que se deforme
            stageLogin.show();

            // 3. Cerramos la ventana gigante del sistema actual
            // Usamos 'areaTrabajo' (o cualquier botón del menú) para obtener la ventana actual
            Stage stageActual = (Stage) areaTrabajo.getScene().getWindow();
            stageActual.close();

        } catch (Exception e) {
            System.err.println("❌ Error al cerrar sesión y cargar el login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}