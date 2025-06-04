package com.uca.idhuca.sistema.indicadores.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;

public interface PersonasAfectadasRepository extends JpaRepository<PersonaAfectada, Long>{
	
	List<PersonaAfectada> findByEvento_DerechoAsociado(Catalogo derechoAsociado);
	
	@Query("SELECT p.evento FROM PersonaAfectada p WHERE p = :persona")
	RegistroEvento findEventoByPersona(@Param("persona") PersonaAfectada persona);
}
