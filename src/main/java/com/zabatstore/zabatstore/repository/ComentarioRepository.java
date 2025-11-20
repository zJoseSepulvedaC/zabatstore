package com.zabatstore.zabatstore.repository;

import com.zabatstore.zabatstore.model.Comentario;
import com.zabatstore.zabatstore.model.Maquinaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByMaquinariaOrderByFechaCreacionDesc(Maquinaria maquinaria);
}
