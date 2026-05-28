package com.asm.servicio;

import com.asm.modelo.Producto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class InventarioService {

    private final SessionFactory sessionFactory;

    public InventarioService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Registra un producto completamente nuevo en el catálogo.
     */
    public boolean registrarNuevoProducto(Producto producto) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.persist(producto);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error al registrar producto: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    /**
     * Actualiza la cantidad de stock de un producto existente (Ej: Llegó un nuevo camión).
     */
    public boolean agregarStock(int idProducto, int cantidadAgregada) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Producto producto = session.get(Producto.class, idProducto);

            if (producto == null) {
                throw new RuntimeException("Producto no encontrado.");
            }

            // Sumamos el stock actual más lo nuevo que llegó
            producto.setStock(producto.getStock() + cantidadAgregada);
            session.merge(producto); // Actualiza en MySQL

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    /**
     * Recupera todos los productos para mostrarlos en la tabla de inventario de JavaFX.
     */
    public List<Producto> obtenerCatalogoCompleto() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Producto", Producto.class).list();
        } catch (Exception e) {
            System.err.println("Error al obtener catálogo: " + e.getMessage());
            return java.util.Collections.emptyList();
        } finally {
            session.close();
        }
    }

    /**
     * metodo que actualiza un producto existente en la base de datos (UPDATE).
     */
    public void actualizarProducto(Producto producto) {
        org.hibernate.Transaction transaccion = null;
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            transaccion = session.beginTransaction();

            // session.merge() es la magia de Hibernate para hacer el UPDATE
            // Busca el ID del producto y sobrescribe todos sus campos con los nuevos valores
            session.merge(producto);

            transaccion.commit(); // Confirmamos los cambios en MySQL
            System.out.println(" Hibernate: Producto actualizado con éxito en la BD.");
        } catch (Exception e) {
            if (transaccion != null) transaccion.rollback(); // Si algo falla, deshacemos por seguridad
            System.err.println(" Error de Hibernate al actualizar producto: " + e.getMessage());
            throw e;
        }
    }
}