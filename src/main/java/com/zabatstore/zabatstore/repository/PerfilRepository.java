package com.zabatstore.zabatstore.repository;

import com.zabatstore.zabatstore.model.Perfil;
import com.zabatstore.zabatstore.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    /**
     * Busca un perfil asociado a un usuario específico.
     * @param usuario entidad Usuario (relación OneToOne)
     * @return Optional con el Perfil si existe, vacío si no.
     */
    Optional<Perfil> findByUsuario(Usuario usuario);
}
