package com.zabatstore.zabatstore.repository;

import com.zabatstore.zabatstore.model.Maquinaria;
import com.zabatstore.zabatstore.model.Usuario;
import com.zabatstore.zabatstore.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByMaquinaria(Maquinaria maquinaria);

    Optional<Valoracion> findByMaquinariaAndUsuario(Maquinaria maquinaria, Usuario usuario);
}
