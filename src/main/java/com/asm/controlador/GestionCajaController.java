package com.asm.controlador;

import com.asm.servicio.CorteCajaService;
import com.asm.servicio.VentaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.print.PrinterJob;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GestionCajaController {

    @FXML private StackPane modalApertura, modalExitoApertura, modalErrorApertura, modalCierre, modalExitoCierre;

    @FXML private Label lblBienvenida, lblCajero, lblFecha, lblExitoCajero, lblExitoFondo;
    @FXML private Label lblCierreCajero, lblCierreTurno, lblCierreFecha, lblCierreHora, lblCierreDiferencia, lblExitoDiferencia;
    @FXML private Label lblCierreVentasEfectivo, lblCierreVentasTarjeta, lblCierreTotalVentas;

    @FXML private ComboBox<String> cbTurno;
    @FXML private TextField txtFondoInicial, txtMontoCierre;
    @FXML private TextArea txtObservaciones;

    private SessionFactory factory;
    private CorteCajaService service;
    private VentaService ventaService;

    private double fondoInicialGuardado = 0.0;
    private double ventasEfectivoReal = 0.0;
    private double ventasTarjetaReal = 0.0;

    @FXML
    public void initialize() {
        try {
            factory = new Configuration().configure("/com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
            service = new CorteCajaService(factory);
            ventaService = new VentaService(factory);
        } catch (Exception e) {
            System.err.println("Ejecutando UI de caja.");
        }

        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();

        if (usuarioLogueado != null) {
            String nombreCompleto = usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellidoPaterno();
            if (lblBienvenida != null) lblBienvenida.setText("Usuario Activo: " + nombreCompleto + " - " + fechaHoy);
            if (lblCajero != null) lblCajero.setText(nombreCompleto);
            if (lblCierreCajero != null) lblCierreCajero.setText(nombreCompleto);
        }

        if (lblFecha != null) lblFecha.setText(fechaHoy);

        if (cbTurno != null) {
            cbTurno.getItems().addAll("Matutino (08:00 - 14:00)", "Vespertino (14:00 - 20:00)");
        }
    }

    @FXML private void ejecutarApertura() {
        if (com.asm.modelo.SesionGlobal.isTurnoAbierto()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "AVISO", "Ya existe un turno abierto. Por favor cierrelo antes de abrir uno nuevo.");
            return;
        }
        if (modalApertura != null) modalApertura.setVisible(true);
    }

    @FXML private void cerrarModalApertura() { if (modalApertura != null) modalApertura.setVisible(false); }
    @FXML private void cerrarModalCierre() { if (modalCierre != null) modalCierre.setVisible(false); }
    @FXML private void cerrarModalError() { if (modalErrorApertura != null) modalErrorApertura.setVisible(false); }

    @FXML
    private void confirmarApertura() {
        if (cbTurno.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "FALTA DE INFORMACIÓN", "Por favor seleccione un turno");
            return;
        }
        String textoFondo = txtFondoInicial.getText();
        if (textoFondo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "MONTO VACIO", "Por favor ingrese el fondo inicial de la caja.");
            return;
        }
        try {
            fondoInicialGuardado = Double.parseDouble(textoFondo);
            if (fondoInicialGuardado < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "MONTO INVALIDO", "El monto inicial no puede ser negativo.");
                return;
            }

            com.asm.modelo.SesionGlobal.setTurnoAbierto(true);
            lblExitoCajero.setText(lblCajero.getText());
            lblExitoFondo.setText(String.format("$%.2f", fondoInicialGuardado));
            modalApertura.setVisible(false);
            modalExitoApertura.setVisible(true);

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "FORMATO INVALIDO", "Favor de ingresar un monto valido solo numeros");
        }
    }

    @FXML
    private void ejecutarCierre() {
        if (!com.asm.modelo.SesionGlobal.isTurnoAbierto()) {
            mostrarAlerta(Alert.AlertType.WARNING, "OPERACION INVALIDA", "No hay ningun turno abierto actualmente.");
            return;
        }

        if (modalCierre != null) {
            // 🔥 CONSULTA REAL A LA BASE DE DATOS
            ventasEfectivoReal = ventaService.obtenerSumaVentasDelDia(1);
            ventasTarjetaReal = ventaService.obtenerSumaVentasDelDia(2);
            System.out.println("🕵️ Efectivo traído de MySQL: " + ventasEfectivoReal);
            System.out.println("🕵️ Tarjeta traída de MySQL: " + ventasTarjetaReal);
            double totalVentas = ventasEfectivoReal + ventasTarjetaReal;

            lblCierreVentasEfectivo.setText(String.format("$%.2f", ventasEfectivoReal));
            lblCierreVentasTarjeta.setText(String.format("$%.2f", ventasTarjetaReal));
            lblCierreTotalVentas.setText(String.format("$%.2f", totalVentas));

            txtMontoCierre.clear();
            lblCierreDiferencia.setText("$0.00");
            lblCierreDiferencia.setTextFill(Color.web("#374151"));

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

            double efectivoEsperado = fondoInicialGuardado + ventasEfectivoReal;
            double diferencia = contado - efectivoEsperado;

            lblCierreDiferencia.setText(String.format("$%.2f", diferencia));

            if (diferencia < 0) {
                lblCierreDiferencia.setTextFill(Color.RED);
            } else {
                lblCierreDiferencia.setTextFill(Color.web("#10b981"));
            }
        } catch (NumberFormatException e) {
            lblCierreDiferencia.setText("error");
            lblCierreDiferencia.setTextFill(Color.RED);
        }
    }

    @FXML
    private void confirmarCierre() {
        if (txtMontoCierre.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "FALTA EFECTIVO", "Ingrese cuanto efectivo hay fisicamente en la caja.");
            return;
        }

        lblExitoDiferencia.setText(lblCierreDiferencia.getText());
        com.asm.modelo.SesionGlobal.setTurnoAbierto(false);
        modalCierre.setVisible(false);
        modalExitoCierre.setVisible(true);
    }

    @FXML
    private void generarReporte() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean continuar = job.showPrintDialog(modalExitoCierre.getScene().getWindow());
            if (continuar) {
                Node ticketResumen = armarDiseñoImpresion();
                boolean exito = job.printPage(ticketResumen);
                if (exito) {
                    job.endJob();
                    System.out.println("Reporte enviado a la impresora");
                }
            }
        }
    }

    private Node armarDiseñoImpresion() {
        VBox ticket = new VBox(10);
        ticket.setStyle("-fx-padding: 20px; -fx-background-color: white;");

        Text titulo = new Text("ALMACENES SAN MIGUEL\nResumen de cierre de caja");
        titulo.setFont(Font.font("Monospaced", 16));

        Text datos = new Text(
                "--------------------------------\n" +
                        "Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                        "Turno: " + cbTurno.getValue() + "\n" +
                        "Fondo inicial: $" + fondoInicialGuardado + "\n" +
                        "Ventas efectivo: $" + ventasEfectivoReal + "\n" +
                        "Ventas tarjeta: $" + ventasTarjetaReal + "\n" +
                        "--------------------------------\n" +
                        "Total en caja esperado: $" + (fondoInicialGuardado + ventasEfectivoReal) + "\n" +
                        "Diferencia reportada: " + lblCierreDiferencia.getText() + "\n" +
                        "--------------------------------\n" +
                        "Firma del cajero:\n\n______________________"
        );
        datos.setFont(Font.font("Monospaced", 12));

        ticket.getChildren().addAll(titulo, datos);
        return ticket;
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
            System.err.println("Error al regresar al menu central");
        }
    }

    @FXML
    private void continuarAVentas() {
        modalExitoApertura.setVisible(false);
        System.out.println("Redirigiendo a ventas");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}