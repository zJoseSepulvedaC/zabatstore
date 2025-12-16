package com.zabatstore.zabatstore.controller;

import com.zabatstore.zabatstore.service.UserDetailsServiceImpl;
import com.zabatstore.zabatstore.security.JwtTokenUtil;
import com.zabatstore.zabatstore.security.JwtAuthenticationFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false) // <--- Desactiva seguridad/login
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Mocks "basura" necesarios para que Spring no falle al cargar la configuración ---
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean(name = "dataSource") private DataSource dataSource;
    @MockBean private JwtTokenUtil jwtTokenUtil;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testHome_RutaRaiz() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("titulo", "Zabat Store"));
    }

    @Test
    void testHome_RutaHome() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void testHome_RutaRecetas() throws Exception {
        mockMvc.perform(get("/recetas"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }
}