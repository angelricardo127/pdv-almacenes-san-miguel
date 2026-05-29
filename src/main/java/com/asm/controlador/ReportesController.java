package com.asm.controlador;

import com.asm.servicio.ReportesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.print.PrinterJob;
import javafx.scene.text.Text;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportesController {

    // --- FILTROS Y MODAL ---
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;
    @FXML private StackPane modalReporte;
    @FXML private Label lblTotalVentasEncontradas;
    @FXML private Label lblSumaTotal;
    @FXML private Label lblBienvenida;

    // --- KPIs DEL DASHBOARD (SUPERIOR) ---
    @FXML private Label lblVentasHoy;
    @FXML private Label lblTransacciones;
    @FXML private Label lblTicketPromedio;
    @FXML private Label lblDevoluciones;

    // --- CONTENEDORES DINÁMICOS DEL DASHBOARD (INFERIOR) ---
    @FXML private VBox vboxCategorias;
    @FXML private VBox vboxTopProductos;
    @FXML private VBox vboxMetodosPago;
    @FXML private VBox vboxCajeros;

    // --- TABLA ---
    @FXML private TableView<String[]> tablaReporte;
    @FXML private TableColumn<String[], String> colFolio;
    @FXML private TableColumn<String[], String> colFecha;
    @FXML private TableColumn<String[], String> colCajero;
    @FXML private TableColumn<String[], String> colProductos;
    @FXML private TableColumn<String[], String> colMetodo;
    @FXML private TableColumn<String[], String> colTotal;

    // --- CONEXIÓN A BD ---
    private SessionFactory factory;
    private ReportesService service;

    @FXML
    public void initialize() {
        lblBienvenida.setText("Bienvenido, Administrador • " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy")));

        // Configuramos la tabla para que lea el arreglo de Strings
        colFolio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
        colFecha.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[1]));
        colCajero.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[2]));
        colProductos.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[3]));
        colMetodo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[4]));
        colTotal.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[5]));

        // --- CONEXIÓN A LA BASE DE DATOS (El Escudo) ---
        try {
            // Usamos la ruta que descubrimos que funciona en tu proyecto
            factory = new Configuration().configure("com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
            service = new ReportesService(factory);
            System.out.println("✅ Conexión a BD en Reportes exitosa");

            // Si la conexión es exitosa, cargamos todo el Dashboard con datos reales
            cargarKpisDelDia();
            cargarEstadisticasSecundarias();

        } catch (Exception e) {
            System.err.println("❌ ERROR DE HIBERNATE EN REPORTES:");
            e.printStackTrace();
        }
    }

    private void cargarKpisDelDia() {
        try {
            double ventasHoy = service.obtenerTotalVentasHoy();
            long transaccionesHoy = service.obtenerTransaccionesHoy();
            double ticketPromedio = (transaccionesHoy > 0) ? (ventasHoy / transaccionesHoy) : 0.0;

            lblVentasHoy.setText("$ " + String.format("%.2f", ventasHoy));
            lblTransacciones.setText(String.valueOf(transaccionesHoy));
            lblTicketPromedio.setText("$ " + String.format("%.2f", ticketPromedio));
            // Devoluciones se queda en 0 por ahora hasta que tengas ese módulo
            lblDevoluciones.setText("0");
        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar KPIs superiores: " + e.getMessage());
        }
    }

    private void cargarEstadisticasSecundarias() {
        try {
            // 1. CARGAR PRODUCTOS MÁS VENDIDOS (Aún desactivado en el servicio)
            List<Object[]> topProductos = service.obtenerTopProductos();
            vboxTopProductos.getChildren().removeIf(node -> node instanceof Label && !((Label) node).getText().equals("Productos Más Vendidos"));
            int rank = 1;
            for (Object[] row : topProductos) {
                Label lbl = new Label(rank + ". " + row[0] + " (" + row[1] + " uds)");
                vboxTopProductos.getChildren().add(lbl);
                rank++;
            }

            // 2. CARGAR MÉTODOS DE PAGO (¡Ya con nombres y totales reales!)
            List<Object[]> metodosPago = service.obtenerVentasPorMetodoPago();
            vboxMetodosPago.getChildren().removeIf(node -> node instanceof HBox);
            for (Object[] row : metodosPago) {
                HBox hbox = new HBox();
                Label nombre = new Label(String.valueOf(row[0]));
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label total = new Label("$ " + String.format("%.2f", Double.parseDouble(String.valueOf(row[1]))));
                total.setStyle("-fx-font-weight: bold;");
                hbox.getChildren().addAll(nombre, spacer, total);
                vboxMetodosPago.getChildren().add(hbox);
            }

            // 3. CARGAR RENDIMIENTO POR CAJERO (¡Ya con nombres y totales reales!)
            List<Object[]> cajeros = service.obtenerRendimientoCajeros();
            vboxCajeros.getChildren().removeIf(node -> node instanceof HBox);
            for (Object[] row : cajeros) {
                HBox hbox = new HBox();
                Label nombre = new Label(String.valueOf(row[0]));
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label total = new Label("$ " + String.format("%.2f", Double.parseDouble(String.valueOf(row[1]))));
                total.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981;");
                hbox.getChildren().addAll(nombre, spacer, total);
                vboxCajeros.getChildren().add(hbox);
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar estadísticas secundarias: " + e.getMessage());
        }
    }

    @FXML
    public void generarReporte() {
        if (dpFechaDesde.getValue() == null || dpFechaHasta.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione Fecha Desde y Fecha Hasta.");
            alert.show();
            return;
        }

        try {
            List<Object[]> resultadosBD = service.obtenerVentasPorRango(dpFechaDesde.getValue(), dpFechaHasta.getValue());
            ObservableList<String[]> datosReales = FXCollections.observableArrayList();
            double sumaTotal = 0.0;

            for (Object[] fila : resultadosBD) {
                String folio = String.valueOf(fila[0]);
                String fecha = String.valueOf(fila[1]);
                String cajero = String.valueOf(fila[2]); // Hibernate ya trae "Roberto Sánchez"
                String metodo = String.valueOf(fila[3]); // Hibernate ya trae "Efectivo"
                double total = Double.parseDouble(String.valueOf(fila[4])); // Matemática real (Precio * Cantidad)

                sumaTotal += total;

                // El campo de productos lo dejamos así porque requeriría otra sub-consulta por ticket
                datosReales.add(new String[]{"#" + folio, fecha, cajero, "Ver ticket para detalle", metodo, "$ " + String.format("%.2f", total)});
            }

            tablaReporte.setItems(datosReales);
            lblTotalVentasEncontradas.setText("Total de ventas encontradas: " + datosReales.size());
            lblSumaTotal.setText("$ " + String.format("%.2f", sumaTotal));

            modalReporte.setVisible(true);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al generar reporte.\n" + e.getMessage());
            alert.show();
        }
    }

    @FXML
    public void cerrarModalReporte() {
        modalReporte.setVisible(false);
    }

    @FXML
    public void imprimirReporte() {
        try {
            StringBuilder contenidoImpresion = new StringBuilder();
            contenidoImpresion.append("========================================================================\n");
            contenidoImpresion.append("                      ALMACENES SAN MIGUEL\n");
            contenidoImpresion.append("                  REPORTE DE VENTAS FILTRADAS\n");
            contenidoImpresion.append("            Del: ").append(dpFechaDesde.getValue()).append(" Al: ").append(dpFechaHasta.getValue()).append("\n");
            contenidoImpresion.append("========================================================================\n\n");

            contenidoImpresion.append(String.format("%-8s %-20s %-20s %-10s\n", "FOLIO", "FECHA", "CAJERO", "TOTAL"));
            contenidoImpresion.append("------------------------------------------------------------------------\n");

            for (String[] fila : tablaReporte.getItems()) {
                // Aquí tomamos los índices que nos interesan para el ticket (0, 1, 2 y 5)
                contenidoImpresion.append(String.format("%-8s %-20s %-20s %-10s\n", fila[0], fila[1], fila[2], fila[5]));
            }

            contenidoImpresion.append("------------------------------------------------------------------------\n");
            contenidoImpresion.append("TOTAL RECAUDADO: ").append(lblSumaTotal.getText()).append("\n");
            contenidoImpresion.append("========================================================================\n");

            Text nodoImpresion = new Text(contenidoImpresion.toString());
            nodoImpresion.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 11;");

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean proceder = job.showPrintDialog(modalReporte.getScene().getWindow());
                if (proceder) {
                    boolean exito = job.printPage(nodoImpresion);
                    if (exito) {
                        job.endJob();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ El reporte ha sido enviado a la impresora correctamente.");
                        alert.show();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No se detectó ninguna impresora instalada en el sistema.");
                alert.show();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al imprimir: " + e.getMessage());
            alert.show();
        }
    }
}