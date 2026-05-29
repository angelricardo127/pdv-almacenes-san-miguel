package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ModalPagoController {

    // --- CONEXIONES VISUALES ORIGINALES ---
    @FXML private Label lblTotalPagar;
    @FXML private Button btnConfirmarPago;

    // --- NUEVAS CONEXIONES PARA EL SWICTH EFECTIVO/TARJETA ---
    @FXML private Button btnEfectivo;
    @FXML private Button btnTarjeta;
    @FXML private VBox cajaEfectivo;
    @FXML private VBox cajaTarjeta;
    @FXML private TextField txtMontoRecibido;
    @FXML private Label lblCambio;
    @FXML private TextField txtFolio;

    // --- VARIABLES DE MEMORIA ---
    private double totalVenta = 0.0;
    private boolean pagoAprobado = false;
    private String metodoDePago = "Efectivo";

    // Variables que le vamos a mandar al ticket al final
    private String metodoPagoFinal = "Efectivo";
    private double montoRecibidoFinal = 0.0;

    @FXML
    public void initialize() {
        btnConfirmarPago.setDisable(true);

        // Ocultar tarjeta al arrancar
        if (cajaTarjeta != null) {
            cajaTarjeta.setVisible(false);
            cajaTarjeta.setManaged(false);
        }

        // Acciones de los botones superiores
        if (btnEfectivo != null) btnEfectivo.setOnAction(e -> seleccionarEfectivo());
        if (btnTarjeta != null) btnTarjeta.setOnAction(e -> seleccionarTarjeta());

        // Escuchar si escriben dinero (Efectivo)
        if (txtMontoRecibido != null) {
            txtMontoRecibido.textProperty().addListener((observable, oldValue, newValue) -> {
                if (metodoDePago.equals("Efectivo")) calcularCambio();
            });
        }

        // Escuchar si escriben el folio (Tarjeta)
        if (txtFolio != null) {
            txtFolio.textProperty().addListener((observable, oldValue, newValue) -> {
                if (metodoDePago.equals("Tarjeta")) {
                    // Habilitar botón solo si el folio no está vacío
                    btnConfirmarPago.setDisable(newValue.trim().isEmpty());
                }
            });
        }

        btnConfirmarPago.setOnAction(event -> confirmarVenta());
    }

    public void setTotalAPagar(double total) {
        this.totalVenta = total;
        lblTotalPagar.setText(String.format("$%.2f", total));
        if (lblCambio != null) lblCambio.setText("$0.00");
    }

    private void seleccionarEfectivo() {
        metodoDePago = "Efectivo";
        cajaEfectivo.setVisible(true);
        cajaEfectivo.setManaged(true);
        cajaTarjeta.setVisible(false);
        cajaTarjeta.setManaged(false);

        btnEfectivo.setStyle("-fx-background-color: #e8f8f5; -fx-text-fill: #1abc9c; -fx-border-color: #1abc9c; -fx-background-radius: 6; -fx-border-radius: 6;");
        btnTarjeta.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: #bdc3c7; -fx-background-radius: 6; -fx-border-radius: 6;");

        calcularCambio(); // Recalcular por si acaso
    }

    private void seleccionarTarjeta() {
        metodoDePago = "Tarjeta";
        cajaTarjeta.setVisible(true);
        cajaTarjeta.setManaged(true);
        cajaEfectivo.setVisible(false);
        cajaEfectivo.setManaged(false);

        btnTarjeta.setStyle("-fx-background-color: #e8f8f5; -fx-text-fill: #1abc9c; -fx-border-color: #1abc9c; -fx-background-radius: 6; -fx-border-radius: 6;");
        btnEfectivo.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: #bdc3c7; -fx-background-radius: 6; -fx-border-radius: 6;");

        // Bloquear el botón de confirmar si aún no escriben el folio
        btnConfirmarPago.setDisable(txtFolio.getText().trim().isEmpty());
    }

    private void calcularCambio() {
        try {
            if (txtMontoRecibido.getText().isEmpty()) {
                lblCambio.setText("$0.00");
                lblCambio.setTextFill(Color.web("#2c3e50"));
                btnConfirmarPago.setDisable(true);
                return;
            }

            double monto = Double.parseDouble(txtMontoRecibido.getText());
            double cambio = monto - totalVenta;

            if (cambio >= 0) {
                lblCambio.setText(String.format("$%.2f", cambio));
                lblCambio.setTextFill(Color.web("#1abc9c"));
                btnConfirmarPago.setDisable(false);
            } else {
                lblCambio.setText("Falta dinero");
                lblCambio.setTextFill(Color.RED);
                btnConfirmarPago.setDisable(true);
            }
        } catch (NumberFormatException e) {
            lblCambio.setText("Monto inválido");
            lblCambio.setTextFill(Color.RED);
            btnConfirmarPago.setDisable(true);
        }
    }

    private void confirmarVenta() {
        if (metodoDePago.equals("Efectivo")) {
            this.montoRecibidoFinal = Double.parseDouble(txtMontoRecibido.getText());
            this.metodoPagoFinal = "Efectivo";
        } else {
            this.montoRecibidoFinal = totalVenta; // En tarjeta siempre pagan exacto
            this.metodoPagoFinal = "Tarjeta (Folio: " + txtFolio.getText().trim() + ")";
        }

        pagoAprobado = true;
        Stage stage = (Stage) btnConfirmarPago.getScene().getWindow();
        stage.close();
    }

    public boolean isPagoAprobado() { return pagoAprobado; }
    public double getMontoRecibido() { return montoRecibidoFinal; }
    public String getMetodoPagoFinal() { return metodoPagoFinal; }
}