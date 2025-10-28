package com.zabatstore.zabatstore.controller;

import com.zabatstore.zabatstore.model.Maquinaria;
import com.zabatstore.zabatstore.repository.MaquinariaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/maquinarias")
public class MaquinariaController {

    private final MaquinariaRepository repo;

    public MaquinariaController(MaquinariaRepository repo) {
        this.repo = repo;
    }

    // ----------------------------------------------------
    // PÚBLICO: listado + búsqueda por tipo/ubicación/fecha/precio
    // ----------------------------------------------------
    @GetMapping
    public String listar(
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "ubicacion", required = false) String ubicacion,
            @RequestParam(value = "fechaDesde", required = false) String fechaDesde, // yyyy-MM-dd
            @RequestParam(value = "maxPrecio", required = false) BigDecimal maxPrecio,
            Model model
    ) {

        List<Maquinaria> data = repo.findAll();
        Stream<Maquinaria> stream = data.stream();

        // Filtro por tipo
        if (tipo != null && !tipo.isBlank()) {
            String t = tipo.toLowerCase();
            stream = stream.filter(m -> lower(m.getTipo()).contains(t));
        }

        // Filtro por ubicación
        if (ubicacion != null && !ubicacion.isBlank()) {
            String u = ubicacion.toLowerCase();
            stream = stream.filter(m -> lower(m.getUbicacion()).contains(u));
        }

        // Filtro por precio máximo
        if (maxPrecio != null) {
            stream = stream.filter(m ->
                    m.getPrecioDiario() != null && m.getPrecioDiario().compareTo(maxPrecio) <= 0
            );
        }

        // Filtro por fecha disponible (m.getDisponibleDesde() <= fechaDesde)
        if (fechaDesde != null && !fechaDesde.isBlank()) {
            try {
                LocalDate d = LocalDate.parse(fechaDesde);
                stream = stream.filter(m ->
                        m.getDisponibleDesde() == null || !m.getDisponibleDesde().isAfter(d)
                );
            } catch (Exception ignored) {
                // si viene mal formateada, no filtramos
            }
        }

        List<Maquinaria> lista = stream.collect(Collectors.toList());

        model.addAttribute("maquinarias", lista);
        // mantener filtros en el formulario
        model.addAttribute("fTipo", tipo == null ? "" : tipo);
        model.addAttribute("fUbicacion", ubicacion == null ? "" : ubicacion);
        model.addAttribute("fFechaDesde", fechaDesde == null ? "" : fechaDesde);
        model.addAttribute("fMaxPrecio", maxPrecio == null ? "" : maxPrecio.toPlainString());

        return "maquinaria-lista"; // templates/maquinaria-lista.html
    }

    private String lower(String s) { return (s == null) ? "" : s.toLowerCase(); }

    // ----------------------------------------------------
    // PRIVADO: Detalle
    // ----------------------------------------------------
    @GetMapping("/{id}/detalle")
    public String detalle(@PathVariable Long id, Model model) {
        Maquinaria m = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("m", m);
        return "maquinaria-detalle"; // templates/maquinaria-detalle.html
    }

    // ----------------------------------------------------
    // PRIVADO: Publicar (form + guardar)
    // ----------------------------------------------------
    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("maquinaria", new Maquinaria());
        return "publicar"; // templates/publicar.html
    }

    @PostMapping
    public String guardar(
            @ModelAttribute("maquinaria") Maquinaria maquinaria,
            @AuthenticationPrincipal UserDetails user
    ) {
        // Si tu UserDetails es tu entidad Usuario, aquí puedes setear propietario:
        // if (user instanceof Usuario u) maquinaria.setPropietario(u);

        repo.save(maquinaria);
        return "redirect:/maquinarias";
    }
}
