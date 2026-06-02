package com.asm.modelo;

public class SesionGlobal {

    // variable estática que mantiene al usuario en la memoria
    private static Usuario usuarioActual;

    // metodo para guardar al usuario cuando hace login
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    // metodo para preguntar quién está logueado en cualquier pantalla
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // metodo para limpiar la memoria al cerrar sesión
    public static void limpiarSesion() {
        usuarioActual = null;
    }
}