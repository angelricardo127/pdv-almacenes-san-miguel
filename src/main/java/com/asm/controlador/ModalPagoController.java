package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ModalPagoController {

    @FXML private Label lblTotalPagar;
    @FXML private TextField txtMontoRecibido;
    @FXML private Label lblCambio;
    @FXML private Button btnConfirmarPago;

    private double totalVenta = 0.0;
    private boolean pagoAprobado = false; // Le avisará a la ventana principal si todo salió bien

    @FXML
    public void initialize() {
        // Bloquear el botón de inicio hasta que paguen
        btnConfirmarPago.setDisable(true);

        // Magia de JavaFX: Escuchar cada vez que el usuario teclea un número
        txtMontoRecibido.textProperty().addListener((observable, oldValue, newValue) -> {
            calcularCambio();
        });

        // Qué hacer al darle clic a confirmar
        btnConfirmarPago.setOnAction(event -> confirmarVenta());
    }

    // Este método lo va a llamar tu pantalla principal para pasarle los $450.00 del carrito
    public void setTotalAPagar(double total) {
        this.totalVenta = total;
        lblTotalPagar.setText(String.format("$%.2f", total));
        lblCambio.setText("$0.00");
    }

    private void calcularCambio() {
        try {
            if (txtMontoRecibido.getText().isEmpty()) {
                lblCambio.setText("$0.00");
                lblCambio.setTextFill(Color.web("#2c3e50")); // Color oscuro por defecto
                btnConfirmarPago.setDisable(true);
                return;
            }

            // Convertimos lo que escribió el cajero a número
            double monto = Double.parseDouble(txtMontoRecibido.getText());
            double cambio = monto - totalVenta;

            if (cambio >= 0) {
                // Si el dinero alcanza, mostramos el cambio y habilitamos el botón
                lblCambio.setText(String.format("$%.2f", cambio));
                lblCambio.setTextFill(Color.web("#1abc9c")); // Verde éxito
                btnConfirmarPago.setDisable(false);
            } else {
                // Si falta dinero, avisamos y bloqueamos el botón
                lblCambio.setText("Falta dinero");
                lblCambio.setTextFill(Color.RED);
                btnConfirmarPago.setDisable(true);
            }
        } catch (NumberFormatException e) {
            // Si escriben letras en vez de números por accidente
            lblCambio.setText("Monto inválido");
            lblCambio.setTextFill(Color.RED);
            btnConfirmarPago.setDisable(true);
        }
    }

    private void confirmarVenta() {
        pagoAprobado = true;

        // Cerramos esta ventanita modal
        Stage stage = (Stage) btnConfirmarPago.getScene().getWindow();
        stage.close();
    }

    // Método para que la pantalla principal sepa si el cajero le dio en confirmar o si canceló
    public boolean isPagoAprobado() {
        return pagoAprobado;
    }
    // Método para que la pantalla principal sepa con cuánto nos pagaron
    public double getMontoRecibido() {
        try {
            return Double.parseDouble(txtMontoRecibido.getText());
        } catch (NumberFormatException e) {
            return totalVenta; // Por si acaso está vacío o pagaron exacto
        }
    }
}