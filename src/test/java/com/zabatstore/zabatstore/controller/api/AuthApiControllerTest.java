package com.zabatstore.zabatstore.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zabatstore.zabatstore.controller.dto.LoginRequest;
import com.zabatstore.zabatstore.model.Rol;
import com.zabatstore.zabatstore.model.Usuario;
import com.zabatstore.zabatstore.repository.UsuarioRepository;
import com.zabatstore.zabatstore.security.JwtAuthenticationFilter;
import com.zabatstore.zabatstore.security.JwtTokenUtil;
import com.zabatstore.zabatstore.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthApiController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactivamos filtros para probar solo la lógica del controller
class AuthApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UsuarioRepository usuarioRepository;

    // --- Mocks de Infraestructura (necesarios para que levante el contexto) ---
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean(name = "dataSource")
    private DataSource dataSource;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testLogin_Exito() throws Exception {
        // 1. Datos de prueba
        String email = "api@zabat.com";
        String password = "123";
        String tokenGenerado = "TOKEN_FALSO_12345";
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        // 2. Mockear el Usuario (Entidad)
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail(email);
        usuarioMock.setNombre("Api User");
        Rol rolUser = new Rol();
        rolUser.setNombre("ROLE_USER");
        usuarioMock.setRoles(Set.of(rolUser));

        // 3. Mockear el UserDetails (Spring Security)
        UserDetails userDetails = new User(email, password, Collections.emptyList());

        // 4. Mockear la Autenticación
        Authentication authMock = mock(Authentication.class);
        when(authMock.getPrincipal()).thenReturn(userDetails);

        // --- Comportamiento de los Mocks ---
        // Cuando el manager intente autenticar, devolvemos éxito
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);
        
        // Cuando busquemos el usuario en la BD, lo encontramos
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioMock));
        
        // Cuando pidamos generar token, devolvemos el string fijo
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn(tokenGenerado);

        // 5. Ejecutar Test (POST /api/auth/login)
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))) // Convertimos request a JSON
                .andExpect(status().isOk()) // Esperamos 200 OK
                .andExpect(jsonPath("$.token").value(tokenGenerado)) // Verificamos que el JSON trae el token
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.nombre").value("Api User"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}