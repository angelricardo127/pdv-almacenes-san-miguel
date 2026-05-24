package com.asm.servicio;

import com.asm.modelo.Venta;
import com.asm.modelo.DetalleVenta;
import com.asm.modelo.Producto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

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

            // 1. Crear y guardar la cabecera de la venta
            Venta venta = new Venta(idMetodoPago);
            session.persist(venta);
            session.flush(); // Forzar a MySQL a darnos el id_venta autoincrementable

            // 2. Procesar cada artículo del carrito virtual
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
}
