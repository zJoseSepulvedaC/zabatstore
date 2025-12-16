package com.zabatstore.zabatstore.service;

import com.zabatstore.zabatstore.model.Usuario;
import com.zabatstore.zabatstore.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_UsuarioExiste_DebeRetornarUserDetails() {
        // 1. Preparar datos (Arrange)
        String email = "juan@zabat.com";
        Usuario mockUsuario = new Usuario();
        mockUsuario.setEmail(email);
        mockUsuario.setPassword("secret123");
        // Configuramos el mock del repositorio para que devuelva el usuario
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(mockUsuario));

        // 2. Ejecutar (Act)
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // 3. Verificar (Assert)
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        verify(usuarioRepository).findByEmail(email); // Verificamos que se llamó a la BD
    }

    @Test
    void loadUserByUsername_UsuarioNoExiste_DebeLanzarExcepcion() {
        // 1. Preparar datos (Arrange)
        String email = "fantasma@zabat.com";
        // Configuramos el mock para que devuelva vacío (usuario no encontrado)
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // 2. Ejecutar y Verificar (Act & Assert)
        // Esperamos que lance UsernameNotFoundException
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        verify(usuarioRepository).findByEmail(email);
    }
}