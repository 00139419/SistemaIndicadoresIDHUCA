package com.uca.idhuca.sistema.indicadores.repositories.custom;


import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.BusquedaRegistroEventoResultado;

public interface RegistroEventoRepositoryCustom {
	BusquedaRegistroEventoResultado buscarEventos(CatalogoDto request);
}
