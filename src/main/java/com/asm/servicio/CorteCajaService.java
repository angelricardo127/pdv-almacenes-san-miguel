package com.asm.servicio;

import com.asm.modelo.CorteCaja;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class CorteCajaService {

    private final SessionFactory sessionFactory;

    public CorteCajaService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * PASOS PARA APERTURA DE CAJA (RF-05)
     * 1. Validar que no exista un corte activo (monto_fisico == 0).
     * 2. Registrar el ID del usuario que opera la caja.
     * 3. Capturar el fondo inicial de dinero en efectivo.
     * 4. Registrar la fecha/hora de inicio y guardar en la BD.
     */
    public boolean abrirCaja(int idUsuario, double fondoInicial) {
        if (fondoInicial < 0) {
            throw new RuntimeException("El fondo inicial no puede ser una cantidad negativa.");
        }

        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Paso 1: Validar si ya hay una caja abierta (monto_fisico igual a 0)
            CorteCaja cajaAbierta = session.createQuery(
                            "FROM CorteCaja WHERE montoFisico = 0.0 ORDER BY idCorte DESC", CorteCaja.class)
                    .setMaxResults(1)
                    .uniqueResult();

            if (cajaAbierta != null) {
                throw new RuntimeException("Error: Ya existe un turno activo en el sistema. Debe cerrarse primero.");
            }

            // Pasos 2, 3 y 4: Instanciar y persistir el nuevo registro
            CorteCaja nuevoCorte = new CorteCaja(idUsuario, fondoInicial);
            session.persist(nuevoCorte);

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Fallo en Apertura: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    /**
     * PASOS PARA CIERRE DE CAJA (RF-06)
     * 1. Localizar el corte activo actual del sistema.
     * 2. Recibir la cantidad de dinero físico contado por el cajero.
     * 3. Actualizar la columna monto_fisico para dar por terminado el turno.
     */
    public boolean cerrarCaja(double montoRealContado) {
        if (montoRealContado < 0) {
            throw new RuntimeException("El monto físico no puede ser menor a cero.");
        }

        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Paso 1: Localizar el corte que sigue abierto
            CorteCaja corteActivo = session.createQuery(
                            "FROM CorteCaja WHERE montoFisico = 0.0 ORDER BY idCorte DESC", CorteCaja.class)
                    .setMaxResults(1)
                    .uniqueResult();

            if (corteActivo == null) {
                throw new RuntimeException("Error: No se encontró ningún turno abierto para proceder con el cierre.");
            }

            // Pasos 2 y 3: Asignar el dinero contado en físico y actualizar en la BD
            corteActivo.setMontoFisico(montoRealContado);
            session.merge(corteActivo);

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Fallo en Cierre: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    /**
     * PASO ADICIONAL: Registrar salida de efectivo (Retiros Manuales)
     * Permite ir acumulando los retiros de efectivo que se hagan durante el turno.
     */
    public boolean registrarRetiro(double montoRetiro) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            CorteCaja corteActivo = session.createQuery(
                            "FROM CorteCaja WHERE montoFisico = 0.0 ORDER BY idCorte DESC", CorteCaja.class)
                    .setMaxResults(1)
                    .uniqueResult();

            if (corteActivo == null) {
                throw new RuntimeException("No hay una caja activa para registrar este retiro.");
            }

            // Acumular el retiro manual
            corteActivo.setRetirosManuales(corteActivo.getRetirosManuales() + montoRetiro);
            session.merge(corteActivo);

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al registrar retiro: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    /**
     * Utilidad para obtener los datos del estado actual desde la interfaz de JavaFX
     */
    public CorteCaja obtenerCorteActual() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM CorteCaja ORDER BY idCorte DESC", CorteCaja.class)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
}