package com.zabatstore.zabatstore.config;

import com.zabatstore.zabatstore.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    // ✅ Inyección automática del servicio (Spring se encarga)
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // === 1) Codificador de contraseñas ===
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // === 2) Autenticación basada en BD ===
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    
    // === 3) Filtro de seguridad y rutas ===
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Públicas
                .requestMatchers("/", "/home", "/login", "/registro",
                                 "/css/**", "/js/**", "/images/**",
                                 "/maquinarias").permitAll()
                // Privadas
                .requestMatchers(
                        "/maquinarias/detalle/**",
                        "/maquinarias/nueva",
                        "/perfil/**",
                        "/reservas/**"
                ).authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/perfil", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // puedes dejarlo así mientras desarrollas

        return http.build();
    }
}
