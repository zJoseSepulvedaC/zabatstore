package com.zabatstore.zabatstore.controller;

import com.zabatstore.zabatstore.model.Maquinaria;
import com.zabatstore.zabatstore.repository.MaquinariaRepository;
import com.zabatstore.zabatstore.service.UserDetailsServiceImpl;
import com.zabatstore.zabatstore.security.JwtTokenUtil;
import com.zabatstore.zabatstore.security.JwtAuthenticationFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // <--- IMPORTANTE
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// addFilters = false DESACTIVA la seguridad para que el test entre directo
@WebMvcTest(MaquinariaController.class)
@AutoConfigureMockMvc(addFilters = false) 
class MaquinariaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaquinariaRepository repo;

    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean(name = "dataSource") private DataSource dataSource;
    @MockBean private JwtTokenUtil jwtTokenUtil;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testListar_SinFiltros() throws Exception {
        when(repo.findAll()).thenReturn(List.of(new Maquinaria()));

        mockMvc.perform(get("/maquinarias"))
                .andExpect(status().isOk())
                .andExpect(view().name("maquinaria-lista"))
                .andExpect(model().attributeExists("maquinarias"));
    }

    @Test
    void testListar_ConFiltroTipo() throws Exception {
        Maquinaria m1 = new Maquinaria(); m1.setTipo("Tractor");
        Maquinaria m2 = new Maquinaria(); m2.setTipo("Excavadora");

        when(repo.findAll()).thenReturn(Arrays.asList(m1, m2));

        mockMvc.perform(get("/maquinarias").param("tipo", "tractor"))
                .andExpect(status().isOk())
                .andExpect(view().name("maquinaria-lista"))
                .andExpect(model().attribute("maquinarias", hasSize(1)));
    }

    @Test
    void testDetalle_Existe() throws Exception {
        Maquinaria m = new Maquinaria();
        m.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(m));

        mockMvc.perform(get("/maquinarias/1/detalle"))
                .andExpect(status().isOk())
                .andExpect(view().name("maquinaria-detalle"))
                .andExpect(model().attributeExists("m"));
    }

    @Test
    void testDetalle_NoExiste() throws Exception {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/maquinarias/99/detalle"))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void testFormularioNueva() throws Exception {
        mockMvc.perform(get("/maquinarias/nueva"))
                .andExpect(status().isOk())
                .andExpect(view().name("publicar"))
                .andExpect(model().attributeExists("maquinaria"));
    }

    @Test
    void testGuardar() throws Exception {
        mockMvc.perform(post("/maquinarias")
                        // Ya no necesitamos csrf() porque desactivamos los filtros
                        .param("tipo", "Grúa")
                        .param("precioDiario", "100.50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/maquinarias"));

        verify(repo).save(any(Maquinaria.class));
    }
}