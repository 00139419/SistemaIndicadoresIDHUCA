package com.uca.idhuca.sistema.indicadores.repositories.custom.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uca.idhuca.sistema.indicadores.dto.ResultadoCatalogo;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.repositories.custom.CatalogoRepositoryCustom;

import java.util.List;

@Repository
public class CatalogoRepositoryImpl implements CatalogoRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public ResultadoCatalogo obtenerCatalogo(String prefijo, Filtros filtro) {

        String baseSql  = """
            FROM catalogo 
            WHERE codigo LIKE CONCAT(:prefijo, '%')
              AND codigo ~ ('^' || :prefijo || '[^_]+$')
            """;

        String dataSql  = "SELECT * "       + baseSql +
                          " ORDER BY CAST(regexp_replace(codigo, '^.*_', '') AS INTEGER)";
        String countSql = "SELECT COUNT(*) " + baseSql;

        int pagina = 0, tamanio = 10;
        if (filtro != null && filtro.getPaginacion() != null) {
            pagina  = Math.max(0, filtro.getPaginacion().getPaginaActual());
            tamanio = filtro.getPaginacion().getRegistrosPorPagina() > 0
                      ? filtro.getPaginacion().getRegistrosPorPagina()
                      : 10;
        }

        Query dataQ = entityManager.createNativeQuery(dataSql, Catalogo.class)
                                   .setParameter("prefijo", prefijo)
                                   .setFirstResult(pagina * tamanio)
                                   .setMaxResults(tamanio);

        Query cntQ  = entityManager.createNativeQuery(countSql)
                                   .setParameter("prefijo", prefijo);

        @SuppressWarnings("unchecked")
		List<Catalogo> datos    = dataQ.getResultList();
        long totalRegistros     = ((Number) cntQ.getSingleResult()).longValue();

        return new ResultadoCatalogo(datos, totalRegistros);
    }

}
