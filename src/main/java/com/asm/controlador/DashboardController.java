package com.asm.controlador;

import com.asm.servicio.DashboardService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// clase que maneja los cuadritos de resumen al iniciar sesion
public class DashboardController {

    // variables enlazadas a los textos de tu pantalla
    @FXML private Label lblBienvenida, lblVentasDia, lblProductosStock, lblUsuariosActivos, lblTotalUsuarios;

    // herramienta para conectar con tu base de datos
    private SessionFactory factory;

    // este metodo arranca solito cuando entras al panel
    @FXML
    public void initialize() {
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")));

        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();

        // cambiamos el mensaje para que sea dinamico
        if (lblBienvenida != null && usuarioLogueado != null) {
            lblBienvenida.setText("bienvenido, " + usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellidoPaterno() + " - " + fechaHoy);
        } else if (lblBienvenida != null) {
            lblBienvenida.setText("bienvenido - " + fechaHoy);
        }

        try {
            factory = new Configuration().configure("/com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
            cargarEstadisticas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // hace las consultas rapidas a mysql para rellenar los cuadros
    private void cargarEstadisticas() {
        try (Session session = factory.openSession()) {

            // cuenta cuantos empleados pueden entrar al sistema
            Long activos = session.createQuery("select count(u) from Usuario u where u.estatus = 1", Long.class).uniqueResult();
            Long totales = session.createQuery("select count(u) from Usuario u", Long.class).uniqueResult();
            if (lblUsuariosActivos != null) lblUsuariosActivos.setText(activos != null ? activos.toString() : "0");
            if (lblTotalUsuarios != null) lblTotalUsuarios.setText("de " + (totales != null ? totales : 0) + " usuarios");

            // cuenta toda la mercancia que tienes registrada
            Long productos = session.createQuery("select count(p) from Producto p", Long.class).uniqueResult();
            if (lblProductosStock != null) lblProductosStock.setText(productos != null ? productos.toString() : "0");

            // reparamos la consulta definiendo limites de tiempo exactos
            LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
            LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);

            // le pedimos a la base de datos que sume los totales que entren en ese limite
            Double ventasHoy = session.createQuery("select sum(v.total) from Venta v where v.fecha between :inicio and :fin", Double.class)
                    .setParameter("inicio", inicioDia)
                    .setParameter("fin", finDia)
                    .uniqueResult();

            // si no hubo ventas devuelve cero para que no truene el programa
            if (lblVentasDia != null) {
                lblVentasDia.setText(String.format("$%.2f", (ventasHoy != null ? ventasHoy : 0.0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}