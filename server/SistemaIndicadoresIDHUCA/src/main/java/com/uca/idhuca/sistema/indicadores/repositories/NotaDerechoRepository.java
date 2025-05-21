package com.uca.idhuca.sistema.indicadores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uca.idhuca.sistema.indicadores.models.NotaDerecho;

public interface NotaDerechoRepository extends JpaRepository<NotaDerecho, Long> {

	List<NotaDerecho> findByDerechoCodigo(String derechoCodigo);
	
	@Query("SELECT n.nombreOriginal FROM NotaDerechoArchivo n WHERE n.archivoUrl = :archivoUrl")
	String findNombreOriginalByArchivoUrl(@Param("archivoUrl") String archivoUrl);


}
