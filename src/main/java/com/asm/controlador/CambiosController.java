package com.asm.controlador;

import com.asm.modelo.TicketPreview;
import com.asm.servicio.DevolucionesService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import com.asm.modelo.DetalleTicketPreview;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CambiosController {

    // Cambiamos lblFechaActual por lblBienvenida para conectar con la vista corregida
    @FXML private Label lblBienvenida;
    @FXML private ListView<TicketPreview> listaTickets;
    @FXML private VBox panelDetalle;

    private DevolucionesService servicio;

    @FXML
    public void initialize() {
        System.out.println("Módulo de Cambios y Devoluciones iniciado.");

        // --- LÓGICA DE SESIÓN Y FECHA ---
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();

        if (lblBienvenida != null && usuarioLogueado != null) {
            lblBienvenida.setText("Usuario activo: " + usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellidoPaterno() + " - " + fechaHoy);
        } else if (lblBienvenida != null) {
            lblBienvenida.setText("Usuario activo - " + fechaHoy);
        }
        // --------------------------------

        configurarDisenoLista();

        // 1. Configuramos Hibernate igual que en el módulo de Inventario
        try {
            Configuration configuration = new Configuration();
            configuration.configure("/com/asm/vista/hibernate.cfg.xml"); // Asegura la diagonal inicial

            // Aseguramos que la clase Venta esté registrada
            configuration.addAnnotatedClass(com.asm.modelo.Venta.class);

            SessionFactory factory = configuration.buildSessionFactory();
            servicio = new DevolucionesService(factory);

            // 2. Cargamos los datos reales
            cargarDatosReales();

        } catch (Exception e) {
            System.err.println("Error al cargar la BD en Devoluciones: " + e.getMessage());
        }
    }

    /**
     * Esta es la magia que transforma un dato simple en la tarjeta de Figma.
     */
    private void configurarDisenoLista() {
        listaTickets.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TicketPreview ticket, boolean empty) {
                super.updateItem(ticket, empty);

                if (empty || ticket == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // --- FILA 1: Ticket y Fecha ---
                    Label lblTicket = new Label("Ticket #" + ticket.getNumeroTicket());
                    lblTicket.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #111827;");

                    Label lblFecha = new Label(ticket.getFecha());
                    lblFecha.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");

                    Region spacer1 = new Region();
                    HBox.setHgrow(spacer1, Priority.ALWAYS);
                    HBox fila1 = new HBox(lblTicket, spacer1, lblFecha);

                    // --- FILA 2: Cajero y Total (En verde) ---
                    Label lblCajero = new Label("Cajero: " + ticket.getNombreCajero());
                    lblCajero.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 13px;");

                    Label lblTotal = new Label(String.format("$%.2f", ticket.getTotal()));
                    lblTotal.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 14px;");

                    Region spacer2 = new Region();
                    HBox.setHgrow(spacer2, Priority.ALWAYS);
                    HBox fila2 = new HBox(lblCajero, spacer2, lblTotal);

                    // --- FILA 3: Detalles (Gris pequeñito) ---
                    Label lblDetalles = new Label(ticket.getCantidadProductos() + " productos • " + ticket.getMetodoPago());
                    lblDetalles.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");

                    // Juntamos todas las filas en una caja vertical (La tarjeta)
                    VBox tarjeta = new VBox(8, fila1, fila2, lblDetalles);
                    tarjeta.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15; -fx-cursor: hand;");

                    // Efecto Hover
                    tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #D1D5DB; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15; -fx-cursor: hand;"));
                    tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15; -fx-cursor: hand;"));

                    setGraphic(tarjeta);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0 0 10 0;");
                }
            }
        });

        // Evento para cuando el usuario hace clic en una tarjeta
        listaTickets.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mostrarDetalleTicket(newValue);
            }
        });
    }

    private void cargarDatosReales() {
        if (servicio != null) {
            List<TicketPreview> ticketsBD = servicio.obtenerHistorialTickets();

            if (ticketsBD != null && !ticketsBD.isEmpty()) {
                listaTickets.getItems().addAll(ticketsBD);
                System.out.println("✅ Se cargaron " + ticketsBD.size() + " tickets desde MySQL.");
            } else {
                System.out.println("⚠️ No hay ventas registradas en la base de datos.");
            }
        }
    }

    private void mostrarDetalleTicket(TicketPreview ticket) {
        // 1. Limpiamos el panel derecho y le damos espaciado
        panelDetalle.getChildren().clear();
        panelDetalle.setSpacing(15);
        panelDetalle.setAlignment(Pos.TOP_LEFT);

        // 2. Título (Ticket #1001)
        Label lblTitulo = new Label("Ticket #" + ticket.getNumeroTicket());
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        // 3. Tarjeta Gris de Resumen
        HBox fila1Info = new HBox(40);
        Label lblFecha = new Label("Fecha: " + ticket.getFecha());
        Label lblCajero = new Label("Cajero: " + ticket.getNombreCajero());
        lblFecha.setStyle("-fx-text-fill: #475569; -fx-font-size: 14px;");
        lblCajero.setStyle("-fx-text-fill: #475569; -fx-font-size: 14px;");
        fila1Info.getChildren().addAll(lblFecha, lblCajero);

        HBox fila2Info = new HBox(40);
        Label lblTotalTxt = new Label("Total: ");
        lblTotalTxt.setStyle("-fx-text-fill: #475569; -fx-font-size: 14px;");
        Label lblTotalVal = new Label(String.format("$%.2f", ticket.getTotal()));
        lblTotalVal.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 14px;"); // Verde
        HBox cajaTotal = new HBox(lblTotalTxt, lblTotalVal);

        Label lblMetodo = new Label("Método: " + ticket.getMetodoPago());
        lblMetodo.setStyle("-fx-text-fill: #475569; -fx-font-size: 14px;");
        fila2Info.getChildren().addAll(cajaTotal, lblMetodo);

        VBox infoCard = new VBox(10, fila1Info, fila2Info);
        infoCard.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-padding: 20;");

        // 4. Sección "Productos a Devolver:"
        Label lblProdBox = new Label("Productos a Devolver:");
        lblProdBox.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1E293B;");

        VBox listaProductosUI = new VBox(10);
        List<DetalleTicketPreview> productos = servicio.obtenerDetallesVenta(ticket.getNumeroTicket());

        // MAPA MÁGICO: Guarda la relación entre CheckBox visual y producto
        java.util.Map<CheckBox, DetalleTicketPreview> mapaSeleccion = new java.util.HashMap<>();

        if(productos != null) {
            for(DetalleTicketPreview prod : productos) {
                HBox tarjetaProd = new HBox(15);
                tarjetaProd.setAlignment(Pos.CENTER_LEFT);
                tarjetaProd.setStyle("-fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-padding: 15; -fx-background-color: white;");

                CheckBox chkBox = new CheckBox();
                chkBox.setStyle("-fx-scale-x: 1.3; -fx-scale-y: 1.3; -fx-cursor: hand;");

                mapaSeleccion.put(chkBox, prod);

                Label icono = new Label("📦");
                icono.setStyle("-fx-font-size: 24px;");

                VBox infoProd = new VBox(3);
                Label lblNom = new Label(prod.getNombreProducto());
                lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #0F172A;");
                Label lblDet = new Label(String.format("$%.2f x %d", prod.getPrecioUnitario(), prod.getCantidad()));
                lblDet.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
                infoProd.getChildren().addAll(lblNom, lblDet);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label lblSub = new Label(String.format("$%.2f", prod.getSubtotal()));
                lblSub.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #0F172A;");

                tarjetaProd.getChildren().addAll(chkBox, icono, infoProd, spacer, lblSub);
                listaProductosUI.getChildren().add(tarjetaProd);
            }
        }

        // 5. Motivo del Cambio/Devolución
        Label lblMotivo = new Label("Motivo del Cambio/Devolución *");
        lblMotivo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0F172A;");
        TextArea txtMotivo = new TextArea();
        txtMotivo.setPromptText("Ej: Talla incorrecta, producto defectuoso, etc.");
        txtMotivo.setPrefRowCount(3);
        txtMotivo.setStyle("-fx-border-color: #CBD5E1; -fx-border-radius: 5; -fx-font-family: 'Segoe UI';");

        // 6. Botones de Acción
        HBox cajaBotones = new HBox(15);
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: white; -fx-border-color: #94A3B8; -fx-border-radius: 6; -fx-padding: 12 20; -fx-font-weight: bold; -fx-text-fill: #0F172A; -fx-cursor: hand;");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnCancelar, Priority.ALWAYS);

        btnCancelar.setOnAction(e -> {
            panelDetalle.getChildren().clear();
            Label placeholder = new Label("Seleccione un Ticket para continuar");
            placeholder.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 16px;");
            panelDetalle.getChildren().add(placeholder);
            panelDetalle.setAlignment(Pos.CENTER);
        });

        Button btnConfirmar = new Button("Confirmar Devolución");
        btnConfirmar.setStyle("-fx-background-color: #20C997; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6; -fx-padding: 12 20; -fx-cursor: hand;");
        btnConfirmar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnConfirmar, Priority.ALWAYS);

        // ====================================================================
        //  LA LÓGICA DE DEVOLUCIÓN (NUEVA CONEXIÓN AL TICKET MODAL)
        // ====================================================================
        btnConfirmar.setOnAction(e -> {
            String motivo = txtMotivo.getText();
            if (motivo == null || motivo.trim().isEmpty()) {
                System.err.println(" Debe escribir un motivo para la devolución.");
                return;
            }

            boolean seDevolvioAlgo = false;
            int idVenta = Integer.parseInt(ticket.getNumeroTicket());
            int cantidadProductosTotal = 0;
            double totalReembolsoCalculado = 0.0;

            // Revisamos cada checkbox del mapa
            for (java.util.Map.Entry<CheckBox, DetalleTicketPreview> entrada : mapaSeleccion.entrySet()) {
                if (entrada.getKey().isSelected()) {
                    DetalleTicketPreview prod = entrada.getValue();

                    // Mandamos a la BD
                    servicio.registrarDevolucion(idVenta, prod.getIdProducto(), motivo, prod.getSubtotal());
                    System.out.println("✅ Devolución registrada: " + prod.getNombreProducto());
                    seDevolvioAlgo = true;

                    // Sumamos para el ticket impreso
                    cantidadProductosTotal += prod.getCantidad();
                    totalReembolsoCalculado += prod.getSubtotal();
                }
            }

            if (!seDevolvioAlgo) {
                System.err.println(" Seleccione al menos un producto de la lista.");
            } else {
                System.out.println(" ¡Proceso de devolución completado exitosamente en BD!");

                // Levantamos la ventana del ticket elegante
                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/TicketDevolucion.fxml"));
                    javafx.scene.Parent root = loader.load();

                    // Le pasamos los datos calculados al modal
                    TicketDevolucionController ticketCtrl = loader.getController();
                    ticketCtrl.setDatosTicket(
                            ticket.getNumeroTicket(),
                            cantidadProductosTotal,
                            motivo,
                            totalReembolsoCalculado
                    );

                    javafx.stage.Stage stage = new javafx.stage.Stage();
                    stage.setTitle("Comprobante de Devolución");
                    stage.setScene(new javafx.scene.Scene(root));
                    stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                    stage.showAndWait();

                } catch (Exception ex) {
                    System.err.println("❌ Error al abrir el ticket elegante: " + ex.getMessage());
                    ex.printStackTrace();
                }

                // Limpiamos la pantalla derecha tras confirmar todo
                btnCancelar.fire();
            }
        });

        cajaBotones.getChildren().addAll(btnCancelar, btnConfirmar);

        // 7. Ensamblaje Final
        panelDetalle.getChildren().addAll(lblTitulo, infoCard, lblProdBox, listaProductosUI, lblMotivo, txtMotivo, cajaBotones);
    }

    @FXML
    public void prepararCambio() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/FormularioCambio.fxml"));
            javafx.scene.Parent root = loader.load();

            // Pasamos el túnel de MySQL al asistente de cambios
            FormularioCambioController asistente = loader.getController();
            asistente.setServicio(this.servicio);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Asistente de Cambios");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al abrir Asistente de Cambios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void prepararDevolucion() {
        System.out.println("ℹ️ Seleccione un ticket de la lista para proceder con la devolución.");
    }
}