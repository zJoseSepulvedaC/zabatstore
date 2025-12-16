package com.zabatstore.zabatstore.controller;

import com.zabatstore.zabatstore.model.Perfil;
import com.zabatstore.zabatstore.model.Usuario;
import com.zabatstore.zabatstore.repository.PerfilRepository;
import com.zabatstore.zabatstore.repository.UsuarioRepository;
import com.zabatstore.zabatstore.service.UserDetailsServiceImpl;
import com.zabatstore.zabatstore.security.JwtTokenUtil;
import com.zabatstore.zabatstore.security.JwtAuthenticationFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PerfilController.class)
@AutoConfigureMockMvc(addFilters = false) // <--- Desactiva login forzoso
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PerfilRepository perfilRepo;

    @MockBean
    private UsuarioRepository usuarioRepo;

    // --- Mocks "basura" de seguridad ---
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean(name = "dataSource") private DataSource dataSource;
    @MockBean private JwtTokenUtil jwtTokenUtil;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // Limpiamos el contexto antes de cada test para evitar contaminación
        SecurityContextHolder.clearContext();
    }

    // Método auxiliar para simular un usuario logueado en el test
    private void simularUsuarioLogueado(String email) {
        // Creamos un UserDetails falso de Spring Security
        UserDetails userDetails = new User(email, "password", Collections.emptyList());
        
        // Creamos la "ficha" de autenticación
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
        // Lo metemos manualmente en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testVerPerfil_Exito() throws Exception {
        String email = "juan@zabat.com";
        simularUsuarioLogueado(email);

        // Simulamos que el usuario existe en BD
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail(email);
        usuarioMock.setNombre("Juan Perez");
        when(usuarioRepo.findByEmail(email)).thenReturn(Optional.of(usuarioMock));

        // Simulamos que NO tiene perfil todavía (para que cree uno nuevo vacío)
        when(perfilRepo.findByUsuario(usuarioMock)).thenReturn(Optional.empty());

        mockMvc.perform(get("/perfil"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("perfil"));
    }

    @Test
    void testVerPerfil_SinSesion_Redireccion() throws Exception {
        // NO llamamos a simularUsuarioLogueado(), así que el principal es null
        
        mockMvc.perform(get("/perfil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout?error=auth"));
    }

    @Test
    void testGuardarPerfil_Exito() throws Exception {
        String email = "juan@zabat.com";
        simularUsuarioLogueado(email);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setEmail(email);
        when(usuarioRepo.findByEmail(email)).thenReturn(Optional.of(usuarioMock));

        // Simulamos que ya existía un perfil previo
        Perfil perfilExistente = new Perfil();
        perfilExistente.setId(10L);
        when(perfilRepo.findByUsuario(usuarioMock)).thenReturn(Optional.of(perfilExistente));

        mockMvc.perform(post("/perfil")
                        .param("direccion", "Calle Falsa 123")
                        .param("telefono", "555-0000")
                        .flashAttr("perfil", new Perfil())) // Pasamos el objeto del form
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil?success"));

        // Verificamos que se llamó al repositorio para guardar
        verify(perfilRepo, times(1)).save(any(Perfil.class));
    }
}