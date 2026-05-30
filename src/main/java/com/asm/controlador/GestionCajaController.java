package com.asm.controlador;

import com.asm.servicio.CorteCajaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.print.PrinterJob;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GestionCajaController {

    @FXML private StackPane modalApertura, modalExitoApertura, modalErrorApertura, modalCierre, modalExitoCierre;

    @FXML private Label lblBienvenida, lblCajero, lblFecha, lblExitoCajero, lblExitoFondo;
    @FXML private Label lblCierreCajero, lblCierreTurno, lblCierreFecha, lblCierreHora, lblCierreDiferencia, lblExitoDiferencia;

    //  NUEVOS ETIQUETADOS PARA TUS 3 CAJITAS
    @FXML private Label lblCierreVentasEfectivo, lblCierreVentasTarjeta, lblCierreTotalVentas;

    @FXML private ComboBox<String> cbTurno;
    @FXML private TextField txtFondoInicial, txtMontoCierre;
    @FXML private TextArea txtObservaciones;

    private SessionFactory factory;
    private CorteCajaService service;

    // --- VARIABLES PARA EL ARQUEO DE LA PRESENTACIÓN ---
    private double fondoInicialGuardado = 0.0;
    private double ventasEfectivoDemo = 1500.50; // Lo que les pagaron con billetes hoy
    private double ventasTarjetaDemo = 840.00;   // Lo que cobraron con la terminal

    @FXML
    public void initialize() {
        try {
            factory = new Configuration().configure("/com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
            service = new CorteCajaService(factory);
        } catch (Exception e) {
            System.err.println("Ejecutando UI de caja.");
        }

        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        if (lblBienvenida != null) lblBienvenida.setText("Bienvenido, Roberto Sánchez Pérez • " + fechaHoy);
        if (lblFecha != null) lblFecha.setText(fechaHoy);

        if (cbTurno != null) {
            cbTurno.getItems().addAll("Matutino (08:00 - 14:00)", "Vespertino (14:00 - 20:00)");
        }
    }

    @FXML private void ejecutarApertura() { if (modalApertura != null) modalApertura.setVisible(true); }
    @FXML private void cerrarModalApertura() { if (modalApertura != null) modalApertura.setVisible(false); }
    @FXML private void cerrarModalCierre() { if (modalCierre != null) modalCierre.setVisible(false); }
    @FXML private void cerrarModalError() { if (modalErrorApertura != null) modalErrorApertura.setVisible(false); }

    @FXML
    private void confirmarApertura() {
        if (txtFondoInicial.getText().isEmpty() || cbTurno.getValue() == null) {
            modalErrorApertura.setVisible(true);
            return;
        }

        // Guardamos en memoria con cuánto dinero abrieron la caja
        try {
            fondoInicialGuardado = Double.parseDouble(txtFondoInicial.getText());
        } catch (NumberFormatException e) {
            fondoInicialGuardado = 0.0;
        }

        lblExitoCajero.setText(lblCajero.getText());
        lblExitoFondo.setText(String.format("$%.2f", fondoInicialGuardado));

        modalApertura.setVisible(false);
        modalExitoApertura.setVisible(true);
    }

    @FXML
    private void ejecutarCierre() {
        if (modalCierre != null) {
            // Llenamos tus 3 cajas con los datos reales
            double totalVentas = ventasEfectivoDemo + ventasTarjetaDemo;

            lblCierreVentasEfectivo.setText(String.format("$%.2f", ventasEfectivoDemo));
            lblCierreVentasTarjeta.setText(String.format("$%.2f", ventasTarjetaDemo));
            lblCierreTotalVentas.setText(String.format("$%.2f", totalVentas));

            modalCierre.setVisible(true);
        }
    }

    @FXML
    private void calcularDiferencia() {
        try {
            String input = txtMontoCierre.getText();
            if (input.isEmpty()) {
                lblCierreDiferencia.setText("$0.00");
                lblCierreDiferencia.setTextFill(Color.web("#374151"));
                return;
            }
            double contado = Double.parseDouble(input);

            // LA MAGIA: El cajón físico solo debe tener el fondo con el que abrió + las ventas en efectivo
            double efectivoEsperado = fondoInicialGuardado + ventasEfectivoDemo;
            double diferencia = contado - efectivoEsperado;

            lblCierreDiferencia.setText(String.format("$%.2f", diferencia));

            if (diferencia < 0) {
                lblCierreDiferencia.setTextFill(Color.RED);
            } else {
                lblCierreDiferencia.setTextFill(Color.web("#10b981"));
            }
        } catch (NumberFormatException e) {
            lblCierreDiferencia.setText("Error");
            lblCierreDiferencia.setTextFill(Color.RED);
        }
    }

    @FXML
    private void confirmarCierre() {
        lblExitoDiferencia.setText(lblCierreDiferencia.getText());
        modalCierre.setVisible(false);
        modalExitoCierre.setVisible(true);
    }

    @FXML
    private void continuarAVentas() {
        modalExitoApertura.setVisible(false);
        System.out.println("Redirigiendo a Ventas...");
    }

    @FXML
    private void generarReporte() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean continuar = job.showPrintDialog(modalExitoCierre.getScene().getWindow());
            if (continuar) {
                job.endJob();
            }
        }
    }

    @FXML
    private void cerrarModalExitoCierre() {
        modalExitoCierre.setVisible(false);
        try {
            Node vistaDashboard = FXMLLoader.load(getClass().getResource("/com/asm/vista/Dashboard.fxml"));
            StackPane areaTrabajo = (StackPane) modalExitoCierre.getScene().getRoot().lookup("#areaTrabajo");

            if (areaTrabajo != null) {
                areaTrabajo.getChildren().clear();
                areaTrabajo.getChildren().add(vistaDashboard);
            }
        } catch (Exception e) {
            System.err.println("Error al regresar al Dashboard: " + e.getMessage());
        }
    }
}