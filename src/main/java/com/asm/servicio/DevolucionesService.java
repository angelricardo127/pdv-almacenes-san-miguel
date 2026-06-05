package com.asm.servicio;

import com.asm.modelo.TicketPreview;
import com.asm.modelo.Producto; // Importamos tu clase Producto
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import com.asm.modelo.DetalleTicketPreview;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DevolucionesService {

    private SessionFactory sessionFactory;

    public DevolucionesService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Hace un JOIN masivo en la BD para traer los tickets listos para la interfaz visual.
     */
    public List<TicketPreview> obtenerHistorialTickets() {
        List<TicketPreview> lista = new ArrayList<>();

        // Consulta SQL pura. Suma los totales y cuenta los productos directamente en MySQL.
        String sql = "SELECT " +
                "v.id_venta, " +
                "v.fecha, " +
                "CONCAT(u.nombre, ' ', u.apellido_paterno) AS cajero, " +
                "SUM(d.cantidad * d.precio_unitario) AS total, " +
                "SUM(d.cantidad) AS cantidadProductos, " +
                "m.nombre_metodo " +
                "FROM venta v " +
                "JOIN usuario u ON v.id_usuario = u.id_usuario " +
                "JOIN metodo_pago m ON v.id_metodo_pago = m.id_metodo_pago " +
                "JOIN detalle_de_venta d ON v.id_venta = d.id_venta " +
                "GROUP BY v.id_venta, v.fecha, u.nombre, u.apellido_paterno, m.nombre_metodo " +
                "ORDER BY v.fecha DESC";

        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
            List<Object[]> resultados = query.getResultList();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // Convertimos cada fila que regresó MySQL en el objeto TicketPreview
            for (Object[] fila : resultados) {
                String idVenta = String.valueOf(fila[0]);

                String fechaFormateada = "Fecha no registrada"; // Texto por defecto
                if (fila[1] != null) {
                    fechaFormateada = sdf.format((java.util.Date) fila[1]);
                }

                String cajero = (String) fila[2];
                double total = ((Number) fila[3]).doubleValue();
                int cantidad = ((Number) fila[4]).intValue();
                String metodo = (String) fila[5];

                lista.add(new TicketPreview(idVenta, fechaFormateada, cajero, total, cantidad, metodo));
            }
        } catch (Exception e) {
            System.err.println(" Error al obtener el historial de ventas: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public List<DetalleTicketPreview> obtenerDetallesVenta(String idVenta) {
        List<DetalleTicketPreview> lista = new java.util.ArrayList<>();

        // Agregamos p.id_producto al inicio del SELECT
        String sql = "SELECT p.id_producto, p.nombre_product, d.cantidad, d.precio_unitario, (d.cantidad * d.precio_unitario) AS subtotal " +
                "FROM detalle_de_venta d " +
                "JOIN productos p ON d.id_producto = p.id_producto " +
                "WHERE d.id_venta = " + idVenta;

        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
            List<Object[]> resultados = query.getResultList();

            for (Object[] fila : resultados) {
                int idProd = ((Number) fila[0]).intValue();
                String nombre = (String) fila[1];
                int cantidad = ((Number) fila[2]).intValue();
                double precio = ((Number) fila[3]).doubleValue();
                double subtotal = ((Number) fila[4]).doubleValue();

                lista.add(new DetalleTicketPreview(idProd, nombre, cantidad, precio, subtotal));
            }
        } catch (Exception e) {
            System.err.println(" Error al obtener los detalles: " + e.getMessage());
        }
        return lista;
    }


     //Trae todas las prendas de la base de datos mapeadas directamente como entidades Producto.

    public List<Producto> obtenerProductosDisponibles() {
        List<Producto> lista = new ArrayList<>();

        // Consulta nativa apuntando a la tabla 'productos'
        String sql = "SELECT * FROM productos";

        try (Session session = sessionFactory.openSession()) {
            // Le pasamos Producto.class para que Hibernate se encargue de construir los objetos automáticamente
            NativeQuery<Producto> query = session.createNativeQuery(sql, Producto.class);
            lista = query.getResultList();

            System.out.println(" Módulo Devoluciones: Se cargaron " + lista.size() + " productos para el catálogo de cambios.");
        } catch (Exception e) {
            System.err.println(" Error al obtener los productos disponibles en Devoluciones: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    // metodo para guardar la devolucion
    public void registrarDevolucion(int idVenta, int idProducto, String motivo, double montoRetornado) {
        String sql = "INSERT INTO devolucion (id_ventaOriginal, id_producto, motivo, monto_retornado) " +
                "VALUES (:idVenta, :idProd, :motivo, :monto)";

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            NativeQuery<?> query = session.createNativeQuery(sql);
            query.setParameter("idVenta", idVenta);
            query.setParameter("idProd", idProducto);
            query.setParameter("motivo", motivo);
            query.setParameter("monto", montoRetornado);

            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(" Error al registrar la devolución: " + e.getMessage());
        }
    }
}