package com.zabatstore.zabatstore.controller;

import com.zabatstore.zabatstore.model.Rol;
import com.zabatstore.zabatstore.model.Usuario;
import com.zabatstore.zabatstore.repository.RolRepository;
import com.zabatstore.zabatstore.repository.UsuarioRepository;
import com.zabatstore.zabatstore.service.UserDetailsServiceImpl;
import com.zabatstore.zabatstore.security.JwtTokenUtil;
import com.zabatstore.zabatstore.security.JwtAuthenticationFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Clave para desactivar filtros
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // <--- ESTO ES LO MÁS IMPORTANTE: Apaga la seguridad para el test
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Mocks requeridos por el constructor de AuthController ---
    @MockBean
    private UsuarioRepository usuarioRepo;

    @MockBean
    private RolRepository rolRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    // --- Mocks "basura" para satisfacer la configuración general de Spring Security ---
    // (Spring intenta cargar la configuración de seguridad aunque addFilters=false, y necesita estos beans)
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean(name = "dataSource") private DataSource dataSource;
    @MockBean private JwtTokenUtil jwtTokenUtil;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1. Test: Ver página de Login
    @Test
    void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // 2. Test: Ver formulario de Registro
    @Test
    void testMostrarRegistro() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("usuario"));
    }

    // 3. Test: Registro Exitoso (Usuario nuevo)
    @Test
    void testRegistrar_Exito() throws Exception {
        // Simulamos que NO existe el email
        when(usuarioRepo.findByEmail("nuevo@zabat.com")).thenReturn(Optional.empty());
        
        // Simulamos que el rol USER existe para no complicar el test
        Rol rolMock = new Rol(1L, "USER");
        when(rolRepo.findByNombre("USER")).thenReturn(Optional.of(rolMock));
        
        // Simulamos el encode de la contraseña
        when(passwordEncoder.encode(anyString())).thenReturn("passwordHasheada");

        mockMvc.perform(post("/registro")
                        .param("nombre", "Nuevo User")
                        .param("email", "nuevo@zabat.com")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection()) // Esperamos redirección
                .andExpect(redirectedUrl("/login?success=1")); // La URL exacta que pusiste en tu controller

        // Verificamos que se guardó el usuario
        verify(usuarioRepo, times(1)).save(any(Usuario.class));
    }

    // 4. Test: Registro Fallido (Email duplicado)
    @Test
    void testRegistrar_FalloEmailDuplicado() throws Exception {
        // Simulamos que YA existe un usuario con ese email
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("duplicado@zabat.com");
        when(usuarioRepo.findByEmail("duplicado@zabat.com")).thenReturn(Optional.of(usuarioExistente));

        mockMvc.perform(post("/registro")
                        .param("nombre", "Intruso")
                        .param("email", "duplicado@zabat.com")
                        .param("password", "1234"))
                .andExpect(status().isOk()) // NO redirige, se queda en la página (200 OK)
                .andExpect(view().name("registro")) // Vuelve a la vista "registro"
                .andExpect(model().attributeExists("error")) // El modelo tiene el mensaje de error
                .andExpect(model().attribute("error", "Ya existe un usuario con ese correo.")); // Mensaje exacto de tu código

        // Verificamos que NO se llamó a guardar
        verify(usuarioRepo, never()).save(any(Usuario.class));
    }
}