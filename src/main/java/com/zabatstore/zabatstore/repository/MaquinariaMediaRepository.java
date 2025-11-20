package com.zabatstore.zabatstore.repository;

import com.zabatstore.zabatstore.model.Maquinaria;
import com.zabatstore.zabatstore.model.MaquinariaMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaquinariaMediaRepository extends JpaRepository<MaquinariaMedia, Long> {

    List<MaquinariaMedia> findByMaquinaria(Maquinaria maquinaria);
}
