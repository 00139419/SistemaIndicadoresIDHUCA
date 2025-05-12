package com.uca.idhuca.sistema.indicadores.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uca.idhuca.sistema.indicadores.models.Auditoria;

public interface IRepoAuditoria extends JpaRepository<Auditoria, Long>{

}
