package com.uca.idhuca.sistema.indicadores.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.Usuario;

public interface IRepoUsuario extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

}
