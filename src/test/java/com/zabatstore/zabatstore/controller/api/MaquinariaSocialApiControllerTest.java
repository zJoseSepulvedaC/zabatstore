package com.zabatstore.zabatstore.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zabatstore.zabatstore.model.*;
import com.zabatstore.zabatstore.repository.ComentarioRepository;
import com.zabatstore.zabatstore.repository.MaquinariaMediaRepository;
import com.zabatstore.zabatstore.repository.MaquinariaRepository;
import com.zabatstore.zabatstore.repository.ValoracionRepository;
import com.zabatstore.zabatstore.security.JwtAuthenticationFilter;
import com.zabatstore.zabatstore.security.JwtTokenUtil;
import com.zabatstore.zabatstore.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaquinariaSocialApiController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactivamos filtros de seguridad
class MaquinariaSocialApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MaquinariaRepository maquinariaRepository;
    @MockBean
    private ComentarioRepository comentarioRepository;
    @MockBean
    private ValoracionRepository valoracionRepository;
    @MockBean
    private MaquinariaMediaRepository mediaRepository;

    // Mocks de infraestructura para que levante el contexto sin errores
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtTokenUtil jwtTokenUtil;
    @MockBean(name = "dataSource") private DataSource dataSource;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Usuario usuarioMock;
    private Maquinaria maquinariaMock;

    @BeforeEach
    void setUp() {
        // 1. Crear Usuario Mock
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombre("Test User");
        usuarioMock.setEmail("test@zabat.com");

        // 2. Crear Maquinaria Mock
        maquinariaMock = new Maquinaria();
        maquinariaMock.setId(10L);
      //  maquinariaMock.setNombre("Excavadora Test");

        // 3. Simular que el usuario está logueado en el Contexto de Seguridad
        // Esto es necesario para que @AuthenticationPrincipal funcione
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(usuarioMock, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // --- TEST COMENTARIOS ---

    @Test
    void testComentar_Exito() throws Exception {
        Map<String, String> body = Map.of("contenido", "Excelente máquina");

        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/maquinarias/10/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contenido").value("Excelente máquina"));
    }

    @Test
    void testComentar_Vacio_Error() throws Exception {
        Map<String, String> body = Map.of("contenido", "   "); // Vacío o espacios

        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));

        mockMvc.perform(post("/api/maquinarias/10/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest()); // Esperamos 400 Bad Request
    }

    @Test
    void testListarComentarios() throws Exception {
        Comentario c1 = new Comentario();
        c1.setContenido("Uno");
        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));
        when(comentarioRepository.findByMaquinariaOrderByFechaCreacionDesc(maquinariaMock))
                .thenReturn(List.of(c1));

        mockMvc.perform(get("/api/maquinarias/10/comentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contenido").value("Uno"));
    }

    // --- TEST VALORACIONES ---

    @Test
    void testValorar_Exito() throws Exception {
        Map<String, Integer> body = Map.of("puntaje", 5);

        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));
        when(valoracionRepository.findByMaquinariaAndUsuario(maquinariaMock, usuarioMock))
                .thenReturn(Optional.empty()); // No existe valoración previa
        when(valoracionRepository.save(any(Valoracion.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/maquinarias/10/valoraciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.puntaje").value(5));
    }

    @Test
    void testValorar_PuntajeInvalido() throws Exception {
        Map<String, Integer> body = Map.of("puntaje", 6); // Inválido (>5)

        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));

        mockMvc.perform(post("/api/maquinarias/10/valoraciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPromedioValoraciones() throws Exception {
        Valoracion v1 = new Valoracion(); v1.setPuntaje(5);
        Valoracion v2 = new Valoracion(); v2.setPuntaje(3);
        
        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));
        when(valoracionRepository.findByMaquinaria(maquinariaMock)).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/maquinarias/10/valoraciones/promedio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedio").value(4.0)) // (5+3)/2 = 4
                .andExpect(jsonPath("$.cantidad").value(2));
    }

    // --- TEST MEDIA ---

    @Test
    void testAgregarMedia_Exito() throws Exception {
        Map<String, String> body = Map.of("tipo", "FOTO", "url", "http://img.com/1.jpg");

        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));
        when(mediaRepository.save(any(MaquinariaMedia.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/maquinarias/10/media")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("FOTO"));
    }

    @Test
    void testAgregarMedia_TipoInvalido() throws Exception {
        Map<String, String> body = Map.of("tipo", "AUDIO", "url", "http://audio.com"); // Inválido

        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));

        mockMvc.perform(post("/api/maquinarias/10/media")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListarMedia() throws Exception {
        MaquinariaMedia media = new MaquinariaMedia();
        media.setUrl("http://video.com");
        
        when(maquinariaRepository.findById(10L)).thenReturn(Optional.of(maquinariaMock));
        when(mediaRepository.findByMaquinaria(maquinariaMock)).thenReturn(List.of(media));

        mockMvc.perform(get("/api/maquinarias/10/media"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].url").value("http://video.com"));
    }

    // --- TEST GENERAL ---
    
    @Test
    void testMaquinariaNoEncontrada() throws Exception {
        when(maquinariaRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/maquinarias/999/comentarios"))
                .andExpect(status().isNotFound());
    }
}