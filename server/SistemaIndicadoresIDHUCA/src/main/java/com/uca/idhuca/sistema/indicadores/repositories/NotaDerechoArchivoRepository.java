package com.uca.idhuca.sistema.indicadores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.idhuca.sistema.indicadores.models.NotaDerechoArchivo;

public interface NotaDerechoArchivoRepository extends JpaRepository<NotaDerechoArchivo, Long> {
	
	List<NotaDerechoArchivo> findByNota_Derecho_Codigo(String codigo);

}
