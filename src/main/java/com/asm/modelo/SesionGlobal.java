package com.asm.modelo;

// esta clase funciona como la memoria temporal de tu sistema
public class SesionGlobal {

    // guarda al usuario que acaba de iniciar sesion
    private static Usuario usuarioActual;

    // nueva variable que funciona como candado para saber si la caja ya se abrio
    private static boolean turnoAbierto = false;

    // metodo para guardar al usuario cuando entra al sistema
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    // metodo para saber quien esta usando el sistema en cualquier pantalla
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // metodo que nos dice si el cajero ya abrio su turno
    public static boolean isTurnoAbierto() {
        return turnoAbierto;
    }

    // metodo para cambiar el candado cuando el cajero guarde su fondo inicial
    public static void setTurnoAbierto(boolean estado) {
        turnoAbierto = estado;
    }

    // metodo para limpiar la memoria cuando cierran sesion
    public static void limpiarSesion() {
        usuarioActual = null;
        turnoAbierto = false; // es muy importante reiniciar el candado aqui
    }
}