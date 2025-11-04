package com.zabatstore.zabatstore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService; // tu impl: UserDetailsServiceImpl

    // Bean explícito de BCryptPasswordEncoder (necesario si algún componente lo inyecta por su clase concreta)
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean de la interfaz PasswordEncoder (reutiliza el BCrypt anterior)
    @Bean
    public PasswordEncoder passwordEncoder(BCryptPasswordEncoder bCrypt) {
        return bCrypt;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF habilitado (Thymeleaf lo usa). Ignora H2 si lo usas.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/h2/**"))

            // Permisos de URLs
            .authorizeHttpRequests(auth -> auth
                // Estáticos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // Páginas públicas
                .requestMatchers("/", "/home", "/recetas", "/login", "/registro", "/buscar", "/h2-console/**", "/h2/**").permitAll()
                // APIs públicas (si tienes)
                .requestMatchers("/api/public/**").permitAll()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Login con formulario (Thymeleaf)
            .formLogin(login -> login
                .loginPage("/login")                 // GET muestra el formulario
                .loginProcessingUrl("/login")        // POST procesa credenciales
                .defaultSuccessUrl("/perfil", true)  // redirección luego de logueo
                .failureUrl("/login?error")
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Manejo de sesión (stateful por formulario)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // Acceso denegado
            .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"))

            // H2 console (permite iframes)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // Usa tu UserDetailsService
        http.userDetailsService(userDetailsService);

        return http.build();
    }
}
