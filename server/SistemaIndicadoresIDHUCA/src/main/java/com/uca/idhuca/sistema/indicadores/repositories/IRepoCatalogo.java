package com.uca.idhuca.sistema.indicadores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

public interface IRepoCatalogo extends JpaRepository<Catalogo, String> {
	Catalogo findByCodigo(String codigo);

	@Query(value = "SELECT * FROM catalogo WHERE codigo LIKE CONCAT(:prefijo, '%')", nativeQuery = true)
	List<Catalogo> obtenerCatalogo(@Param("prefijo") String prefijo);
}
