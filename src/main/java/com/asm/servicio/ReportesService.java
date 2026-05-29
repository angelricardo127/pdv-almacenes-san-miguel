package com.asm.servicio;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ReportesService {

    private final SessionFactory sessionFactory;

    public ReportesService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // 1. Obtener las ventas filtradas (Con Nombres y Total real)
    public List<Object[]> obtenerVentasPorRango(LocalDate inicio, LocalDate fin) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT v.idVenta, v.fecha, concat(u.nombre, ' ', u.apellidoPaterno), mp.nombreMetodo, SUM(dv.cantidad * dv.precioUnitario) " +
                    "FROM Venta v, Usuario u, MetodoPago mp, DetalleVenta dv " +
                    "WHERE v.idUsuario = u.idUsuario " +
                    "AND v.idMetodoPago = mp.idMetodoPago " +
                    "AND v.idVenta = dv.idVenta " +
                    "AND DATE(v.fecha) BETWEEN :inicio AND :fin " +
                    "GROUP BY v.idVenta, v.fecha, u.nombre, u.apellidoPaterno, mp.nombreMetodo";

            return session.createQuery(hql, Object[].class)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    // 2. Obtener el total de dinero recaudado HOY
    public double obtenerTotalVentasHoy() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT SUM(dv.cantidad * dv.precioUnitario) " +
                    "FROM Venta v, DetalleVenta dv " +
                    "WHERE v.idVenta = dv.idVenta " +
                    "AND DATE(v.fecha) = :hoy";
            Double total = session.createQuery(hql, Double.class)
                    .setParameter("hoy", LocalDate.now())
                    .uniqueResult();
            return total != null ? total : 0.0;
        }
    }

    // 3. Obtener el número de transacciones (tickets) de HOY
    public long obtenerTransaccionesHoy() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(DISTINCT v.idVenta) FROM Venta v WHERE DATE(v.fecha) = :hoy";
            Long conteo = session.createQuery(hql, Long.class)
                    .setParameter("hoy", LocalDate.now())
                    .uniqueResult();
            return conteo != null ? conteo : 0;
        }
    }

    // =========================================================================
    // CONSULTAS AVANZADAS PARA LA PARTE INFERIOR DEL DASHBOARD
    // =========================================================================

    // 4. Rendimiento por Cajero (Usuario)
    public List<Object[]> obtenerRendimientoCajeros() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT concat(u.nombre, ' ', u.apellidoPaterno), SUM(dv.cantidad * dv.precioUnitario) " +
                    "FROM Venta v, Usuario u, DetalleVenta dv " +
                    "WHERE v.idVenta = dv.idVenta AND v.idUsuario = u.idUsuario " +
                    "GROUP BY u.idUsuario, u.nombre, u.apellidoPaterno " +
                    "ORDER BY SUM(dv.cantidad * dv.precioUnitario) DESC";
            return session.createQuery(hql, Object[].class).setMaxResults(5).list();
        }
    }

    // 5. Ventas por Método de Pago
    public List<Object[]> obtenerVentasPorMetodoPago() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT mp.nombreMetodo, SUM(dv.cantidad * dv.precioUnitario) " +
                    "FROM Venta v, MetodoPago mp, DetalleVenta dv " +
                    "WHERE v.idVenta = dv.idVenta AND v.idMetodoPago = mp.idMetodoPago " +
                    "GROUP BY mp.idMetodoPago, mp.nombreMetodo " +
                    "ORDER BY SUM(dv.cantidad * dv.precioUnitario) DESC";
            return session.createQuery(hql, Object[].class).list();
        }
    }

    // Pendientes hasta que tengamos la clase Producto.java
    public List<Object[]> obtenerTopProductos() {
        return Collections.emptyList();
    }
    public List<Object[]> obtenerVentasPorCategoria() {
        return Collections.emptyList();
    }
}