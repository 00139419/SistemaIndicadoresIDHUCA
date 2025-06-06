package com.uca.idhuca.sistema.indicadores.filtros.dto;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UsuarioSimple;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class Filtros {
	private Paginacion paginacion;
	private RangoFechas rangoFechas;
	private UsuarioSimple creador;
	private Catalogo derechoAsociado;
	private EventoFiltro eventoFiltro;
	private PersonaAfectadaFiltro afectadaFiltro;
	private DerechosVulneradosFiltro derechosVulneradosFiltro;
	private ViolenciaFiltro violenciaFiltro;
	private DetencionFiltro detencionFiltro;
	private ExpresionCensuraFiltro censuraFiltro;
	private AccesoJusticiaFiltro accesoJusticiaFiltro;
}
