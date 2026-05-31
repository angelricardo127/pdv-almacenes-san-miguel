package com.asm.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioUnitarioTest {

    @Test
    public void testConstructorInicializaValoresCorrectamente() {
        // creamos el usuario solo en memoria ram
        Usuario usuario = new Usuario("admin1", "pass123", "juan", "perez", 1);

        // verificamos que los datos se guardaron bien en el objeto
        assertEquals("admin1", usuario.getUsername());
        assertEquals("pass123", usuario.getContrasena());
        assertEquals("juan", usuario.getNombre());

        // probamos tu regla de negocio: el estatus por defecto debe ser 1
        assertEquals(1, usuario.getEstatus(), "el estatus inicial debe ser 1");
    }

    @Test
    public void testSettersYGetters() {
        // creamos un usuario vacio usando el constructor de hibernate
        Usuario usuario = new Usuario();

        // le asignamos datos manualmente usando los setters
        usuario.setIdUsuario(5);
        usuario.setUsername("nuevo_admin");
        usuario.setEstatus(0); // lo damos de baja

        // verificamos que los getters devuelvan lo que acabamos de meter
        assertEquals(5, usuario.getIdUsuario());
        assertEquals("nuevo_admin", usuario.getUsername());
        assertEquals(0, usuario.getEstatus(), "el estatus debio cambiar a 0");
    }
}