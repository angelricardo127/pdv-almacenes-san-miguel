package com.asm.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TicketDevolucionController {

    @FXML private Label lblTicket;
    @FXML private Label lblCantidad;
    @FXML private Label lblMotivo;
    @FXML private Label lblReembolso;

    /**
     * Este es el método clave. La pantalla de devoluciones lo va a llamar
     * para inyectar los datos reales justo antes de mostrar el ticket.
     */
    public void setDatosTicket(String folioTicket, int cantidadProductos, String motivo, double totalReembolso) {
        lblTicket.setText("#" + folioTicket);
        lblCantidad.setText(String.valueOf(cantidadProductos));
        lblMotivo.setText(motivo);
        lblReembolso.setText(String.format("$%.2f", totalReembolso));
    }

    @FXML
    private void imprimirTicket(ActionEvent event) {
        System.out.println("⏳ Solicitando cuadro de impresión/guardado a Windows...");
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) {
            // 🔥 El truco mágico: pasar 'null' evita que JavaFX bloquee la ventana de Windows
            boolean continuar = job.showPrintDialog(null);

            if (continuar) {
                // Capturamos visualmente todo el FXML del ticket
                Node nodoAImprimir = ((Node) event.getSource()).getScene().getRoot();

                // Lo mandamos a imprimir o guardar como PDF
                boolean exito = job.printPage(nodoAImprimir);

                if (exito) {
                    job.endJob();
                    System.out.println("✅ ¡Comprobante guardado/impreso con éxito!");
                } else {
                    System.err.println("⚠️ Hubo un error al procesar el documento.");
                }
            } else {
                System.out.println("❌ El usuario cerró la ventana de Windows.");
            }
        } else {
            System.err.println("⚠️ Cuidado: Windows no detectó ninguna impresora ni generador de PDF instalado.");
        }
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        // Cierra este modal (popup) de manera limpia
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        // Opcional: Aquí podrías disparar un evento para limpiar el formulario de devoluciones
    }
}