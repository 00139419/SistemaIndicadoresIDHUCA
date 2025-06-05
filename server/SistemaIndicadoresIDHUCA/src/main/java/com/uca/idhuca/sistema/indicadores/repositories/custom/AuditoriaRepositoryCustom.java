package com.uca.idhuca.sistema.indicadores.repositories.custom;

import com.uca.idhuca.sistema.indicadores.models.*;
import com.uca.idhuca.sistema.indicadores.dto.ResultadoPaginado;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;

public interface AuditoriaRepositoryCustom {
	ResultadoPaginado<Auditoria> findByFiltros(Filtros filtros);
}