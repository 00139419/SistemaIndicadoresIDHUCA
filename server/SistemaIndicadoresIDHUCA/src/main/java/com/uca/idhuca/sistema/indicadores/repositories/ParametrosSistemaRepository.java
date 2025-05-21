package com.uca.idhuca.sistema.indicadores.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.idhuca.sistema.indicadores.models.ParametroSistema;

public interface ParametrosSistemaRepository extends JpaRepository<ParametroSistema, Long> {
	ParametroSistema findByClave(String clave);
}
