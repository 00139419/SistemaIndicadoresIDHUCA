package com.uca.idhuca.sistema.indicadores.repositories.custom.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uca.idhuca.sistema.indicadores.dto.ResultadoPaginado;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Paginacion;
import com.uca.idhuca.sistema.indicadores.filtros.dto.RangoFechas;
import com.uca.idhuca.sistema.indicadores.models.Auditoria;
import com.uca.idhuca.sistema.indicadores.repositories.custom.AuditoriaRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class AuditoriaRepositoryImpl implements AuditoriaRepositoryCustom {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public ResultadoPaginado<Auditoria> findByFiltros(Filtros filtros) {
	    CriteriaBuilder cb = em.getCriteriaBuilder();

	    // Consulta de conteo
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<Auditoria> countRoot = countQuery.from(Auditoria.class);
	    List<Predicate> predicates = construirPredicados(filtros, cb, countRoot);
	    countQuery.select(cb.count(countRoot)).where(predicates.toArray(new Predicate[0]));
	    Long totalRegistros = em.createQuery(countQuery).getSingleResult();

	    // Consulta paginada
	    CriteriaQuery<Auditoria> cq = cb.createQuery(Auditoria.class);
	    Root<Auditoria> root = cq.from(Auditoria.class);
	    cq.select(root).where(construirPredicados(filtros, cb, root).toArray(new Predicate[0]));
	    TypedQuery<Auditoria> query = em.createQuery(cq);

	    Paginacion pag = filtros.getPaginacion();
	    if (pag != null) {
	        int pagina = pag.getPaginaActual() > 0 ? pag.getPaginaActual() - 1 : 0;
	        int limite = pag.getRegistrosPorPagina() > 0 ? pag.getRegistrosPorPagina() : 10;
	        query.setFirstResult(pagina * limite);
	        query.setMaxResults(limite);
	    }

	    List<Auditoria> resultados = query.getResultList();
	    return new ResultadoPaginado<>(resultados, totalRegistros);
	}

	private List<Predicate> construirPredicados(Filtros filtros, CriteriaBuilder cb, Root<Auditoria> root) {
	    List<Predicate> predicates = new ArrayList<>();
	    RangoFechas rangoFechas = filtros.getRangoFechas();
	    if (rangoFechas != null) {
	        LocalDate inicio = rangoFechas.getFechaInicio();
	        LocalDate fin = rangoFechas.getFechaFin();

	        if (inicio != null && fin != null) {
	            predicates.add(cb.between(root.get("fecha"),
	                inicio.atStartOfDay(), fin.atTime(23, 59, 59, 999_999_999)));
	        } else if (inicio != null) {
	            predicates.add(cb.greaterThanOrEqualTo(root.get("fecha"), inicio.atStartOfDay()));
	        } else if (fin != null) {
	            predicates.add(cb.lessThanOrEqualTo(root.get("fecha"), fin.atTime(23, 59, 59, 999_999_999)));
	        }
	    }
	    return predicates;
	}

}
