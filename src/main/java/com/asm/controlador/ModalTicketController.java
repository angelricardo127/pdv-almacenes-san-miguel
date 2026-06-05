package com.asm.controlador;

import com.asm.modelo.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javafx.print.PrinterJob;

public class ModalTicketController {

    @FXML private Label lblFechaHora;
    @FXML private Label lblCajeroTicket; // <-- Conectado con el nuevo fx:id del FXML
    @FXML private VBox vboxListaArticulos;
    @FXML private Label lblSubtotalTicket;
    @FXML private Label lblTotalTicket;
    @FXML private Label lblMetodoPagoTicket;
    @FXML private Label lblRecibidoTicket;
    @FXML private Label lblCambioTicket;
    @FXML private Button btnCerrar;
    @FXML private Button btnCerrarX;
    @FXML private Button btnGenerarTicket;

    @FXML
    public void initialize() {
        // Poner la fecha y hora actual automáticamente
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        lblFechaHora.setText(dtf.format(LocalDateTime.now()));

        // PONER EL CAJERO REAL AUTOMÁTICAMENTE DESDE LA SESIÓN GLOBAL
        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();
        if (lblCajeroTicket != null && usuarioLogueado != null) {
            lblCajeroTicket.setText(usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellidoPaterno());
        }

        // Darle función a los botones de cerrar
        btnCerrar.setOnAction(event -> cerrarVentana());
        btnCerrarX.setOnAction(event -> cerrarVentana());

        // Ejecutar la impresión nativa
        btnGenerarTicket.setOnAction(event -> imprimirTicket());
    }

    /**
     * MÉTODO ORIGINAL: Usado por PuntoVentaController y ModalPagoController para Ventas normales.
     */
    public void cargarDatosTicket(Map<Integer, Integer> cantidades, Map<Integer, Producto> productos,
                                  double total, double recibido, double cambio, String metodoPago) {

        lblSubtotalTicket.setText(String.format("$%.2f", total));
        lblTotalTicket.setText(String.format("$%.2f", total));
        lblRecibidoTicket.setText(String.format("$%.2f", recibido));
        lblCambioTicket.setText(String.format("$%.2f", cambio));
        lblMetodoPagoTicket.setText(metodoPago);

        vboxListaArticulos.getChildren().clear();

        for (Integer id : cantidades.keySet()) {
            Producto p = productos.get(id);
            int cantidad = cantidades.get(id);
            double subtotalProducto = p.getPrecio() * cantidad;

            Label lblNombre = new Label(p.getNombreProducto());
            lblNombre.setFont(Font.font("Monospaced", 12));

            HBox renglonDetalle = new HBox();
            Label lblCantPrecio = new Label(cantidad + " x " + String.format("$%.2f", p.getPrecio()));
            lblCantPrecio.setFont(Font.font("Monospaced", 12));

            Region separador = new Region();
            HBox.setHgrow(separador, Priority.ALWAYS);

            Label lblSubt = new Label(String.format("$%.2f", subtotalProducto));
            lblSubt.setFont(Font.font("Monospaced", 12));

            renglonDetalle.getChildren().addAll(lblCantPrecio, separador, lblSubt);

            VBox bloqueProducto = new VBox(lblNombre, renglonDetalle);
            bloqueProducto.setSpacing(2);

            vboxListaArticulos.getChildren().add(bloqueProducto);
        }
    }

    /**
     * NUEVO MÉTODO SOBRECARGADO: Llamado desde FormularioCambioController para cambios de prendas.
     */
    public void cargarDatosCambio(String folio, String prendaSale, double precioSale, String prendaNuevo, double precioNuevo, double diferencia) {

        lblSubtotalTicket.setText(String.format("$%.2f", precioSale));
        lblTotalTicket.setText(String.format("$%.2f", precioNuevo));

        vboxListaArticulos.getChildren().clear();

        // Renglón del producto que el cliente regresa
        Label lblSaleNombre = new Label("(-) REGRESA: " + prendaSale);
        lblSaleNombre.setFont(Font.font("Monospaced", 11));
        lblSaleNombre.setStyle("-fx-text-fill: #E53E3E;");

        HBox renglonSale = new HBox();
        Label lblSaleDetalle = new Label("1 x " + String.format("$%.2f", precioSale));
        lblSaleDetalle.setFont(Font.font("Monospaced", 11));
        Region sep1 = new Region();
        HBox.setHgrow(sep1, Priority.ALWAYS);
        Label lblSaleSub = new Label(String.format("-$%.2f", precioSale));
        lblSaleSub.setFont(Font.font("Monospaced", 11));
        renglonSale.getChildren().addAll(lblSaleDetalle, sep1, lblSaleSub);

        // Renglón del producto nuevo que se lleva
        Label lblNuevoNombre = new Label("(+) SE LLEVA: " + prendaNuevo);
        lblNuevoNombre.setFont(Font.font("Monospaced", 11));
        lblNuevoNombre.setStyle("-fx-text-fill: #3182CE;");

        HBox renglonNuevo = new HBox();
        Label lblNuevoDetalle = new Label("1 x " + String.format("$%.2f", precioNuevo));
        lblNuevoDetalle.setFont(Font.font("Monospaced", 11));
        Region sep2 = new Region();
        HBox.setHgrow(sep2, Priority.ALWAYS);
        Label lblNuevoSub = new Label(String.format("$%.2f", precioNuevo));
        lblNuevoSub.setFont(Font.font("Monospaced", 11));
        renglonNuevo.getChildren().addAll(lblNuevoDetalle, sep2, lblNuevoSub);

        VBox bloqueCambio = new VBox(lblSaleNombre, renglonSale, new Label(" "), lblNuevoNombre, renglonNuevo);
        bloqueCambio.setSpacing(2);
        vboxListaArticulos.getChildren().add(bloqueCambio);

        if (diferencia > 0) {
            lblMetodoPagoTicket.setText("Excedente Cobrado");
            lblRecibidoTicket.setText(String.format("$%.2f", diferencia));
            lblCambioTicket.setText("$0.00");
        } else if (diferencia < 0) {
            lblMetodoPagoTicket.setText("Saldo Devuelto");
            lblRecibidoTicket.setText("$0.00");
            lblCambioTicket.setText(String.format("$%.2f", Math.abs(diferencia)));
        } else {
            lblMetodoPagoTicket.setText("Cambio Equivalente");
            lblRecibidoTicket.setText("$0.00");
            lblCambioTicket.setText("$0.00");
        }
    }

    private void imprimirTicket() {
        System.out.println("🖨 Abriendo cuadro de diálogo de impresión...");

        btnGenerarTicket.setVisible(false);
        btnCerrar.setVisible(false);
        btnCerrarX.setVisible(false);

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) {
            javafx.stage.Stage ventanaTicket = (javafx.stage.Stage) btnGenerarTicket.getScene().getWindow();
            javafx.stage.Window ventanaPadre = ventanaTicket.getOwner();

            boolean mostrarDialogo = job.showPrintDialog(ventanaPadre);
            if (mostrarDialogo) {
                boolean impreso = job.printPage(btnGenerarTicket.getScene().getRoot());

                if (impreso) {
                    job.endJob();
                    System.out.println(" Ticket enviado exitosamente a la cola de impresión.");
                } else {
                    System.out.println(" Falló la comunicación con la impresora.");
                }
            } else {
                System.out.println(" Impresión cancelada por el usuario.");
            }
        } else {
            System.out.println(" No se encontró ninguna impresora instalada en el equipo.");
        }

        btnGenerarTicket.setVisible(true);
        btnCerrar.setVisible(true);
        btnCerrarX.setVisible(true);

        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}