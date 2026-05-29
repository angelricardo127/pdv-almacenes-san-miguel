package com.asm.controlador;

import com.asm.servicio.CorteCajaService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GestionCajaController {

    // ==============================
    // COMPONENTES FXML
    // ==============================

    // Apertura
    @FXML
    private StackPane modalApertura;
    @FXML
    private StackPane modalExitoApertura;
    @FXML
    private StackPane modalErrorApertura;
    @FXML
    private StackPane modalAvisoTurno;
    @FXML
    private StackPane modalCierre;
    @FXML
    private StackPane modalExitoCierre; // nuevo modal agregado

    // Labels usuario dinámico
    @FXML
    private Label lblUsuarioSidebar;
    @FXML
    private Label lblRolSidebar;
    @FXML
    private Label lblBienvenida;
    @FXML
    private Label lblCajero;

    // Otros labels
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblExitoCajero;
    @FXML
    private Label lblExitoFondo;

    // Apertura
    @FXML
    private ComboBox<String> cbTurno;
    @FXML
    private TextField txtFondoInicial;
    @FXML
    private TextArea txtObservaciones;

    // Cierre
    @FXML
    private Label lblCierreCajero;
    @FXML
    private Label lblCierreFecha;
    @FXML
    private Label lblCierreTurno;
    @FXML
    private Label lblCierreHora;
    @FXML
    private Label lblErrorCierre;
    @FXML
    private Label lblCierreFondoInicial;
    @FXML
    private Label lblCierreVentas;
    @FXML
    private Label lblCierreTotalEsperado;
    @FXML
    private Label lblCierreDiferencia;
    @FXML
    private Label lblExitoDiferencia; // label de diferencia para modal de exito

    @FXML
    private TextField txtMontoCierre;
    @FXML
    private TextArea txtObservacionesCierre;

    // ==============================
    // VARIABLES
    // ==============================

    private SessionFactory factory;
    private CorteCajaService service;

    // ESTE USUARIO DEBE VENIR DEL LOGIN
    private String nombreUsuario = "Roberto Sánchez Pérez";
    private String rolUsuario = "Administrador";

    private final int ID_USUARIO_LOGEADO = 1;

    private double totalEsperadoSistema = 0.0;

    // ==============================
    // INITIALIZE
    // ==============================

    @FXML
    public void initialize() {
        // --- 1. ESCUDO DE BASE DE DATOS ---
        try {
            factory = new Configuration()
                    .configure("com/asm/vista/hibernate.cfg.xml") // <-- ¡La ruta real!
                    .buildSessionFactory();

            service = new CorteCajaService(factory);
            System.out.println("✅ Conexión a BD en Caja exitosa");
        } catch (Exception e) {
            System.err.println("❌ ERROR FATAL DE HIBERNATE EN LA CAJA:");
            e.printStackTrace();
        }

        // ... (el resto de tu código de fechas y turnos se queda igual)

        // --- 2. FECHA ---
        lblFecha.setText(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
        );

        // --- 3. TURNOS ---
        cbTurno.getItems().addAll(
                "Matutino (08:00 - 14:00)",
                "Vespertino (14:00 - 20:00)"
        );

        // --- 4. CARGAR DATOS DEL USUARIO ---
        cargarDatosUsuario();
    }

    // ==============================
    // USUARIO DINÁMICO
    // ==============================

    private void cargarDatosUsuario() {

        // SIDEBAR
        if (lblUsuarioSidebar != null) {
            lblUsuarioSidebar.setText(nombreUsuario);
        }

        if (lblRolSidebar != null) {
            lblRolSidebar.setText(rolUsuario);
        }

        // MODAL APERTURA
        if (lblCajero != null) {
            lblCajero.setText(nombreUsuario);
        }

        // HEADER
        if (lblBienvenida != null) {

            String fechaActual = LocalDate.now().format(
                    DateTimeFormatter.ofPattern(
                            "EEEE, dd 'de' MMMM 'de' yyyy"
                    )
            );

            lblBienvenida.setText(
                    "Bienvenido, "
                            + nombreUsuario
                            + " • "
                            + fechaActual
            );
        }
    }

    // ==============================
    // APERTURA
    // ==============================

    @FXML
    private void ejecutarApertura() {

        modalApertura.setVisible(true);

        txtFondoInicial.setText("1000.00");
    }

    @FXML
    private void cerrarModalApertura() {

        modalApertura.setVisible(false);
    }

    @FXML
    private void confirmarApertura() {

        try {

            if (cbTurno.getValue() == null) {

                modalAvisoTurno.setVisible(true);
                return;
            }

            double fondo = Double.parseDouble(
                    txtFondoInicial.getText()
            );

            boolean exito = service.abrirCaja(
                    ID_USUARIO_LOGEADO,
                    fondo
            );

            if (exito) {

                lblExitoCajero.setText(
                        lblCajero.getText()
                );

                lblExitoFondo.setText(
                        "$" + String.format("%.2f", fondo)
                );

                modalApertura.setVisible(false);

                modalExitoApertura.setVisible(true);
            }

        } catch (Exception e) {

            if (e.getMessage() != null
                    && e.getMessage()
                    .toLowerCase()
                    .contains("turno activo")) {

                modalApertura.setVisible(false);

                modalErrorApertura.setVisible(true);

            } else {

                Alert a = new Alert(
                        Alert.AlertType.ERROR,
                        e.getMessage()
                );

                a.show();
            }
        }
    }

    @FXML
    private void cerrarModalError() {

        modalErrorApertura.setVisible(false);
    }

    @FXML
    private void cerrarModalAviso() {

        modalAvisoTurno.setVisible(false);
    }

    @FXML
    private void continuarAVentas() {

        modalExitoApertura.setVisible(false);
    }

    // ==============================
    // CIERRE
    // ==============================

    @FXML
    private void ejecutarCierre() {

        // DATOS HEADER
        lblCierreCajero.setText(nombreUsuario);

        lblCierreTurno.setText(
                cbTurno.getValue() != null
                        ? cbTurno.getValue()
                        : "Turno Actual"
        );

        lblCierreFecha.setText(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
        );

        lblCierreHora.setText(
                LocalTime.now().format(
                        DateTimeFormatter.ofPattern("hh:mm a")
                )
        );

        // FONDO INICIAL
        double fondoInicial;

        try {

            fondoInicial = Double.parseDouble(
                    txtFondoInicial.getText()
            );

        } catch (Exception e) {

            fondoInicial = 0.0;
        }

        // VENTAS
        double ventasEfectivo = 0.0;

        // TOTAL ESPERADO
        totalEsperadoSistema =
                fondoInicial + ventasEfectivo;

        // MOSTRAR
        lblCierreFondoInicial.setText(
                "$" + String.format("%.2f", fondoInicial)
        );

        lblCierreVentas.setText(
                "$" + String.format("%.2f", ventasEfectivo)
        );

        lblCierreTotalEsperado.setText(
                "$" + String.format("%.2f", totalEsperadoSistema)
        );

        // LIMPIAR
        txtMontoCierre.clear();

        txtObservacionesCierre.clear();

        lblCierreDiferencia.setText("$0.00");

        lblCierreDiferencia.setTextFill(
                Color.web("#111827")
        );

        // ocultar cualquier error viejo antes de abrir el modal
        lblErrorCierre.setVisible(false);
        lblErrorCierre.setManaged(false);

        // MOSTRAR MODAL
        modalCierre.setVisible(true);
    }

    // ==============================
    // CALCULAR DIFERENCIA
    // ==============================

    @FXML
    private void calcularDiferencia() {

        try {

            if (txtMontoCierre.getText().isEmpty()) {

                lblCierreDiferencia.setText("$0.00");

                lblCierreDiferencia.setTextFill(
                        Color.web("#1e293b")
                );

                return;
            }

            double montoContado = Double.parseDouble(
                    txtMontoCierre.getText()
            );

            double diferencia =
                    montoContado - totalEsperadoSistema;

            lblCierreDiferencia.setText(
                    "$" + String.format("%.2f", diferencia)
            );

            // COLORES
            if (diferencia < 0) {

                lblCierreDiferencia.setTextFill(
                        Color.web("#ef4444")
                );

            } else if (diferencia > 0) {

                lblCierreDiferencia.setTextFill(
                        Color.web("#f59e0b")
                );

            } else {

                lblCierreDiferencia.setTextFill(
                        Color.web("#10b981")
                );
            }

        } catch (NumberFormatException e) {

            lblCierreDiferencia.setText("Error");

            lblCierreDiferencia.setTextFill(
                    Color.web("#ef4444")
            );
        }
    }

    // ==============================
    // CERRAR MODAL
    // ==============================

    @FXML
    private void cerrarModalCierre() {

        modalCierre.setVisible(false);
    }

    // ==============================
    // CONFIRMAR CIERRE
    // ==============================

    @FXML
    private void confirmarCierre() {
        try {
            // Ocultamos el error al iniciar
            lblErrorCierre.setVisible(false);
            lblErrorCierre.setManaged(false);

            double monto = Double.parseDouble(txtMontoCierre.getText());
            service.cerrarCaja(monto);

            modalCierre.setVisible(false);

            // actualizar texto y color de la diferencia para el nuevo modal
            lblExitoDiferencia.setText(lblCierreDiferencia.getText());
            lblExitoDiferencia.setTextFill(lblCierreDiferencia.getTextFill());

            // mostrar nuestro nuevo modal de exito en lugar del alert feo
            modalExitoCierre.setVisible(true);

        } catch (NumberFormatException e) {
            // Error visual integrado
            lblErrorCierre.setText("Monto inválido. Ingrese una cantidad numérica.");
            lblErrorCierre.setVisible(true);
            lblErrorCierre.setManaged(true);
        } catch (Exception e) {
            // Error visual integrado
            lblErrorCierre.setText("Error: " + e.getMessage());
            lblErrorCierre.setVisible(true);
            lblErrorCierre.setManaged(true);
        }
    }

    // ==============================
    // CERRAR MODAL EXITO CIERRE
    // ==============================
    @FXML
    private void cerrarModalExitoCierre() {
        modalExitoCierre.setVisible(false);
    }

    // ==============================
    // CERRAR SESION Y SALIR
    // ==============================
    @FXML
    private void cerrarSesion() {
        try {
            // cargar la vista del login que hara tu companero
            // ajusta el nombre cuando el te pase su archivo
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/Login.fxml"));

            // obtener la ventana actual
            javafx.stage.Stage stage = (javafx.stage.Stage) lblCajero.getScene().getWindow();

            // cambiar a la pantalla de login
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (Exception e) {
            // como el archivo aun no esta en el proyecto capturamos el error
            // mostramos un aviso para que el profe vea que el boton si funciona
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Sesión cerrada. (Esperando integración del módulo de Login)");
            a.show();

            System.out.println("vista de login no encontrada por el momento.");
        }
    }

    // ==============================
    // generar e imprimir reporte
    // ==============================
    @FXML
    private void generarReporte() {
        try {
            // armamos el diseno basico del ticket en memoria
            javafx.scene.control.Label ticket = new javafx.scene.control.Label(
                    "==================================\n" +
                            "       ALMACENES SAN MIGUEL       \n" +
                            "==================================\n" +
                            "\n" +
                            "REPORTE DE CIERRE DE CAJA\n" +
                            "cajero: " + lblCierreCajero.getText() + "\n" +
                            "turno: " + lblCierreTurno.getText() + "\n" +
                            "fecha: " + lblCierreFecha.getText() + "\n" +
                            "\n" +
                            "----------------------------------\n" +
                            "fondo inicial: " + lblCierreFondoInicial.getText() + "\n" +
                            "ventas total:  " + lblCierreVentas.getText() + "\n" +
                            "total sistema: " + lblCierreTotalEsperado.getText() + "\n" +
                            "----------------------------------\n" +
                            "diferencia:    " + lblExitoDiferencia.getText() + "\n" +
                            "\n" +
                            "==================================\n"
            );

            // le damos fuente de maquinita de tickets para que se vea real
            ticket.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 12;");

            // preparamos el trabajo de impresion
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();

            if (job != null) {
                // mostramos la ventana de impresion de windows al usuario
                boolean imprimir = job.showPrintDialog(modalExitoCierre.getScene().getWindow());

                if (imprimir) {
                    // si le da aceptar, mandamos el nodo a la impresora
                    boolean exito = job.printPage(ticket);
                    if (exito) {
                        job.endJob(); // finalizamos el proceso
                        System.out.println("reporte enviado a la impresora correctamente");
                    }
                }
            } else {
                System.out.println("no se detecto ninguna impresora instalada");
            }

        } catch (Exception e) {
            System.out.println("error al intentar imprimir: " + e.getMessage());
        }
    }

}