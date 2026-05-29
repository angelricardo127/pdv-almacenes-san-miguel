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

    public Usuario validarUsuario(String username, String password) {
        // Abrimos la conexión
        Session session = sessionFactory.openSession();

        try {
            // 🚨 CORRECCIÓN AQUÍ: Cambiamos 'password' por 'contrasena' y agregamos el filtro de 'estatus = 1'
            String hql = "FROM Usuario WHERE username = :user AND contrasena = :pass AND estatus = 1";
            Query<Usuario> query = session.createQuery(hql, Usuario.class);
            query.setParameter("user", username);
            query.setParameter("pass", password);

            // uniqueResult() trae al usuario si lo encuentra, o devuelve 'null' si no existe
            Usuario usuarioEncontrado = query.uniqueResult();
            return usuarioEncontrado;

        } catch (Exception e) {
            System.err.println("Error al consultar credenciales: " + e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }
}