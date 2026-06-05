package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import javafx.geometry.Insets;
import com.asm.modelo.Producto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML private Label lblBienvenida;
    @FXML private Label lblVentasDia;
    @FXML private Label lblProductosStock;
    @FXML private Label lblUsuariosActivos;
    @FXML private Label lblTotalUsuarios;
    @FXML private VBox vboxTopProductos; // Contenedor dinámico enlazado al FXML
    @FXML private VBox vboxAvisos;
    private SessionFactory factory;

    @FXML
    public void initialize() {
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();

        if (lblBienvenida != null && usuarioLogueado != null) {
            lblBienvenida.setText("Bienvenido, " + usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellidoPaterno() + " - " + fechaHoy);
        } else if (lblBienvenida != null) {
            lblBienvenida.setText("Bienvenido - " + fechaHoy);
        }

        try {
            factory = new Configuration().configure("/com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
            cargarEstadisticas();
            cargarTopProductos();
            cargarAvisosReales();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEstadisticas() {
        try (Session session = factory.openSession()) {
            Long activos = session.createQuery("select count(u) from Usuario u where u.estatus = 1", Long.class).uniqueResult();
            Long totales = session.createQuery("select count(u) from Usuario u", Long.class).uniqueResult();
            if (lblUsuariosActivos != null) lblUsuariosActivos.setText(activos != null ? activos.toString() : "0");
            if (lblTotalUsuarios != null) lblTotalUsuarios.setText("de " + (totales != null ? totales : 0) + " usuarios");

            Long productos = session.createQuery("select count(p) from Producto p", Long.class).uniqueResult();
            if (lblProductosStock != null) lblProductosStock.setText(productos != null ? productos.toString() : "0");

            LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
            LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);

            Double ventasHoy = session.createQuery("select sum(v.total) from Venta v where v.fecha between :inicio and :fin", Double.class)
                    .setParameter("inicio", inicioDia)
                    .setParameter("fin", finDia)
                    .uniqueResult();

            if (lblVentasDia != null) {
                lblVentasDia.setText(String.format("$%.2f", (ventasHoy != null ? ventasHoy : 0.0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Consulta la base de datos de MySQL para obtener los 3 artículos con mayor cantidad de unidades vendidas
     */
    private void cargarTopProductos() {
        if (vboxTopProductos == null) return;
        vboxTopProductos.getChildren().clear();

        String sql = "SELECT p.nombre_product, SUM(d.cantidad) AS total_vendido " +
                "FROM detalle_de_venta d " +
                "JOIN productos p ON d.id_producto = p.id_producto " +
                "GROUP BY p.id_producto, p.nombre_product " +
                "ORDER BY total_vendido DESC LIMIT 3";

        try (Session session = factory.openSession()) {
            NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
            List<Object[]> resultados = query.getResultList();

            int posicion = 1;

            if (resultados.isEmpty()) {
                Label lblVacio = new Label("No se registran movimientos de venta en el sistema.");
                lblVacio.setFont(Font.font("System", 13));
                lblVacio.setTextFill(javafx.scene.paint.Color.web("#64748b"));
                vboxTopProductos.getChildren().add(lblVacio);
                return;
            }

            for (Object[] fila : resultados) {
                String nombreProducto = (String) fila[0];
                int cantidadVendida = ((Number) fila[1]).intValue();

                // Construcción de la fila contenedora con diseño Figma
                HBox filaTop = new HBox(15);
                filaTop.setAlignment(Pos.CENTER_LEFT);
                filaTop.setPadding(new Insets(10, 15, 10, 15));
                filaTop.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 8;");

                // Icono del podio (1º, 2º, 3º)
                String medalla = (posicion == 1) ? "🥇" : (posicion == 2) ? "🥈" : "🥉";
                Label lblMedalla = new Label(medalla + " #" + posicion);
                lblMedalla.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblMedalla.setPrefWidth(60);

                // Nombre del producto
                Label lblNombre = new Label(nombreProducto);
                lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblNombre.setTextFill(javafx.scene.paint.Color.web("#1e293b"));

                // Separador invisible expansible
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Contador de unidades
                Label lblCantidad = new Label(cantidadVendida + " uds");
                lblCantidad.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblCantidad.setTextFill(javafx.scene.paint.Color.web("#3b82f6"));
                lblCantidad.setStyle("-fx-background-color: #eff6ff; -fx-padding: 4 10; -fx-background-radius: 12;");

                filaTop.getChildren().addAll(lblMedalla, lblNombre, spacer, lblCantidad);
                vboxTopProductos.getChildren().add(filaTop);

                posicion++;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el Top de Productos en el Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarAvisosReales() {
        if (vboxAvisos == null) return;
        vboxAvisos.getChildren().clear();

        try (Session session = factory.openSession()) {
            // Consulta HQL: Busca uniformes que estén en riesgo de agotarse
            String hql = "FROM Producto p WHERE p.stock <= p.stockMinimo ORDER BY p.stock ASC";
            List<Producto> productosBajos = session.createQuery(hql, Producto.class)
                    .setMaxResults(3) // Top 3 avisos más urgentes
                    .getResultList();

            //
            HBox avisoCaja = new HBox(10);
            avisoCaja.setAlignment(Pos.TOP_LEFT);
            Label iconCaja = new Label("🟢");
            VBox textoCaja = new VBox(2);
            Label lblTituloCaja = new Label("Caja Activa");
            lblTituloCaja.setFont(Font.font("System", FontWeight.BOLD, 13));
            Label lblDescCaja = new Label("Turno operativo iniciado correctamente.");
            lblDescCaja.setWrapText(true);
            lblDescCaja.setTextFill(javafx.scene.paint.Color.web("#64748b"));
            lblDescCaja.setFont(Font.font("System", 12));
            textoCaja.getChildren().addAll(lblTituloCaja, lblDescCaja);
            avisoCaja.getChildren().addAll(iconCaja, textoCaja);
            vboxAvisos.getChildren().add(avisoCaja);

            //
            if (productosBajos.isEmpty()) {
                // Si todo el stock está sano
                HBox avisoSano = new HBox(10);
                avisoSano.setAlignment(Pos.TOP_LEFT);
                Label iconSano = new Label("✅");
                VBox textoSano = new VBox(2);
                Label lblTituloSano = new Label("Inventario Estable");
                lblTituloSano.setFont(Font.font("System", FontWeight.BOLD, 13));
                Label lblDescSano = new Label("Ningún producto se encuentra por debajo de su stock mínimo.");
                lblDescSano.setWrapText(true);
                lblDescSano.setTextFill(javafx.scene.paint.Color.web("#64748b"));
                lblDescSano.setFont(Font.font("System", 12));
                textoSano.getChildren().addAll(lblTituloSano, lblDescSano);
                avisoSano.getChildren().addAll(iconSano, textoSano);
                vboxAvisos.getChildren().add(avisoSano);
            } else {
                // Iteramos los productos que sí están bajos en tu BD
                for (Producto prod : productosBajos) {
                    HBox avisoProd = new HBox(10);
                    avisoProd.setAlignment(Pos.TOP_LEFT);

                    // Si el stock es 0 ponemos alerta roja, si es mayor pero menor al mínimo ponemos advertencia
                    String emoji = (prod.getStock() <= 0) ? "🚨" : "⚠️";
                    Label iconProd = new Label(emoji);

                    VBox textoProd = new VBox(2);
                    Label lblTituloProd = new Label("Stock Crítico: " + prod.getNombreProducto());
                    lblTituloProd.setFont(Font.font("System", FontWeight.BOLD, 13));

                    Label lblDescProd = new Label("Existencias actuales: " + prod.getStock() + " pzas (Mínimo requerido: " + prod.getStockMinimo() + ").");
                    lblDescProd.setWrapText(true);
                    lblDescProd.setTextFill(javafx.scene.paint.Color.web("#64748b"));
                    lblDescProd.setFont(Font.font("System", 12));

                    textoProd.getChildren().addAll(lblTituloProd, lblDescProd);
                    avisoProd.getChildren().addAll(iconProd, textoProd);
                    vboxAvisos.getChildren().add(avisoProd);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar avisos dinámicos en Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}