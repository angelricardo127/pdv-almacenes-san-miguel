package com.asm.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardController {

    @FXML private Label lblBienvenida, lblVentasDia, lblProductosStock, lblUsuariosActivos, lblTotalUsuarios;
    private SessionFactory factory;

    @FXML
    public void initialize() {
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        if (lblBienvenida != null) lblBienvenida.setText("Bienvenido, Roberto Sánchez Pérez • " + fechaHoy);

        try {
            factory = new Configuration().configure("/com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
            cargarEstadisticas();
        } catch (Exception e) {
            System.err.println("⚠️ Hibernate no conectado. Mostrando datos por defecto.");
        }
    }

    private void cargarEstadisticas() {
        try (Session session = factory.openSession()) {
            // Usuarios
            Long activos = session.createQuery("select count(u) from Usuario u where u.estatus = 1", Long.class).uniqueResult();
            Long totales = session.createQuery("select count(u) from Usuario u", Long.class).uniqueResult();
            if (lblUsuariosActivos != null) lblUsuariosActivos.setText(activos != null ? activos.toString() : "0");
            if (lblTotalUsuarios != null) lblTotalUsuarios.setText("de " + (totales != null ? totales : 0) + " usuarios");

            // Productos (Asumiendo que tienes tu entidad Producto)
            Long productos = session.createQuery("select count(p) from Producto p", Long.class).uniqueResult();
            if (lblProductosStock != null) lblProductosStock.setText(productos != null ? productos.toString() : "0");

            // Ventas del día (Ahora usando el campo 'total' real de Venta)
            Double ventasHoy = session.createQuery("select sum(v.total) from Venta v where date(v.fecha) = current_date()", Double.class)
                    .uniqueResult();
            if (lblVentasDia != null) {
                lblVentasDia.setText(String.format("$%.2f", (ventasHoy != null ? ventasHoy : 0.0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}