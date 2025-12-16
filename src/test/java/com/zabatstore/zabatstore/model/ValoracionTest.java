package com.zabatstore.zabatstore.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ValoracionTest {

    @Test
    void testValoracion() {
        // 1. Instanciar
        Valoracion valoracion = new Valoracion();
        Maquinaria maquinaria = new Maquinaria();
        Usuario usuario = new Usuario();

        // 2. Verificar fecha inicial
        assertNotNull(valoracion.getFechaCreacion());
        assertNull(valoracion.getId());

        // 3. Setters
        valoracion.setMaquinaria(maquinaria);
        valoracion.setUsuario(usuario);
        valoracion.setPuntaje(5);

        LocalDateTime fechaManual = LocalDateTime.now().minusDays(1);
        valoracion.setFechaCreacion(fechaManual);

        // 4. Getters
        assertEquals(maquinaria, valoracion.getMaquinaria());
        assertEquals(usuario, valoracion.getUsuario());
        assertEquals(5, valoracion.getPuntaje());
        assertEquals(fechaManual, valoracion.getFechaCreacion());
    }
}