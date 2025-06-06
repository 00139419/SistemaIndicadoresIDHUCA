package com.uca.idhuca.sistema.indicadores.repositories.custom.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.BusquedaRegistroEventoResultado;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.repositories.custom.RegistroEventoRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RegistroEventoRepositoryImpl implements RegistroEventoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    /* ---------- MÉTODO PRINCIPAL ---------- */
    @Override
    public BusquedaRegistroEventoResultado buscarEventos(CatalogoDto request) {
        Map<String, Object> params = new HashMap<>();

        // Base query con derecho y más filtros
        String baseJpql = armarBaseQueryPorDerecho(request, params);

        // Conteo total
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(re) " + baseJpql, Long.class);
        params.forEach(countQuery::setParameter);
        Long total = countQuery.getSingleResult();

        // Query paginada
        TypedQuery<RegistroEvento> query = em.createQuery("SELECT re " + baseJpql, RegistroEvento.class);
        params.forEach(query::setParameter);
        aplicarPaginacion(query, request);

        return new BusquedaRegistroEventoResultado(query.getResultList(), total);
    }


    /* ---------- HELPER 1: filtro por derecho ---------- */
    /** Devuelve la porción "FROM … WHERE …" con parámetro :der ya previsto */
    private String armarBaseQueryPorDerecho(CatalogoDto request, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder("FROM RegistroEvento re WHERE re.derechoAsociado = :der");
        params.put("der", request.getDerecho());

        // Aquí invocamos el filtro de fechas
        agregarFiltroPorFechas(request, sb, params);

        return sb.toString();
    }


    /* ---------- HELPER 2: paginación ---------- */
    private void aplicarPaginacion(TypedQuery<?> query, CatalogoDto request) {
        if (request.getFiltros() == null || request.getFiltros().getPaginacion() == null) {
            return; // no hay info de paginación → sin límite
        }

        var pag = request.getFiltros().getPaginacion();
        Integer size = pag.getRegistrosPorPagina();
        Integer page = pag.getPaginaActual();

        if (size != null && size > 0) {
            int pageIndex = (page != null && page >= 0) ? page : 0;
            query.setFirstResult(pageIndex * size);
            query.setMaxResults(size);
        }
    }
    
    /* ---------- HELPER 2: filtro por rango de fechas ---------- */
    private void agregarFiltroPorFechas(CatalogoDto request,
                                        StringBuilder where, Map<String, Object> params) {

        if (request.getFiltros() == null || request.getFiltros().getRangoFechas() == null) {
            return; // nada que agregar
        }

        var rango = request.getFiltros().getRangoFechas();
        
        java.sql.Timestamp fechaInicio = java.sql.Timestamp.valueOf(rango.getFechaInicio().atStartOfDay());
        java.sql.Timestamp fechaFin = java.sql.Timestamp.valueOf(rango.getFechaFin().atTime(23,59,59,999_000_000));

        log.info("[SYSTEM] fechaInicio: " + fechaInicio);
        log.info("[SYSTEM] fechaFin: " + fechaFin);
        
        if (rango.getFechaInicio() != null) {
            where.append(" AND re.fechaRegistro >= :fechaInicio");
            params.put("fechaInicio",fechaInicio);
        }

        if (rango.getFechaFin() != null) {
            where.append(" AND re.fechaRegistro <= :fechaFin");
            params.put("fechaFin", fechaFin);
        }
    }
}

