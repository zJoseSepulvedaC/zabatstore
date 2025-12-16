package com.zabatstore.zabatstore.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void testUsuarioFull() {
        // 1. Preparar datos
        Rol rolAdmin = new Rol(1L, "admin");
        Set<Rol> roles = new HashSet<>();
        roles.add(rolAdmin);

        // 2. Test Constructor con argumentos
        Usuario user = new Usuario(1L, "test@zabat.com", "pass123", "Juan Perez", roles);

        // 3. Validaciones básicas
        assertEquals(1L, user.getId());
        assertEquals("Juan Perez", user.getNombre());
        assertEquals("test@zabat.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void testUserDetailsMethods() {
        Usuario user = new Usuario();
        user.setEmail("usuario@zabat.com");
        
        // Test de métodos de Spring Security
        assertEquals("usuario@zabat.com", user.getUsername()); // getUsername devuelve email
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        
        // Test toString
        user.setNombre("Test User");
        String toStringResult = user.toString();
        assertTrue(toStringResult.contains("Test User"));
        assertTrue(toStringResult.contains("usuario@zabat.com"));
    }

    @Test
    void testAuthoritiesLogic() {
        // Esta prueba verifica tu lógica: "ROLE_" + nombre.toUpperCase()
        Usuario user = new Usuario();
        Rol rol = new Rol(1L, "admin"); // nombre en minúscula
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        user.setRoles(roles);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        
        // Verificamos que transformó "admin" en "ROLE_ADMIN"
        String authorityName = authorities.iterator().next().getAuthority();
        assertEquals("ROLE_ADMIN", authorityName);
    }
    
    @Test
    void testSetRolesNullSafety() {
        Usuario user = new Usuario();
        user.setRoles(null); // Tu código dice: roles != null ? roles : new HashSet<>()
        
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }
    
    @Test
    void testPerfilRelation() {
        Usuario user = new Usuario();
        Perfil perfil = new Perfil();
        user.setPerfil(perfil);
        assertEquals(perfil, user.getPerfil());
    }
}