package com.asm.servicio;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class ReportesService {

    private final SessionFactory sessionFactory;

    public ReportesService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // 1. Obtener las ventas filtradas (Mucho más rápido leyendo el campo Total)
    public List<Object[]> obtenerVentasPorRango(LocalDate inicio, LocalDate fin) {
        try (Session session = sessionFactory.openSession()) {
            // Abarcamos desde el primer segundo del día inicio, hasta el último segundo del día fin
            LocalDateTime fechaInicio = inicio.atStartOfDay();
            LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

            String hql = "SELECT v.idVenta, v.fecha, concat(u.nombre, ' ', u.apellidoPaterno), mp.nombreMetodo, v.total " +
                    "FROM Venta v, Usuario u, MetodoPago mp " +
                    "WHERE v.idUsuario = u.idUsuario " +
                    "AND v.idMetodoPago = mp.idMetodoPago " +
                    "AND v.fecha BETWEEN :inicio AND :fin " +
                    "ORDER BY v.fecha DESC";

            return session.createQuery(hql, Object[].class)
                    .setParameter("inicio", fechaInicio)
                    .setParameter("fin", fechaFin)
                    .list();
        } catch (Exception e) {
            System.err.println("Error al filtrar ventas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 2. Obtener el total de dinero recaudado HOY
    public double obtenerTotalVentasHoy() {
        try (Session session = sessionFactory.openSession()) {
            LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
            LocalDateTime finDia = LocalDate.now().atTime(LocalTime.MAX);

            String hql = "SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin";
            Double total = session.createQuery(hql, Double.class)
                    .setParameter("inicio", inicioDia)
                    .setParameter("fin", finDia)
                    .uniqueResult();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    // 3. Obtener el número de transacciones (tickets) de HOY
    public long obtenerTransaccionesHoy() {
        try (Session session = sessionFactory.openSession()) {
            LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
            LocalDateTime finDia = LocalDate.now().atTime(LocalTime.MAX);

            String hql = "SELECT COUNT(v.idVenta) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin";
            Long conteo = session.createQuery(hql, Long.class)
                    .setParameter("inicio", inicioDia)
                    .setParameter("fin", finDia)
                    .uniqueResult();
            return conteo != null ? conteo : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // =========================================================================
    // CONSULTAS AVANZADAS PARA LA PARTE INFERIOR DEL DASHBOARD
    // =========================================================================

    // 4. Rendimiento por Cajero (Usuario)
    public List<Object[]> obtenerRendimientoCajeros() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT concat(u.nombre, ' ', u.apellidoPaterno), SUM(v.total) " +
                    "FROM Venta v, Usuario u " +
                    "WHERE v.idUsuario = u.idUsuario " +
                    "GROUP BY u.idUsuario, u.nombre, u.apellidoPaterno " +
                    "ORDER BY SUM(v.total) DESC";
            return session.createQuery(hql, Object[].class).setMaxResults(5).list();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // 5. Ventas por Método de Pago
    public List<Object[]> obtenerVentasPorMetodoPago() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT mp.nombreMetodo, SUM(v.total) " +
                    "FROM Venta v, MetodoPago mp " +
                    "WHERE v.idMetodoPago = mp.idMetodoPago " +
                    "GROUP BY mp.idMetodoPago, mp.nombreMetodo " +
                    "ORDER BY SUM(v.total) DESC";
            return session.createQuery(hql, Object[].class).list();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // 6. ¡ACTUALIZADO! Productos Más Vendidos
    public List<Object[]> obtenerTopProductos() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT p.nombreProducto, SUM(dv.cantidad) " +
                    "FROM DetalleVenta dv, Producto p " +
                    "WHERE dv.idProducto = p.idProducto " +
                    "GROUP BY p.idProducto, p.nombreProducto " +
                    "ORDER BY SUM(dv.cantidad) DESC";
            return session.createQuery(hql, Object[].class).setMaxResults(5).list();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Object[]> obtenerVentasPorCategoria() {
        return Collections.emptyList();
    }
}