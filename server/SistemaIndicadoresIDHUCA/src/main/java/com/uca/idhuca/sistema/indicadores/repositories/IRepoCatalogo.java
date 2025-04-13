package com.uca.idhuca.sistema.indicadores.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

public interface IRepoCatalogo extends JpaRepository<Catalogo, String> {
	
}
