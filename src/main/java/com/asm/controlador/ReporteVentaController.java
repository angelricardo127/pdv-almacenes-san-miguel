package com.asm.controlador;

import com.asm.modelo.Venta;
import com.asm.servicio.VentaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ReporteVentaController {

    @FXML
    private TableView<Venta> tablaReportes;

    @FXML
    private VBox contenedorEstadoVacio;

    private final ObservableList<Venta> listaVentasOberservable = FXCollections.observableArrayList();

    // Agregamos la instancia del servicio
    private VentaService ventaService;

    @FXML
    public void initialize() {
        // Inicializamos la conexión a la base de datos y el servicio
        SessionFactory sessionFactory = new Configuration().configure("com/asm/vista/hibernate.cfg.xml").buildSessionFactory();
        ventaService = new VentaService(sessionFactory);

        tablaReportes.setItems(listaVentasOberservable);

        // 1. Le pedimos al Service que traiga el historial de MySQL
        List<Venta> historial = ventaService.obtenerHistorialVentas();

        // 2. Mandamos esa información a la interfaz
        cargarDatosReporte(historial);
    }

    public void cargarDatosReporte(List<Venta> ventasDesdeBD) {
        if (ventasDesdeBD != null) {
            listaVentasOberservable.setAll(ventasDesdeBD);
        } else {
            listaVentasOberservable.clear();
        }
        actualizarVisibilidadInterfaz();
    }

    private void actualizarVisibilidadInterfaz() {
        if (listaVentasOberservable.isEmpty()) {
            // Activa el Empty State
            tablaReportes.setVisible(false);
            tablaReportes.setManaged(false);

            contenedorEstadoVacio.setVisible(true);
            contenedorEstadoVacio.setManaged(true);
        } else {
            // Muestra la tabla de datos
            tablaReportes.setVisible(true);
            tablaReportes.setManaged(true);

            contenedorEstadoVacio.setVisible(false);
            contenedorEstadoVacio.setManaged(false);
        }
    }
}