package com.asm.controlador;

import com.asm.servicio.CorteCajaService;
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

    // --- VARIABLES DE ESTADO Y ARQUEO ---
    private boolean turnoAbierto = false; // 🔥 NUEVO: Candado para saber si hay turno activo
    private double fondoInicialGuardado = 0.0;

    // NOTA: Estas variables eventualmente las llenarás consultando tu VentaService
    private double ventasEfectivoDemo = 1500.50;
    private double ventasTarjetaDemo = 840.00;

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

    // --- MÉTODOS DE NAVEGACIÓN DE MODALES ---
    @FXML private void ejecutarApertura() {
        if (turnoAbierto) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Aviso", "Ya existe un turno abierto. Por favor ciérrelo antes de abrir uno nuevo.");
            return;
        }
        if (modalApertura != null) modalApertura.setVisible(true);
    }

    @FXML private void cerrarModalApertura() { if (modalApertura != null) modalApertura.setVisible(false); }
    @FXML private void cerrarModalCierre() { if (modalCierre != null) modalCierre.setVisible(false); }
    @FXML private void cerrarModalError() { if (modalErrorApertura != null) modalErrorApertura.setVisible(false); }

    // --- LÓGICA DE APERTURA DE TURNO ---
    @FXML
    private void confirmarApertura() {
        // 1. Validamos que hayan elegido el turno
        if (cbTurno.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Falta Información", "Por favor, seleccione un turno (Matutino/Vespertino).");
            return;
        }

        // 2. Validamos el dinero
        String textoFondo = txtFondoInicial.getText();
        if (textoFondo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Monto Vacío", "Por favor, ingrese el fondo inicial de la caja.");
            return;
        }

        try {
            // Intentamos convertir el texto a número. Si hay letras, saltará al "catch"
            fondoInicialGuardado = Double.parseDouble(textoFondo);

            if (fondoInicialGuardado < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Monto Inválido", "El monto inicial no puede ser negativo.");
                return;
            }

            // Si llegamos aquí, los datos son perfectos.
            turnoAbierto = true; // 🔥 ABRIMOS EL CANDADO

            lblExitoCajero.setText(lblCajero.getText());
            lblExitoFondo.setText(String.format("$%.2f", fondoInicialGuardado));

            modalApertura.setVisible(false);
            modalExitoApertura.setVisible(true);

        } catch (NumberFormatException e) {
            // Si el cajero tecleó "100a" o "cien", salta esta alerta
            mostrarAlerta(Alert.AlertType.ERROR, "Formato Inválido", "Favor de ingresar un monto válido (solo números).");
        }
    }

    // --- LÓGICA DE CIERRE DE TURNO ---
    @FXML
    private void ejecutarCierre() {
        // 🔥 VERIFICACIÓN: No puedes cerrar lo que no está abierto
        if (!turnoAbierto) {
            mostrarAlerta(Alert.AlertType.WARNING, "Operación Inválida", "No hay ningún turno abierto actualmente. Abra un turno para poder operar.");
            return;
        }

        if (modalCierre != null) {
            double totalVentas = ventasEfectivoDemo + ventasTarjetaDemo;

            lblCierreVentasEfectivo.setText(String.format("$%.2f", ventasEfectivoDemo));
            lblCierreVentasTarjeta.setText(String.format("$%.2f", ventasTarjetaDemo));
            lblCierreTotalVentas.setText(String.format("$%.2f", totalVentas));

            // Limpiamos los campos por si tenían datos de un cierre anterior
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

            // El cajón físico debe tener el fondo + las ventas cobradas solo en billetes/monedas
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
        if (txtMontoCierre.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Falta Efectivo Físico", "Por favor ingrese cuánto efectivo hay físicamente en la caja.");
            return;
        }

        lblExitoDiferencia.setText(lblCierreDiferencia.getText());

        // 🔥 CERRAMOS EL CANDADO DEL TURNO
        turnoAbierto = false;

        modalCierre.setVisible(false);

        // Alerta de éxito antes de pasar a la ventana final
        mostrarAlerta(Alert.AlertType.INFORMATION, "Cierre de Caja Exitoso", "El turno se ha cerrado correctamente en el sistema.");

        modalExitoCierre.setVisible(true);
    }

    // --- LÓGICA DE IMPRESIÓN Y SALIDA ---
    @FXML
    private void generarReporte() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            // Esto lanza la ventana de impresión nativa de Windows
            boolean continuar = job.showPrintDialog(modalExitoCierre.getScene().getWindow());
            if (continuar) {
                // Creamos el diseño del ticket "invisible" para mandarlo a la impresora
                Node ticketResumen = armarDiseñoImpresion();
                boolean exito = job.printPage(ticketResumen);

                if (exito) {
                    job.endJob();
                    System.out.println("✅ Reporte enviado a la impresora exitosamente.");
                } else {
                    System.err.println("❌ Fallo al intentar imprimir la página.");
                }
            }
        }
    }

    // Método oculto que "dibuja" el ticket para que salga bien alineado en el papel
    private Node armarDiseñoImpresion() {
        VBox ticket = new VBox(10);
        ticket.setStyle("-fx-padding: 20px; -fx-background-color: white;");

        Text titulo = new Text("ALMACENES SAN MIGUEL\nResumen de Cierre de Caja");
        titulo.setFont(Font.font("Monospaced", 16));

        Text datos = new Text(
                "--------------------------------\n" +
                        "Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                        "Turno: " + cbTurno.getValue() + "\n" +
                        "Fondo Inicial: $" + fondoInicialGuardado + "\n" +
                        "Ventas Efectivo: $" + ventasEfectivoDemo + "\n" +
                        "Ventas Tarjeta: $" + ventasTarjetaDemo + "\n" +
                        "--------------------------------\n" +
                        "Total en Caja Esperado: $" + (fondoInicialGuardado + ventasEfectivoDemo) + "\n" +
                        "Diferencia Reportada: " + lblCierreDiferencia.getText() + "\n" +
                        "--------------------------------\n" +
                        "Firma del Cajero:\n\n______________________"
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
            System.err.println("Error al regresar al Dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void continuarAVentas() {
        modalExitoApertura.setVisible(false);
        System.out.println("Redirigiendo a Ventas...");
    }

    // --- MÉTODO REUTILIZABLE PARA ALERTAS ---
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}