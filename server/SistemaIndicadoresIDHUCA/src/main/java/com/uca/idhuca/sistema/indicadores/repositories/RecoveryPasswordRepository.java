package com.uca.idhuca.sistema.indicadores.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.idhuca.sistema.indicadores.models.RecoveryPassword;
import com.uca.idhuca.sistema.indicadores.models.Usuario;

public interface RecoveryPasswordRepository extends JpaRepository<RecoveryPassword, Long>{
	// Para obtener un registro por usuario (si solo hay uno)
    Optional<RecoveryPassword> findByUsuario(Usuario usuario);
}
