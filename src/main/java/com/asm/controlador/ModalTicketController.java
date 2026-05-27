package com.asm.controlador;

import com.asm.modelo.Producto;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
        // 1. Poner la fecha y hora actual automáticamente
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        lblFechaHora.setText(dtf.format(LocalDateTime.now()));

        // 2. Darle función a los botones de cerrar
        btnCerrar.setOnAction(event -> cerrarVentana());
        btnCerrarX.setOnAction(event -> cerrarVentana());

        // 3. Simular la impresión
        btnGenerarTicket.setOnAction(event -> imprimirTicket());
    }

    // Este método lo llamaremos desde ModalPagoController justo después de confirmar el pago
    public void cargarDatosTicket(Map<Integer, Integer> cantidades, Map<Integer, Producto> productos,
                                  double total, double recibido, double cambio, String metodoPago) {

        lblSubtotalTicket.setText(String.format("$%.2f", total));
        lblTotalTicket.setText(String.format("$%.2f", total));
        lblRecibidoTicket.setText(String.format("$%.2f", recibido));
        lblCambioTicket.setText(String.format("$%.2f", cambio));
        lblMetodoPagoTicket.setText(metodoPago);

        // Limpiamos la lista por si acaso
        vboxListaArticulos.getChildren().clear();

        // Recorremos los productos comprados para dibujarlos en el ticket
        for (Integer id : cantidades.keySet()) {
            Producto p = productos.get(id);
            int cantidad = cantidades.get(id);
            double subtotalProducto = p.getPrecio() * cantidad;

            // Nombre del producto (Ej: Pantalón Gala Hombre)
            Label lblNombre = new Label(p.getNombreProducto());
            lblNombre.setFont(Font.font("Monospaced", 12));

            // Renglón inferior con cantidad y precio (Ej: 1 x $450.00       $450.00)
            HBox renglonDetalle = new HBox();

            Label lblCantPrecio = new Label(cantidad + " x " + String.format("$%.2f", p.getPrecio()));
            lblCantPrecio.setFont(Font.font("Monospaced", 12));

            // Un espacio flexible para empujar el total a la derecha
            Region separador = new Region();
            HBox.setHgrow(separador, Priority.ALWAYS);

            Label lblSubt = new Label(String.format("$%.2f", subtotalProducto));
            lblSubt.setFont(Font.font("Monospaced", 12));

            renglonDetalle.getChildren().addAll(lblCantPrecio, separador, lblSubt);

            // Juntamos el nombre y el detalle en un bloque y lo agregamos al ticket
            VBox bloqueProducto = new VBox(lblNombre, renglonDetalle);
            bloqueProducto.setSpacing(2);

            vboxListaArticulos.getChildren().add(bloqueProducto);
        }
    }

    private void imprimirTicket() {
        System.out.println("🖨️ Abriendo cuadro de diálogo de impresión...");

        // 1. Creamos el "trabajo de impresión" con la herramienta nativa de JavaFX
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) {
            // 2. Esto abre la ventana clásica de Windows de "Seleccionar Impresora"
            boolean mostrarDialogo = job.showPrintDialog(btnGenerarTicket.getScene().getWindow());

            if (mostrarDialogo) {
                // 3. ¡TRUCO DE MAGIA! Ocultamos los botones para que no salgan impresos en el papel
                btnGenerarTicket.setVisible(false);
                btnCerrar.setVisible(false);
                btnCerrarX.setVisible(false);

                // 4. Le tomamos una "foto" a la ventana y la mandamos a la impresora
                boolean impreso = job.printPage(btnGenerarTicket.getScene().getRoot());

                if (impreso) {
                    job.endJob(); // Sella y envía el documento
                    System.out.println("✅ Ticket enviado a la impresora exitosamente.");
                } else {
                    System.out.println("❌ Falló la comunicación con la impresora.");
                }

                // 5. Volvemos a mostrar los botones por si acaso
                btnGenerarTicket.setVisible(true);
                btnCerrar.setVisible(true);
                btnCerrarX.setVisible(true);
            } else {
                System.out.println("⚠️ Impresión cancelada por el usuario.");
            }
        } else {
            System.out.println("❌ No se encontró ninguna impresora configurada en esta computadora.");
        }

        // 6. Finalmente, cerramos la ventanita del ticket
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}