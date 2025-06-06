package com.uca.idhuca.sistema.indicadores.filtros.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class AccesoJusticiaFiltro {
	private List<Catalogo> tiposProceso;
	private RangoFechas fechaDenunciaRango;
	private List<Catalogo> tiposDenunciante;
	private List<Catalogo> duracionesProceso;
	private Boolean accesoAbogado;
	private Boolean huboParcialidad;
	private List<String> resultadosProceso;
	private List<String> instancias;
}
