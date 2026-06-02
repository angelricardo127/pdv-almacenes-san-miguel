package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ContenedorBaseController implements Initializable {

    @FXML private StackPane areaTrabajo;

    // Botones del menú
    @FXML private Button btnDashboard, btnVentas, btnInventario, btnCaja, btnCambios, btnReportes, btnSeguridad;

    // Textos del perfil en la esquina inferior
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarPermisos();

        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();

        if (usuarioLogueado != null) {
            // Actualizar textos del perfil
            if (lblNombreUsuario != null) {
                lblNombreUsuario.setText("👤 " + usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellidoPaterno());
            }
            if (lblRolUsuario != null) {
                String nombreRol = "";
                switch (usuarioLogueado.getIdRol()) {
                    case 1: nombreRol = "Administrador"; break;
                    case 2: nombreRol = "Cajero"; break;
                    case 3: nombreRol = "Almacenista"; break;
                }
                lblRolUsuario.setText(nombreRol);
            }

            // Redirección inicial
            if (usuarioLogueado.getIdRol() == 3) {
                mostrarInventario();
            } else {
                mostrarDashboard();
            }
        }
    }

    private void configurarPermisos() {
        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();
        if (usuarioLogueado == null) return;

        int idRolUsuario = usuarioLogueado.getIdRol();

        switch (idRolUsuario) {
            case 1:
                // Administrador: Todo visible
                break;
            case 2:
                // Cajero
                ocultarBoton(btnInventario);
                ocultarBoton(btnReportes);
                ocultarBoton(btnSeguridad);
                break;
            case 3:
                // Almacenista
                ocultarBoton(btnDashboard);
                ocultarBoton(btnVentas);
                ocultarBoton(btnCaja);
                ocultarBoton(btnCambios);
                ocultarBoton(btnSeguridad);
                break;
            default:
                // Bloqueo total por seguridad si el rol es desconocido
                ocultarBoton(btnVentas);
                ocultarBoton(btnInventario);
                ocultarBoton(btnCaja);
                ocultarBoton(btnCambios);
                ocultarBoton(btnReportes);
                ocultarBoton(btnSeguridad);
                break;
        }
    }

    private void ocultarBoton(Button boton) {
        if (boton != null) {
            boton.setVisible(false);
            boton.setManaged(false);
        }
    }

    private void resaltarBoton(Button botonActivo) {
        String estiloInactivo = "-fx-background-color: transparent; -fx-alignment: BASELINE_LEFT; -fx-cursor: hand; -fx-text-fill: #9ca3af; -fx-font-size: 14px;";
        String estiloActivo = "-fx-background-color: #10b981; -fx-alignment: BASELINE_LEFT; -fx-background-radius: 8; -fx-cursor: hand; -fx-text-fill: WHITE; -fx-font-size: 14px; -fx-font-weight: bold;";

        Button[] botones = {btnDashboard, btnVentas, btnInventario, btnCaja, btnCambios, btnReportes, btnSeguridad};
        for (Button btn : botones) {
            if (btn != null) btn.setStyle(estiloInactivo);
        }
        if (botonActivo != null) botonActivo.setStyle(estiloActivo);
    }

    private void cargarVista(String archivoFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/asm/vista/" + archivoFxml));
            Node vista = loader.load();
            areaTrabajo.getChildren().clear();
            areaTrabajo.getChildren().add(vista);
        } catch (Exception e) {
            System.err.println(" ERROR: No se encontró el archivo: /com/asm/vista/" + archivoFxml);
            e.printStackTrace();
        }
    }

    @FXML public void mostrarDashboard() { resaltarBoton(btnDashboard); cargarVista("Dashboard.fxml"); }
    @FXML public void mostrarVentas() { resaltarBoton(btnVentas); cargarVista("PuntoVenta.fxml"); }
    @FXML public void mostrarInventario() { resaltarBoton(btnInventario); cargarVista("inventario.fxml"); }
    @FXML public void mostrarCaja() { resaltarBoton(btnCaja); cargarVista("GestionCaja.fxml"); }
    @FXML public void mostrarCambios() { resaltarBoton(btnCambios); cargarVista("cambios.fxml"); }
    @FXML public void mostrarReportes() { resaltarBoton(btnReportes); cargarVista("Reportes.fxml"); }
    @FXML public void mostrarSeguridad() { resaltarBoton(btnSeguridad); cargarVista("Seguridad.fxml"); }

    @FXML
    public void cerrarSesion() {
        try {
            com.asm.modelo.SesionGlobal.limpiarSesion();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/asm/vista/Login.fxml"));
            Parent root = loader.load();

            Stage stageLogin = new Stage();
            stageLogin.setTitle("Almacenes San Miguel - Iniciar Sesión");
            stageLogin.setScene(new Scene(root));
            stageLogin.setResizable(false);
            stageLogin.show();

            Stage stageActual = (Stage) areaTrabajo.getScene().getWindow();
            stageActual.close();

        } catch (Exception e) {
            System.err.println(" Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}