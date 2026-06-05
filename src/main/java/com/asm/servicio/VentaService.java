package com.asm.servicio;

import com.asm.modelo.Venta;
import com.asm.modelo.DetalleVenta;
import com.asm.modelo.Producto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import java.util.Map;

public class VentaService {

    private final SessionFactory sessionFactory;

    public VentaService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean registrarVentaCompleta(int idMetodoPago, List<DetalleVenta> carrito) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            double totalCompra = 0.0;
            for (DetalleVenta detalle : carrito) {
                totalCompra += (detalle.getCantidad() * detalle.getPrecioUnitario());
            }

            Venta venta = new Venta(idMetodoPago);
            venta.setTotal(totalCompra);

            if (com.asm.modelo.SesionGlobal.getUsuarioActual() != null) {
                venta.setIdUsuario(com.asm.modelo.SesionGlobal.getUsuarioActual().getIdUsuario());
            }

            session.persist(venta);
            session.flush();

            for (DetalleVenta detalle : carrito) {
                Producto producto = session.get(Producto.class, detalle.getIdProducto());

                if (producto == null) {
                    throw new RuntimeException("Error: El producto con ID " + detalle.getIdProducto() + " no existe.");
                }

                if (producto.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombreProducto());
                }

                producto.setStock(producto.getStock() - detalle.getCantidad());
                session.merge(producto);

                detalle.setIdVenta(venta.getIdVenta());
                session.persist(detalle);
            }

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("VENTA CANCELADA -> " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    public List<Venta> obtenerHistorialVentas() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Venta", Venta.class).list();
        } catch (Exception e) {
            System.err.println("Error al obtener el historial de ventas: " + e.getMessage());
            return java.util.Collections.emptyList();
        } finally {
            session.close();
        }
    }

    /**
     * CORREGIDO: Ahora aplica el filtro 'WHERE activo = true' para sincronizarse
     * de manera exacta con el catálogo general del Inventario.
     */
    public List<Producto> obtenerProductos() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Producto WHERE activo = true", Producto.class).list();
        } catch (Exception e) {
            System.err.println("Error al obtener el catálogo de productos activos: " + e.getMessage());
            return java.util.Collections.emptyList();
        } finally {
            session.close();
        }
    }

    public void registrarVenta(Map<Integer, Integer> carrito, double totalVenta, int idMetodoPago) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Venta nuevaVenta = new Venta();
            nuevaVenta.setIdMetodoPago(idMetodoPago);
            nuevaVenta.setTotal(totalVenta);

            if (com.asm.modelo.SesionGlobal.getUsuarioActual() != null) {
                nuevaVenta.setIdUsuario(com.asm.modelo.SesionGlobal.getUsuarioActual().getIdUsuario());
            }

            session.persist(nuevaVenta);
            session.flush();

            for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
                int idProducto = entry.getKey();
                int cantidadVendida = entry.getValue();

                Producto producto = session.get(Producto.class, idProducto);

                if (producto != null) {
                    int nuevoStock = producto.getStock() - cantidadVendida;
                    producto.setStock(nuevoStock);
                    session.merge(producto);

                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setIdVenta(nuevaVenta.getIdVenta());
                    detalle.setIdProducto(producto.getIdProducto());
                    detalle.setCantidad(cantidadVendida);
                    detalle.setPrecioUnitario(producto.getPrecio());

                    session.persist(detalle);
                }
            }

            tx.commit();
            System.out.println("Transacción exitosa: Venta registrada y stock actualizado en MySQL.");

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error crítico al registrar la venta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Venta obtenerVentaPorId(int idVenta) {
        Session session = sessionFactory.openSession();
        Venta v = session.get(Venta.class, idVenta);
        session.close();
        return v;
    }

    public java.util.List<DetalleVenta> obtenerDetallesPorVenta(int idVenta) {
        Session session = sessionFactory.openSession();
        java.util.List<DetalleVenta> detalles = session.createQuery("FROM DetalleVenta WHERE idVenta = :id", DetalleVenta.class)
                .setParameter("id", idVenta)
                .list();
        session.close();
        return detalles;
    }

    public Producto obtenerProductoPorId(int idProducto) {
        Session session = sessionFactory.openSession();
        Producto p = session.get(Producto.class, idProducto);
        session.close();
        return p;
    }

    public double obtenerSumaVentasDelDia(int idMetodoPago) {
        Session session = sessionFactory.openSession();
        try {
            java.time.LocalDateTime inicioDia = java.time.LocalDate.now().atStartOfDay();
            java.time.LocalDateTime finDia = java.time.LocalDate.now().atTime(java.time.LocalTime.MAX);

            String hql = "SELECT SUM(v.total) FROM Venta v WHERE v.idMetodoPago = :metodo AND v.fecha >= :inicio AND v.fecha <= :fin";

            Double suma = session.createQuery(hql, Double.class)
                    .setParameter("metodo", idMetodoPago)
                    .setParameter("inicio", inicioDia)
                    .setParameter("fin", finDia)
                    .uniqueResult();

            return suma != null ? suma : 0.0;
        } catch (Exception e) {
            System.err.println(" Error al sumar ventas del día: " + e.getMessage());
            return 0.0;
        } finally {
            session.close();
        }
    }
}