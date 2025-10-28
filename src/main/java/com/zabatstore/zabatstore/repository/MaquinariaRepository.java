package com.zabatstore.zabatstore.repository;

import com.zabatstore.zabatstore.model.Maquinaria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaquinariaRepository extends JpaRepository<Maquinaria, Long> {
  List<Maquinaria> findByTipoContainingIgnoreCaseAndUbicacionContainingIgnoreCase(String tipo, String ubicacion);
}
