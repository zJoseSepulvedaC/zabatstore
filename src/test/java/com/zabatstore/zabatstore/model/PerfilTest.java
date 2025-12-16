package com.zabatstore.zabatstore.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PerfilTest {

    @Test
    void testPerfilGettersSettersAndNullSafety() {
        Perfil perfil = new Perfil();
        Usuario usuario = new Usuario();

        perfil.setId(10L);
        perfil.setUsuario(usuario);
        
        // Probamos valores normales
        perfil.setDireccion("Calle Falsa 123");
        perfil.setTelefono("987654321");
        perfil.setCultivos("Trigo");

        assertEquals(10L, perfil.getId());
        assertEquals(usuario, perfil.getUsuario());
        assertEquals("Calle Falsa 123", perfil.getDireccion());
        assertEquals("987654321", perfil.getTelefono());
        assertEquals("Trigo", perfil.getCultivos());
    }

    @Test
    void testPerfilNullSafety() {
        // Tu código tiene lógica: (val != null) ? val : ""
        // Vamos a probar que eso funciona
        Perfil perfil = new Perfil();

        perfil.setDireccion(null);
        perfil.setTelefono(null);
        perfil.setCultivos(null);

        // No deberían ser null, deberían ser vacíos
        assertEquals("", perfil.getDireccion());
        assertEquals("", perfil.getTelefono());
        assertEquals("", perfil.getCultivos());
    }
}