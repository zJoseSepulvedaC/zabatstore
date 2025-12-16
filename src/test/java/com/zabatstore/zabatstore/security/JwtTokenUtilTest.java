package com.zabatstore.zabatstore.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private UserDetails mockUser;

    // HS256 requiere una clave de al menos 32 caracteres (256 bits)
    private final String SECRET = "12345678901234567890123456789012"; 
    private final long EXPIRATION_MS = 3600000; // 1 hora

    @BeforeEach
    void setUp() {
        // Inyectamos los valores manualmente como si fuera el application.properties
        jwtTokenUtil = new JwtTokenUtil(SECRET, EXPIRATION_MS);
        
        // Mockeamos el usuario
        mockUser = Mockito.mock(UserDetails.class);
        when(mockUser.getUsername()).thenReturn("usuarioTest");
    }

    @Test
    void testGenerateAndParseToken() {
        // 1. Generar token
        String token = jwtTokenUtil.generateToken(mockUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 2. Extraer usuario del token
        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals("usuarioTest", username);
    }

    @Test
    void testTokenValidity_Exito() {
        String token = jwtTokenUtil.generateToken(mockUser);
        assertTrue(jwtTokenUtil.isTokenValid(token, mockUser));
    }

    @Test
    void testTokenValidity_UsuarioIncorrecto() {
        String token = jwtTokenUtil.generateToken(mockUser);

        // Simulamos otro usuario
        UserDetails otroUsuario = Mockito.mock(UserDetails.class);
        when(otroUsuario.getUsername()).thenReturn("hacker");

        assertFalse(jwtTokenUtil.isTokenValid(token, otroUsuario));
    }

    @Test
    void testTokenValidity_TokenExpirado() {
        // Creamos una instancia con expiración negativa (-1 segundo)
        JwtTokenUtil expiredUtil = new JwtTokenUtil(SECRET, -1000);
        String tokenExpirado = expiredUtil.generateToken(mockUser);

        // Debería lanzar excepción internamente, ser capturada y devolver false
        assertFalse(jwtTokenUtil.isTokenValid(tokenExpirado, mockUser));
    }

    @Test
    void testTokenValidity_TokenCorrupto() {
        String token = jwtTokenUtil.generateToken(mockUser);
        
        // Modificamos el token para romper la firma
        String tokenCorrupto = token + "basura";

        assertFalse(jwtTokenUtil.isTokenValid(tokenCorrupto, mockUser));
    }
}