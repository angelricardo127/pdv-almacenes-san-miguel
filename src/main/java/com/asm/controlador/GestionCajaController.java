package com.asm.controlador;

import com.asm.servicio.CorteCajaService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GestionCajaController {

    @FXML private StackPane modalApertura, modalExitoApertura, modalErrorApertura, modalAvisoTurno, modalCierre, modalExitoCierre;
    @FXML private Label lblCajero, lblFecha, lblExitoCajero, lblExitoFondo, lblCierreCajero, lblCierreTurno, lblCierreFecha, lblCierreHora, lblErrorCierre, lblCierreFondoInicial, lblCierreVentas, lblCierreTotalEsperado, lblCierreDiferencia, lblExitoDiferencia;
    @FXML private ComboBox<String> cbTurno;
    @FXML private TextField txtFondoInicial, txtMontoCierre;
    @FXML private TextArea txtObservaciones, txtObservacionesCierre;

    private SessionFactory factory;
    private CorteCajaService service;

    @FXML
    public void initialize() {
        // Configuración segura de Hibernate
        try {
            factory = new Configuration().configure("/hibernate.cfg.xml").buildSessionFactory();
            service = new CorteCajaService(factory);
        } catch (Exception e) {
            System.err.println("Error de Hibernate: " + e.getMessage());
        }

        // Inicialización segura de componentes
        if (lblFecha != null) {
            lblFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        if (cbTurno != null) {
            cbTurno.getItems().addAll("Matutino (08:00 - 14:00)", "Vespertino (14:00 - 20:00)");
        }
    }

    @FXML private void ejecutarApertura() { if (modalApertura != null) modalApertura.setVisible(true); }
    @FXML private void cerrarModalApertura() { if (modalApertura != null) modalApertura.setVisible(false); }
    @FXML private void ejecutarCierre() { if (modalCierre != null) modalCierre.setVisible(true); }
    @FXML private void cerrarModalCierre() { if (modalCierre != null) modalCierre.setVisible(false); }
    @FXML private void cerrarModalError() { if (modalErrorApertura != null) modalErrorApertura.setVisible(false); }
    @FXML private void cerrarModalAviso() { if (modalAvisoTurno != null) modalAvisoTurno.setVisible(false); }
    @FXML private void cerrarModalExitoCierre() { if (modalExitoCierre != null) modalExitoCierre.setVisible(false); }

    // Método dummy para que no falle el FXML si no lo has implementado
    @FXML private void confirmarApertura() { /* Lógica de apertura aquí */ }
    @FXML private void confirmarCierre() { /* Lógica de cierre aquí */ }
    @FXML private void calcularDiferencia() { /* Lógica de diferencia aquí */ }
    @FXML private void continuarAVentas() { modalExitoApertura.setVisible(false); }
    @FXML private void generarReporte() { /* Lógica de impresión aquí */ }
    @FXML private void cerrarSesion() { System.exit(0); }
}