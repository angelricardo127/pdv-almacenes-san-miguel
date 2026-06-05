package com.asm.controlador;

import com.asm.modelo.DetalleVenta;
import com.asm.modelo.Producto;
import com.asm.modelo.Venta;
import com.asm.servicio.VentaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    // Metodo para recibir el servicio desde la pantalla principal
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

        if (folioStr == null || folioStr.trim().isEmpty()) {
            System.out.println(" Por favor ingresa un número de folio.");
            return;
        }

        try {
            int idVenta = Integer.parseInt(folioStr.trim());

            //  Buscamos si la venta existe en MySQL
            Venta ventaHistorica = servicioVentas.obtenerVentaPorId(idVenta);

            if (ventaHistorica == null) {
                System.out.println(" No se encontró ninguna venta con el folio: #" + idVenta);
                return;
            }

            //  Traemos todos los detalles (los artículos que compró)
            List<DetalleVenta> detalles = servicioVentas.obtenerDetallesPorVenta(idVenta);

            //  Reconstruimos los mapas que necesita tu ModalTicketController
            Map<Integer, Integer> cantidades = new HashMap<>();
            Map<Integer, Producto> productos = new HashMap<>();
            double totalCompra = 0.0;

            for (DetalleVenta detalle : detalles) {
                Producto p = servicioVentas.obtenerProductoPorId(detalle.getIdProducto());
                if(p != null) {
                    // Usamos el precio histórico al que se vendió en aquel entonces
                    p.setPrecio(detalle.getPrecioUnitario());

                    cantidades.put(p.getIdProducto(), detalle.getCantidad());
                    productos.put(p.getIdProducto(), p);
                    totalCompra += (detalle.getCantidad() * detalle.getPrecioUnitario());
                }
            }

            //  Invocamos tu misma ventana de Ticket
            FXMLLoader ticketLoader = new FXMLLoader(getClass().getResource("/com/asm/vista/ModalTicket.fxml"));
            Parent ticketRoot = ticketLoader.load();

            ModalTicketController ticketController = ticketLoader.getController();
            // Como es reimpresión, simulamos que pagó exacto (cambio $0.00)
            ticketController.cargarDatosTicket(cantidades, productos, totalCompra, totalCompra, 0.0, "Efectivo");

            Stage ticketStage = new Stage();
            ticketStage.setTitle("Reimpresión - Ticket #" + idVenta);
            ticketStage.setScene(new Scene(ticketRoot));
            ticketStage.initModality(Modality.APPLICATION_MODAL);

            // Cerramos la ventanita morada
            cerrarVentana();

            // Mostramos el ticket al usuario
            ticketStage.showAndWait();

        } catch (NumberFormatException e) {
            System.out.println(" El folio debe ser un número entero (Ej: 1).");
        } catch (Exception e) {
            System.err.println(" Error al armar el ticket histórico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}