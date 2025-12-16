package com.zabatstore.zabatstore.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MaquinariaMediaTest {

    @Test
    void testMaquinariaMedia() {
        // 1. Instanciar
        MaquinariaMedia media = new MaquinariaMedia();
        Maquinaria maquinaria = new Maquinaria();
        Usuario autor = new Usuario();

        // 2. Estado inicial
        assertNotNull(media.getFechaSubida());
        assertNull(media.getId());

        // 3. Setters
        media.setMaquinaria(maquinaria);
        media.setAutor(autor);
        media.setTipo("FOTO");
        media.setUrl("http://ejemplo.com/foto.jpg");
        
        LocalDateTime fecha = LocalDateTime.of(2023, 12, 31, 23, 59);
        media.setFechaSubida(fecha);

        // 4. Getters
        assertEquals(maquinaria, media.getMaquinaria());
        assertEquals(autor, media.getAutor());
        assertEquals("FOTO", media.getTipo());
        assertEquals("http://ejemplo.com/foto.jpg", media.getUrl());
        assertEquals(fecha, media.getFechaSubida());
    }
}