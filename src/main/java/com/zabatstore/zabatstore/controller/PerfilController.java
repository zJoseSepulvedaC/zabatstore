package com.zabatstore.zabatstore.controller;

import com.zabatstore.zabatstore.model.Perfil;
import com.zabatstore.zabatstore.model.Usuario;
import com.zabatstore.zabatstore.repository.PerfilRepository;
import com.zabatstore.zabatstore.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final PerfilRepository perfilRepo;
    private final UsuarioRepository usuarioRepo;

    public PerfilController(PerfilRepository perfilRepo, UsuarioRepository usuarioRepo) {
        this.perfilRepo = perfilRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // === [GET] Mostrar perfil ===
    @GetMapping
    public String ver(@AuthenticationPrincipal Object principal, Model model) {
        String email = extraerEmail(principal);
        if (email == null) return "redirect:/logout?error=auth";

        var userOpt = usuarioRepo.findByEmail(email);
        if (userOpt.isEmpty()) return "redirect:/logout?error=missing";

        Usuario user = userOpt.get();

        // Carga el perfil o crea uno vacío inicializado para evitar NPE
        Perfil perfil = perfilRepo.findByUsuario(user).orElseGet(() -> {
            Perfil nuevo = new Perfil();
            nuevo.setUsuario(user);
            nuevo.setDireccion("");
            nuevo.setTelefono("");
            nuevo.setCultivos("");
            return nuevo;
        });

        model.addAttribute("usuario", user);
        model.addAttribute("perfil", perfil);

        // Si se acaba de guardar, mostrar mensaje visual
        model.addAttribute("guardado", false);
        return "perfil";
    }

    // === [POST] Guardar perfil ===
    @PostMapping
    public String guardar(@ModelAttribute("perfil") Perfil perfil,
                          @AuthenticationPrincipal Object principal) {

        String email = extraerEmail(principal);
        if (email == null) return "redirect:/logout?error=auth";

        var userOpt = usuarioRepo.findByEmail(email);
        if (userOpt.isEmpty()) return "redirect:/logout?error=missing";

        Usuario user = userOpt.get();

        // Mantener ID existente si ya hay perfil guardado
        perfilRepo.findByUsuario(user).ifPresent(p -> perfil.setId(p.getId()));

        perfil.setUsuario(user);
        perfilRepo.save(perfil);

        return "redirect:/perfil?success";
    }

    // === Método auxiliar privado ===
    private String extraerEmail(Object principal) {
        if (principal instanceof Usuario u) {
            return u.getEmail();
        } else if (principal instanceof UserDetails ud) {
            return ud.getUsername();
        } else if (principal != null) {
            return principal.toString();
        }
        return null;
    }
}
