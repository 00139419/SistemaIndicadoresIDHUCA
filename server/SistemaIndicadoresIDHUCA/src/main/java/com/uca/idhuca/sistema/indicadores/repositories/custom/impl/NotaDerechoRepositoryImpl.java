package com.uca.idhuca.sistema.indicadores.repositories.custom.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uca.idhuca.sistema.indicadores.controllers.dto.FichaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.ResultadoPaginado;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.filtros.dto.RangoFechas;
import com.uca.idhuca.sistema.indicadores.models.NotaDerecho;
import com.uca.idhuca.sistema.indicadores.repositories.custom.NotaDerechoRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class NotaDerechoRepositoryImpl implements NotaDerechoRepositoryCustom {
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public ResultadoPaginado<NotaDerecho> findNotas(FichaDerechoRequest request) {

	    CriteriaBuilder cb = em.getCriteriaBuilder();

	    /* ---------- COUNT ---------- */
	    CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);
	    Root<NotaDerecho> rootCnt = cqCount.from(NotaDerecho.class);

	    List<Predicate> predCount = new ArrayList<>();
	    predCount.add(cb.equal(rootCnt.get("derecho").get("codigo"), request.getCodigoDerecho()));

	    Filtros filtros = request.getFiltros();
	    RangoFechas rf  = filtros != null ? filtros.getRangoFechas() : null;
	    if (rf != null) {
	        LocalDate ini = rf.getFechaInicio();
	        LocalDate fin = rf.getFechaFin();

	        if (ini != null) {
	            predCount.add(cb.greaterThanOrEqualTo(
	                rootCnt.get("modificadoEn"),            
	                java.sql.Timestamp.valueOf(ini.atStartOfDay())
	            ));
	        }
	        if (fin != null) {
	            predCount.add(cb.lessThanOrEqualTo(
	                rootCnt.get("modificadoEn"),            
	                java.sql.Timestamp.valueOf(fin.atTime(23,59,59,999_999_999))
	            ));
	        }
	    }

	    cqCount.select(cb.count(rootCnt)).where(predCount.toArray(new Predicate[0]));
	    long total = em.createQuery(cqCount).getSingleResult();

	    CriteriaQuery<NotaDerecho> cq = cb.createQuery(NotaDerecho.class);
	    Root<NotaDerecho> root = cq.from(NotaDerecho.class);

	    List<Predicate> predMain = new ArrayList<>();
	    predMain.add(cb.equal(root.get("derecho").get("codigo"), request.getCodigoDerecho()));

	    if (rf != null) {
	        LocalDate ini = rf.getFechaInicio();
	        LocalDate fin = rf.getFechaFin();

	        if (ini != null) {
	            predMain.add(cb.greaterThanOrEqualTo(
	                root.get("modificadoEn"),                
	                java.sql.Timestamp.valueOf(ini.atStartOfDay())
	            ));
	        }
	        if (fin != null) {
	            predMain.add(cb.lessThanOrEqualTo(
	                root.get("modificadoEn"),              
	                java.sql.Timestamp.valueOf(fin.atTime(23,59,59,999_999_999))
	            ));
	        }
	    }

	    cq.select(root)
	      .where(predMain.toArray(new Predicate[0]))
	      .orderBy(cb.desc(root.get("modificadoEn")));       

	    TypedQuery<NotaDerecho> q = em.createQuery(cq);

	    int pag = 0, size = 10;
	    if (filtros != null && filtros.getPaginacion() != null) {
	        pag  = Math.max(0, filtros.getPaginacion().getPaginaActual());
	        size = filtros.getPaginacion().getRegistrosPorPagina() > 0
	               ? filtros.getPaginacion().getRegistrosPorPagina()
	               : 10;
	    }
	    q.setFirstResult(pag * size);
	    q.setMaxResults(size);

	    return new ResultadoPaginado<>(q.getResultList(), total);
	}


}
