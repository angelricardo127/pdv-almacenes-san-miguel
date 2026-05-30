package com.asm.servicio;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DashboardService {

    private SessionFactory sessionFactory;

    // constructor que recibe la conexion a la base de datos
    public DashboardService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // metodo para contar los usuarios activos en el sistema
    public long obtenerTotalUsuariosActivos() {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT COUNT(u) FROM Usuario u WHERE u.estatus = 1";
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("error al contar usuarios: " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }

    // metodo para sumar todo el stock fisico de tus productos
    public long obtenerTotalStock() {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT SUM(p.stock) FROM Producto p";
            Query<Long> query = session.createQuery(hql, Long.class);
            Long total = query.uniqueResult();

            // si no hay productos regresa 0 para evitar errores nulos
            return total != null ? total : 0;
        } catch (Exception e) {
            System.err.println("error al sumar stock: " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }

    // metodo para contar cuantas ventas se realizaron hoy
    public long obtenerVentasDelDia() {
        Session session = sessionFactory.openSession();
        try {
            // calculamos la fecha de hoy a la medianoche para filtrar
            LocalDate hoy = LocalDate.now();
            Date inicioDia = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());

            String hql = "SELECT COUNT(v) FROM Venta v WHERE v.fecha >= :inicioDia";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("inicioDia", inicioDia);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("error al contar ventas de hoy: " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }
}