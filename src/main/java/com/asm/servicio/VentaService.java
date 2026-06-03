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

    /**
     * Procesa una venta completa: valida stock, descuenta inventario y guarda los tickets.
     * @param idMetodoPago ID del método de pago (Efectivo, Tarjeta, etc.)
     * @param carrito Lista de detalles transaccionales que se quieren vender
     * @return boolean true si la venta fue exitosa
     */
    public boolean registrarVentaCompleta(int idMetodoPago, List<DetalleVenta> carrito) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // 1. Calcular el total de la venta recorriendo el carrito
            double totalCompra = 0.0;
            for (DetalleVenta detalle : carrito) {
                totalCompra += (detalle.getCantidad() * detalle.getPrecioUnitario());
            }

            // 2. Crear y guardar la cabecera de la venta
            Venta venta = new Venta(idMetodoPago);
            venta.setTotal(totalCompra); // Inyectamos el total para evitar el NULL en la base de datos

            // Vincular la venta al usuario que tiene la sesión activa
            if (com.asm.modelo.SesionGlobal.getUsuarioActual() != null) {
                venta.setIdUsuario(com.asm.modelo.SesionGlobal.getUsuarioActual().getIdUsuario());
            }

            session.persist(venta);
            session.flush(); // Forzar a MySQL a darnos el id_venta autoincrementable

            // 3. Procesar cada artículo del carrito virtual
            for (DetalleVenta detalle : carrito) {
                // Consultar el estado actual del producto en la BD
                Producto producto = session.get(Producto.class, detalle.getIdProducto());

                if (producto == null) {
                    throw new RuntimeException("Error: El producto con ID " + detalle.getIdProducto() + " no existe.");
                }

                // EXCEPCIÓN E-1: Validar si hay suficiente stock disponible
                if (producto.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombreProducto()
                            + " (Disponibles: " + producto.getStock() + ")");
                }

                // RF-20: Descontar de forma automática el inventario
                producto.setStock(producto.getStock() - detalle.getCantidad());
                session.merge(producto);

                // Vincular el detalle con el ID de la venta que acabamos de registrar
                detalle.setIdVenta(venta.getIdVenta());
                session.persist(detalle);
            }

            // Si todo salió bien, guardamos los cambios de manera definitiva
            transaction.commit();
            return true;

        } catch (Exception e) {
            // Si algo falla (ej. stock insuficiente), deshacemos todo para no dejar datos corruptos
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("VENTA CANCELADA -> " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    /**
     * Recupera el historial completo de ventas registradas en la base de datos.
     * @return Lista de ventas para mostrar en los reportes
     */
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
     * Recupera el catálogo completo de productos desde la base de datos.
     * @return Lista de productos disponibles para vender
     */
    public List<Producto> obtenerProductos() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Producto", Producto.class).list();
        } catch (Exception e) {
            System.err.println("Error al obtener el catálogo de productos: " + e.getMessage());
            return java.util.Collections.emptyList();
        } finally {
            session.close();
        }
    }

    // Método alternativo de registro
    public void registrarVenta(Map<Integer, Integer> carrito, double totalVenta) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            // 1. Crear el registro principal de la Venta
            Venta nuevaVenta = new Venta();
            // Nota: La fecha se genera sola gracias al @CreationTimestamp en tu clase Venta
            nuevaVenta.setIdMetodoPago(1); // Asignamos 1 por defecto (Efectivo)
            nuevaVenta.setTotal(totalVenta); // Inyectamos el total calculado

            if (com.asm.modelo.SesionGlobal.getUsuarioActual() != null) {
                nuevaVenta.setIdUsuario(com.asm.modelo.SesionGlobal.getUsuarioActual().getIdUsuario());
            }

            session.persist(nuevaVenta);
            session.flush();

            // 2. Recorrer el carrito para descontar stock y crear los detalles
            for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
                int idProducto = entry.getKey();
                int cantidadVendida = entry.getValue();

                Producto producto = session.get(Producto.class, idProducto);

                if (producto != null) {
                    // --- A) DESCONTAR STOCK ---
                    int nuevoStock = producto.getStock() - cantidadVendida;
                    producto.setStock(nuevoStock);
                    session.merge(producto);

                    // --- B) GUARDAR EL DETALLE HISTÓRICO ---
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

    // 1. Buscar la venta principal
    public Venta obtenerVentaPorId(int idVenta) {
        Session session = sessionFactory.openSession();
        Venta v = session.get(Venta.class, idVenta);
        session.close();
        return v;
    }

    // 2. Buscar los detalles (qué productos compró en esa venta)
    public java.util.List<DetalleVenta> obtenerDetallesPorVenta(int idVenta) {
        Session session = sessionFactory.openSession();
        java.util.List<DetalleVenta> detalles = session.createQuery("FROM DetalleVenta WHERE idVenta = :id", DetalleVenta.class)
                .setParameter("id", idVenta)
                .list();
        session.close();
        return detalles;
    }

    // 3. Buscar el producto individual para armar el ticket
    public Producto obtenerProductoPorId(int idProducto) {
        Session session = sessionFactory.openSession();
        Producto p = session.get(Producto.class, idProducto);
        session.close();
        return p;
    }
}