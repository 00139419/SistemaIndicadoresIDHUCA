package com.uca.idhuca.sistema.indicadores.repositories.custom;

import com.uca.idhuca.sistema.indicadores.dto.ResultadoCatalogo;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;

public interface CatalogoRepositoryCustom {
	ResultadoCatalogo obtenerCatalogo(String prefijo, Filtros filtro);
}
