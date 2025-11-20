package com.zabatstore.zabatstore.controller.api;

import com.zabatstore.zabatstore.model.*;
import com.zabatstore.zabatstore.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maquinarias")
public class MaquinariaSocialApiController {

    private final MaquinariaRepository maquinariaRepository;
    private final ComentarioRepository comentarioRepository;
    private final ValoracionRepository valoracionRepository;
    private final MaquinariaMediaRepository mediaRepository;

    public MaquinariaSocialApiController(MaquinariaRepository maquinariaRepository,
                                         ComentarioRepository comentarioRepository,
                                         ValoracionRepository valoracionRepository,
                                         MaquinariaMediaRepository mediaRepository) {
        this.maquinariaRepository = maquinariaRepository;
        this.comentarioRepository = comentarioRepository;
        this.valoracionRepository = valoracionRepository;
        this.mediaRepository = mediaRepository;
    }

    private Maquinaria getMaquinariaOr404(Long id) {
        return maquinariaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquinaria no encontrada"));
    }

    // ------------------ COMENTARIOS ------------------

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<Comentario> comentar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Usuario usuario
    ) {
        Maquinaria m = getMaquinariaOr404(id);
        String contenido = body.get("contenido");

        if (contenido == null || contenido.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario no puede estar vacío");
        }

        Comentario comentario = new Comentario();
        comentario.setMaquinaria(m);
        comentario.setAutor(usuario);
        comentario.setContenido(contenido.trim());

        Comentario guardado = comentarioRepository.save(comentario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/{id}/comentarios")
    public List<Comentario> listarComentarios(@PathVariable Long id) {
        Maquinaria m = getMaquinariaOr404(id);
        return comentarioRepository.findByMaquinariaOrderByFechaCreacionDesc(m);
    }

    // ------------------ VALORACIONES ------------------

    @PostMapping("/{id}/valoraciones")
    public ResponseEntity<Valoracion> valorar(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal Usuario usuario
    ) {
        Maquinaria m = getMaquinariaOr404(id);
        Integer puntaje = body.get("puntaje");

        if (puntaje == null || puntaje < 1 || puntaje > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La valoración debe ser entre 1 y 5");
        }

        // Un usuario solo puede tener una valoración, la actualizamos si ya existe
        Valoracion valoracion = valoracionRepository
                .findByMaquinariaAndUsuario(m, usuario)
                .orElseGet(Valoracion::new);

        valoracion.setMaquinaria(m);
        valoracion.setUsuario(usuario);
        valoracion.setPuntaje(puntaje);

        Valoracion guardada = valoracionRepository.save(valoracion);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @GetMapping("/{id}/valoraciones/promedio")
    public Map<String, Object> promedioValoraciones(@PathVariable Long id) {
        Maquinaria m = getMaquinariaOr404(id);
        List<Valoracion> valoraciones = valoracionRepository.findByMaquinaria(m);

        double promedio = valoraciones.stream()
                .mapToInt(Valoracion::getPuntaje)
                .average()
                .orElse(0.0);

        return Map.of(
                "maquinariaId", id,
                "promedio", promedio,
                "cantidad", valoraciones.size()
        );
    }

    // ------------------ MEDIA (FOTOS / VIDEOS POR URL) ------------------

    @PostMapping("/{id}/media")
    public ResponseEntity<MaquinariaMedia> agregarMedia(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Usuario usuario
    ) {
        Maquinaria m = getMaquinariaOr404(id);

        String tipo = body.get("tipo");
        String url = body.get("url");

        if (tipo == null || url == null || url.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipo y url son obligatorios");
        }

        tipo = tipo.toUpperCase();
        if (!tipo.equals("FOTO") && !tipo.equals("VIDEO")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipo debe ser FOTO o VIDEO");
        }

        MaquinariaMedia media = new MaquinariaMedia();
        media.setMaquinaria(m);
        media.setAutor(usuario);
        media.setTipo(tipo);
        media.setUrl(url.trim());

        MaquinariaMedia guardada = mediaRepository.save(media);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @GetMapping("/{id}/media")
    public List<MaquinariaMedia> listarMedia(@PathVariable Long id) {
        Maquinaria m = getMaquinariaOr404(id);
        return mediaRepository.findByMaquinaria(m);
    }
}
