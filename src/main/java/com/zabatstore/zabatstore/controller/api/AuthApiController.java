package com.zabatstore.zabatstore.controller.api;

import com.zabatstore.zabatstore.controller.dto.LoginRequest;
import com.zabatstore.zabatstore.controller.dto.LoginResponse;
import com.zabatstore.zabatstore.model.Rol;
import com.zabatstore.zabatstore.model.Usuario;
import com.zabatstore.zabatstore.repository.UsuarioRepository;
import com.zabatstore.zabatstore.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsuarioRepository usuarioRepository;

    public AuthApiController(AuthenticationManager authenticationManager,
                             JwtTokenUtil jwtTokenUtil,
                             UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(user);

        Usuario usuario = usuarioRepository.findByEmail(user.getUsername())
                .orElseThrow();

        List<String> roles = usuario.getRoles()
                .stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(
                token,
                usuario.getNombre(),
                usuario.getEmail(),
                roles
        );

        return ResponseEntity.ok(response);
    }
}
