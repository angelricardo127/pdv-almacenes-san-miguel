package com.asm.controlador;

import com.asm.modelo.DetalleVenta;
import com.asm.modelo.Producto;
import com.asm.modelo.Venta;
import com.asm.servicio.VentaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModalReimprimirController {

    @FXML private Button btnCerrarX;
    @FXML private TextField txtFolioTicket;
    @FXML private Button btnCancelar;
    @FXML private Button btnReimprimir;

    private VentaService servicioVentas;

    public void setServicioVentas(VentaService servicioVentas) {
        this.servicioVentas = servicioVentas;
    }

    @FXML
    public void initialize() {
        btnCerrarX.setOnAction(event -> cerrarVentana());
        btnCancelar.setOnAction(event -> cerrarVentana());
        btnReimprimir.setOnAction(event -> buscarYReimprimir());
    }

    private void buscarYReimprimir() {
        String folioStr = txtFolioTicket.getText();
        if (folioStr == null || folioStr.trim().isEmpty()) return;

        try {
            int idVenta = Integer.parseInt(folioStr.trim());
            Venta ventaHistorica = servicioVentas.obtenerVentaPorId(idVenta);
            if (ventaHistorica == null) return;

            List<DetalleVenta> detalles = servicioVentas.obtenerDetallesPorVenta(idVenta);
            Map<Integer, Integer> cantidades = new HashMap<>();
            Map<Integer, Producto> productos = new HashMap<>();
            double totalCompra = 0.0;

            for (DetalleVenta detalle : detalles) {
                Producto p = servicioVentas.obtenerProductoPorId(detalle.getIdProducto());
                if(p != null) {
                    p.setPrecio(detalle.getPrecioUnitario());
                    cantidades.put(p.getIdProducto(), detalle.getCantidad());
                    productos.put(p.getIdProducto(), p);
                    totalCompra += (detalle.getCantidad() * detalle.getPrecioUnitario());
                }
            }

            // --- DEFENSA FINAL CONTRA NULLPOINTERS ---
            FXMLLoader ticketLoader = new FXMLLoader(getClass().getResource("/com/asm/vista/ModalTicket.fxml"));
            Parent ticketRoot = ticketLoader.load();

            ModalTicketController ticketController = ticketLoader.getController();
            if (ticketController != null) {
                ticketController.cargarDatosTicket(cantidades, productos, totalCompra, totalCompra, 0.0, "Efectivo");
                Stage ticketStage = new Stage();
                ticketStage.setScene(new Scene(ticketRoot));
                ticketStage.showAndWait();
            }
            cerrarVentana();

        } catch (Exception e) {
            // Este log es tu mejor amigo. Si peta, el error saldrá aquí en la consola de IntelliJ.
            System.err.println("❌ ERROR FATAL EN REIMPRESIÓN: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}