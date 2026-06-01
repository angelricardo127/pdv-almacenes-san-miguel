package com.asm.servicio;

import com.asm.modelo.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class UsuarioService {

    private SessionFactory sessionFactory;

    public UsuarioService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // MÉTODO 1: El original (Lo dejamos intacto para las pruebas de JUnit)
    // -------------------------------------------------------------------------
    public Usuario validarUsuario(String username, String password) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "FROM Usuario WHERE username = :user AND contrasena = :pass AND estatus = 1";
            Query<Usuario> query = session.createQuery(hql, Usuario.class);
            query.setParameter("user", username);
            query.setParameter("pass", password);

            Usuario usuarioEncontrado = query.uniqueResult();
            return usuarioEncontrado;

        } catch (Exception e) {
            System.err.println("Error al consultar credenciales: " + e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }

    // -------------------------------------------------------------------------
    // MÉTODO 2: El detallado (Especial para la pantalla de Login en JavaFX)
    // -------------------------------------------------------------------------
    public Usuario validarUsuarioDetallado(String username, String password) throws Exception {
        Session session = sessionFactory.openSession();

        try {
            // 1. Buscamos SOLO por el nombre de usuario
            String hql = "FROM Usuario WHERE username = :user";
            Query<Usuario> query = session.createQuery(hql, Usuario.class);
            query.setParameter("user", username);

            Usuario usuarioEncontrado = query.uniqueResult();

            // 2. Validamos si existe en la base de datos
            if (usuarioEncontrado == null) {
                throw new Exception("El usuario no existe o es incorrecto.");
            }

            // 3. Validamos la contraseña
            if (!usuarioEncontrado.getContrasena().equals(password)) {
                throw new Exception("Contraseña incorrecta. Inténtelo de nuevo.");
            }

            // 4. Validamos si está activo (estatus = 1)
            if (usuarioEncontrado.getEstatus() != 1) {
                throw new Exception("Usuario inactivo. Contacte al administrador.");
            }

            // Si pasa todos los filtros, regresamos el usuario al controlador
            return usuarioEncontrado;

        } finally {
            session.close();
        }
    }
}