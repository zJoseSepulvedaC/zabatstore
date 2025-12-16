package com.zabatstore.zabatstore.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RolTest {

    @Test
    void testRolConstructorsAndGetters() {
        // Test Constructor Vacío
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");

        assertEquals(1L, rol.getId());
        assertEquals("ADMIN", rol.getNombre());

        // Test Constructor con Argumentos
        Rol rol2 = new Rol(2L, "USER");
        assertEquals(2L, rol2.getId());
        assertEquals("USER", rol2.getNombre());
    }
}