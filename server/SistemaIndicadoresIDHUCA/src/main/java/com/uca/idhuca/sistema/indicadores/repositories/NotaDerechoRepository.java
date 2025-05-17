package com.uca.idhuca.sistema.indicadores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.idhuca.sistema.indicadores.models.NotaDerecho;

public interface NotaDerechoRepository extends JpaRepository<NotaDerecho, Long> {

	List<NotaDerecho> findByDerechoCodigo(String derechoCodigo);

}
