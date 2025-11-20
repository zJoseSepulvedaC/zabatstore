package com.zabatstore.zabatstore.config;

import com.zabatstore.zabatstore.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 🔴 Desactivamos CSRF (API con JWT + simplificar)
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                // Páginas públicas
                .requestMatchers("/", "/home", "/recetas", "/login", "/registro", "/buscar",
                                 "/h2-console/**", "/h2/**").permitAll()

                // API pública: login que genera JWT
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // APIs privadas: todo lo demás bajo /api/** requiere JWT
                .requestMatchers("/api/**").authenticated()

                // El resto de las rutas web lo dejamos accesible
                .anyRequest().permitAll()
            )

            // ✅ Login web por formulario (arregla tu 405 en /login)
            .formLogin(form -> form
                .loginPage("/login")          // tu template de login
                .loginProcessingUrl("/login") // POST del formulario
                .defaultSuccessUrl("/recetas", true)
                .failureUrl("/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/recetas")
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Sesión solo cuando haga falta (para la parte web)
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );

        // Cabeceras útiles
        http.headers(headers -> {
            headers.frameOptions(frame -> frame.sameOrigin());
            headers.addHeaderWriter(new StaticHeadersWriter(
                "Content-Security-Policy",
                String.join(" ",
                    "default-src 'self';",
                    "script-src 'self';",
                    "style-src 'self';",
                    "img-src 'self' data:;",
                    "object-src 'none';",
                    "base-uri 'self';",
                    "frame-ancestors 'self';",
                    "form-action 'self';"
                )
            ));
            headers.addHeaderWriter(new StaticHeadersWriter("X-Content-Type-Options", "nosniff"));
            headers.addHeaderWriter(new StaticHeadersWriter("Referrer-Policy", "no-referrer"));
            headers.addHeaderWriter(new StaticHeadersWriter(
                "Strict-Transport-Security",
                "max-age=31536000; includeSubDomains; preload"
            ));
        });

        http.userDetailsService(userDetailsService);

        // Filtro JWT para /api/**
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
