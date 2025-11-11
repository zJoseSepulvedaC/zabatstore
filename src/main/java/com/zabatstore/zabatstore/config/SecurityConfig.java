package com.zabatstore.zabatstore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    // ÚNICO PasswordEncoder del contexto
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
            // CSRF para formularios; ignora H2 console
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/h2/**"))

            // Autorización
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/home", "/recetas", "/login", "/registro", "/buscar",
                                 "/h2-console/**", "/h2/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/maquinarias", "/maquinarias/").permitAll()
                .requestMatchers(HttpMethod.GET, "/maquinarias/**").authenticated()
                .anyRequest().authenticated()
            )

            // Login / Logout
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
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

            // Sesión stateful
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // Acceso denegado
            .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"))

            .headers(headers -> {
            // Anti-Clickjacking: permite iframes solo desde el mismo origen (útil para H2)
            headers.frameOptions(frame -> frame.sameOrigin());

            // CSP sin inline, con directivas completas y fallback
            headers.addHeaderWriter(new StaticHeadersWriter(
                "Content-Security-Policy",
                String.join(" ",
                    "default-src 'self';",
                    "script-src 'self';",
                    "style-src 'self';",           // 'unsafe-inline'
                    "img-src 'self' data:;",
                    "object-src 'none';",
                    "base-uri 'self';",
                    "frame-ancestors 'self';",
                    "form-action 'self';"    //  Anti-Clickjacking
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
        return http.build();
    }
}
