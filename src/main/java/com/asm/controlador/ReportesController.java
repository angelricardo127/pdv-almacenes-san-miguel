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
import java.util.Locale;

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
        // sescion global y fecha
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();

        if (lblBienvenida != null && usuarioLogueado != null) {
            String nombreRol = (usuarioLogueado.getIdRol() == 1) ? "Administrador" : (usuarioLogueado.getIdRol() == 2) ? "Cajero" : "Almacenista";
            lblBienvenida.setText("Bienvenido, " + nombreRol + " (" + usuarioLogueado.getNombre() + ") • " + fechaHoy);
        } else if (lblBienvenida != null) {
            lblBienvenida.setText("Bienvenido • " + fechaHoy);
        }
        // ------------------------------------

        // Configuramos la tabla para que lea el arreglo de Strings
        colFolio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
        colFecha.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[1]));
        colCajero.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[2]));
        colProductos.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[3]));
        colMetodo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[4]));
        colTotal.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[5]));

        // conectamos a al base  de datos
        try {
            factory = new Configuration().configure("com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
            service = new ReportesService(factory);
            System.out.println(" Conexión a BD en Reportes exitosa");

            // Precargamos las fechas de hoy en los calendarios
            dpFechaDesde.setValue(LocalDate.now());
            dpFechaHasta.setValue(LocalDate.now());

            cargarKpisDelDia();
            cargarEstadisticasSecundarias();

        } catch (Exception e) {
            System.err.println(" ERROR DE HIBERNATE EN REPORTES:");
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
            // Devoluciones se queda en 0 por ahora hasta conectar el módulo real
            lblDevoluciones.setText("0");
        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar KPIs superiores: " + e.getMessage());
        }
    }

    private void cargarEstadisticasSecundarias() {
        try {
            // 1. CARGAR PRODUCTOS MÁS VENDIDOS
            List<Object[]> topProductos = service.obtenerTopProductos();
            vboxTopProductos.getChildren().removeIf(node -> node instanceof Label && !((Label) node).getText().equals("Productos Más Vendidos"));

            if (topProductos.isEmpty()) {
                vboxTopProductos.getChildren().add(new Label("No hay ventas registradas aún."));
            } else {
                int rank = 1;
                for (Object[] row : topProductos) {
                    Label lbl = new Label(rank + ". " + row[0] + " (" + row[1] + " uds)");
                    lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569;");
                    vboxTopProductos.getChildren().add(lbl);
                    rank++;
                }
            }

            // 2. CARGAR MÉTODOS DE PAGO
            List<Object[]> metodosPago = service.obtenerVentasPorMetodoPago();
            vboxMetodosPago.getChildren().removeIf(node -> node instanceof HBox);
            for (Object[] row : metodosPago) {
                HBox hbox = new HBox();
                Label nombre = new Label(String.valueOf(row[0]).toUpperCase());
                nombre.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label total = new Label("$ " + String.format("%.2f", Double.parseDouble(String.valueOf(row[1]))));
                total.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");
                hbox.getChildren().addAll(nombre, spacer, total);
                vboxMetodosPago.getChildren().add(hbox);
            }

            // 3. CARGAR RENDIMIENTO POR CAJERO
            List<Object[]> cajeros = service.obtenerRendimientoCajeros();
            vboxCajeros.getChildren().removeIf(node -> node instanceof HBox);
            for (Object[] row : cajeros) {
                HBox hbox = new HBox();
                Label nombre = new Label(String.valueOf(row[0]));
                nombre.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label total = new Label("$ " + String.format("%.2f", Double.parseDouble(String.valueOf(row[1]))));
                total.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #10b981;");
                hbox.getChildren().addAll(nombre, spacer, total);
                vboxCajeros.getChildren().add(hbox);
            }

            // Ocultamos la categoría vacía por ahora para mantener el diseño limpio
            vboxCategorias.setVisible(false);
            vboxCategorias.setManaged(false);

        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar estadísticas secundarias: " + e.getMessage());
        }
    }

    @FXML
    public void generarReporte() {
        if (dpFechaDesde.getValue() == null || dpFechaHasta.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Fechas incompletas");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione Fecha Desde y Fecha Hasta para filtrar.");
            alert.showAndWait();
            return;
        }

        try {
            // Aquí es donde está ocurriendo el problema de la tabla vacía
            List<Object[]> resultadosBD = service.obtenerVentasPorRango(dpFechaDesde.getValue(), dpFechaHasta.getValue());
            ObservableList<String[]> datosReales = FXCollections.observableArrayList();
            double sumaTotal = 0.0;

            for (Object[] fila : resultadosBD) {
                String folio = String.valueOf(fila[0]);
                String fecha = String.valueOf(fila[1]);
                String cajero = String.valueOf(fila[2]);
                String metodo = String.valueOf(fila[3]);
                double total = Double.parseDouble(String.valueOf(fila[4]));

                sumaTotal += total;

                datosReales.add(new String[]{"#" + folio, fecha, cajero, "Resumen de ticket", metodo, "$ " + String.format("%.2f", total)});
            }

            tablaReporte.setItems(datosReales);
            lblTotalVentasEncontradas.setText("Total de tickets procesados: " + datosReales.size());
            lblSumaTotal.setText("$ " + String.format("%.2f", sumaTotal));

            modalReporte.setVisible(true);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Sistema");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al generar el reporte de base de datos.");
            alert.showAndWait();
        }
    }

    @FXML
    public void cerrarModalReporte() {
        modalReporte.setVisible(false);
    }

    @FXML
    public void imprimirReporte() {
        if (tablaReporte.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Reporte Vacío");
            alert.setHeaderText(null);
            alert.setContentText("No hay datos para imprimir en este rango de fechas.");
            alert.showAndWait();
            return;
        }

        try {
            StringBuilder contenidoImpresion = new StringBuilder();
            contenidoImpresion.append("========================================================================\n");
            contenidoImpresion.append("                      ALMACENES SAN MIGUEL\n");
            contenidoImpresion.append("                  REPORTE DE VENTAS FILTRADAS\n");
            contenidoImpresion.append("            Del: ").append(dpFechaDesde.getValue()).append(" Al: ").append(dpFechaHasta.getValue()).append("\n");
            contenidoImpresion.append("========================================================================\n\n");

            contenidoImpresion.append(String.format("%-10s %-20s %-20s %-10s\n", "FOLIO", "FECHA/HORA", "CAJERO", "TOTAL"));
            contenidoImpresion.append("------------------------------------------------------------------------\n");

            for (String[] fila : tablaReporte.getItems()) {
                contenidoImpresion.append(String.format("%-10s %-20s %-20s %-10s\n", fila[0], fila[1], fila[2], fila[5]));
            }

            contenidoImpresion.append("------------------------------------------------------------------------\n");
            contenidoImpresion.append("TOTAL RECAUDADO EN EL PERIODO: ").append(lblSumaTotal.getText()).append("\n");
            contenidoImpresion.append("TICKETS EMITIDOS: ").append(tablaReporte.getItems().size()).append("\n");
            contenidoImpresion.append("========================================================================\n");
            contenidoImpresion.append("              Reporte generado por: ").append(com.asm.modelo.SesionGlobal.getUsuarioActual().getNombre()).append("\n");

            Text nodoImpresion = new Text(contenidoImpresion.toString());
            nodoImpresion.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 11;");

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean proceder = job.showPrintDialog(modalReporte.getScene().getWindow());
                if (proceder) {
                    boolean exito = job.printPage(nodoImpresion);
                    if (exito) {
                        job.endJob();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Impresión Exitosa");
                        alert.setHeaderText(null);
                        alert.setContentText("El reporte ha sido enviado a la impresora correctamente.");
                        alert.showAndWait();
                    }
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Impresión");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un fallo de comunicación con la impresora del sistema.");
            alert.showAndWait();
        }
    }
}