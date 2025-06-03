package com.uca.idhuca.sistema.indicadores.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;

public interface PersonasAfectadasRepository extends JpaRepository<PersonaAfectada, Long>{
	
	List<PersonaAfectada> findByEvento_DerechoAsociado(Catalogo derechoAsociado);
}
