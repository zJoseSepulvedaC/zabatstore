package com.zabatstore.zabatstore.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ComentarioTest {

    @Test
    void testComentario() {
        // 1. Instanciar
        Comentario comentario = new Comentario();
        Maquinaria maquinaria = new Maquinaria();
        Usuario autor = new Usuario();

        // 2. Verificar estado inicial (Fecha no debe ser null)
        assertNotNull(comentario.getFechaCreacion());
        assertNull(comentario.getId()); // ID es null antes de persistir

        // 3. Setters
        comentario.setMaquinaria(maquinaria);
        comentario.setAutor(autor);
        comentario.setContenido("Excelente maquinaria, muy recomendada");
        
        LocalDateTime nuevaFecha = LocalDateTime.of(2023, 1, 1, 12, 0);
        comentario.setFechaCreacion(nuevaFecha);

        // 4. Getters
        assertEquals(maquinaria, comentario.getMaquinaria());
        assertEquals(autor, comentario.getAutor());
        assertEquals("Excelente maquinaria, muy recomendada", comentario.getContenido());
        assertEquals(nuevaFecha, comentario.getFechaCreacion());
    }
}