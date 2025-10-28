package com.zabatstore.zabatstore.controller;

import com.zabatstore.zabatstore.model.Usuario;
import com.zabatstore.zabatstore.repository.UsuarioRepository;
import com.zabatstore.zabatstore.repository.RolRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UsuarioRepository usuarioRepo, RolRepository rolRepo, BCryptPasswordEncoder encoder) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.encoder = encoder;
    }

    // ---------- LOGIN (único método) ----------
    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false) String success) {
        if (success != null) {
            model.addAttribute("success", "Registro exitoso. Ya puedes iniciar sesión.");
        }
        return "login";
    }

    // ---------- REGISTRO ----------
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {
        if (usuarioRepo.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "Ya existe un usuario con ese correo.");
            model.addAttribute("usuario", usuario);
            return "registro";
        }

        usuario.setPassword(encoder.encode(usuario.getPassword()));

        var rolUser = rolRepo.findByNombre("USER")
                .orElseGet(() -> rolRepo.save(new com.zabatstore.zabatstore.model.Rol(null, "USER")));

        usuario.getRoles().add(rolUser);
        usuarioRepo.save(usuario);

        return "redirect:/login?success=1";
    }
}
