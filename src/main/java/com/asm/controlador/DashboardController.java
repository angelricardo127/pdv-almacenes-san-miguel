package com.asm.controlador;

import com.asm.servicio.DashboardService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController {

    // panel para cambiar pantallas
    @FXML
    private BorderPane principalPane;

    // guarda el diseño del inicio
    @FXML
    private VBox vistaContenido;

    @FXML
    private Label lblVentasDia;
    @FXML
    private Label lblTotalStock;
    @FXML
    private Label lblTotalUsuarios;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnVentas;
    @FXML
    private Button btnInventario;
    @FXML
    private Button btnCaja;
    @FXML
    private Button btnCambios;
    @FXML
    private Button btnReportes;
    @FXML
    private Button btnSeguridad;
    @FXML
    private Button btnCerrarSesionLateral;
    @FXML
    private Button btnSalir;

    private DashboardService dashboardService;

    @FXML
    public void initialize() {
        // aqui conectas la base de datos
        // dashboardService = new DashboardService(tuSessionFactory);
        // cargarEstadisticas();
    }

    // pinta de verde el boton que presionas
    private void activarBoton(Button botonSeleccionado) {
        Button[] botones = {btnDashboard, btnVentas, btnInventario, btnCaja, btnCambios, btnReportes, btnSeguridad};

        for (Button btn : botones) {
            if (btn != null) {
                // pone los otros botones en gris
                btn.setStyle("-fx-background-color: transparent; -fx-alignment: BASELINE_LEFT;");
                btn.setTextFill(javafx.scene.paint.Color.web("#9ca3af"));
                if (btn.getFont().getName().contains("Bold")) {
                    btn.setStyle(btn.getStyle() + "-fx-font-weight: normal;");
                }
            }
        }

        // pone el boton activo en verde
        botonSeleccionado.setStyle("-fx-background-color: #10b981; -fx-alignment: BASELINE_LEFT; -fx-background-radius: 8; -fx-font-weight: bold;");
        botonSeleccionado.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    // metodos para navegar en el centro
    @FXML
    public void abrirDashboard(ActionEvent event) {
        activarBoton(btnDashboard);
        // regresa a la pantalla de inicio
        principalPane.setCenter(vistaContenido);
    }

    @FXML
    public void abrirInventario(ActionEvent event) {
        activarBoton(btnInventario);
        cargarVistaInterna("/vistas/Inventario.fxml");
    }

    @FXML
    public void abrirCaja(ActionEvent event) {
        activarBoton(btnCaja);
        cargarVistaInterna("/vistas/GestionCaja.fxml");
    }

    @FXML
    public void abrirVentas(ActionEvent event) {
        activarBoton(btnVentas);
        cargarVistaInterna("/vistas/GestionVentas.fxml");
    }

    @FXML
    public void abrirCambios(ActionEvent event) {
        activarBoton(btnCambios);
        System.out.println("cargando modulo de cambios...");
    }

    @FXML
    public void abrirReportes(ActionEvent event) {
        activarBoton(btnReportes);
        System.out.println("cargando modulo de reportes...");
    }

    @FXML
    public void abrirSeguridad(ActionEvent event) {
        activarBoton(btnSeguridad);
        System.out.println("cargando modulo de seguridad...");
    }

    // cierra sesion y manda al login
    @FXML
    public void cerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load();

            // busca la ventana que esta abierta
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Almacenes San Miguel - Inicio de Sesion");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.err.println("error al redirigir al login: " + e.getMessage());
        }
    }

    // carga vistas sin quitar el menu
    private void cargarVistaInterna(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent nuevaVista = loader.load();
            principalPane.setCenter(nuevaVista);
        } catch (Exception e) {
            System.err.println("no se pudo cargar la vista interna: " + rutaFxml + " -> " + e.getMessage());
        }
    }

    public void cargarEstadisticas() {
        if (dashboardService != null) {
            long ventas = dashboardService.obtenerVentasDelDia();
            long stock = dashboardService.obtenerTotalStock();
            long usuarios = dashboardService.obtenerTotalUsuariosActivos();

            lblVentasDia.setText("$" + ventas + ".00");
            lblTotalStock.setText(String.valueOf(stock));
            lblTotalUsuarios.setText(String.valueOf(usuarios));
        }
    }
}