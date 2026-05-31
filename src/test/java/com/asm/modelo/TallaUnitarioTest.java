package com.asm.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TallaUnitarioTest {

    @Test
    public void testTallaAsignaNombreCorrectamente() {
        // creamos una talla comun para faldas o pantalones de uniforme
        Talla talla = new Talla("talla 14");

        // validamos que el texto se asigne bien y no permita nulos en memoria
        assertNotNull(talla.getNombreTalla(), "el nombre de la talla no debe ser nulo");
        assertEquals("talla 14", talla.getNombreTalla(), "el nombre de la talla debe coincidir exacto");
    }

    @Test
    public void testLongitudTallaPermitida() {
        // creamos una talla clasica para sueteres escolares
        Talla talla = new Talla("unitalla");

        // comprobamos logicamente que la longitud respete el limite de tu base de datos
        assertTrue(talla.getNombreTalla().length() <= 10, "el nombre de la talla debe medir 10 caracteres o menos");
    }

    @Test
    public void testIndependenciaDeTallasEscolares() {
        // creamos dos tallas distintas para playeras de deportes
        Talla tallaChica = new Talla("ch");
        Talla tallaGrande = new Talla("g");

        // verificamos que cada objeto mantenga su propia talla escolar intacta
        assertNotEquals(tallaChica.getNombreTalla(), tallaGrande.getNombreTalla(), "las tallas deben ser totalmente independientes");
    }
}