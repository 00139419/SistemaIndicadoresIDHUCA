package com.uca.idhuca.sistema.indicadores.filtros.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class ExpresionCensuraFiltro {
	private List<Catalogo> mediosExpresion;
    private List<Catalogo> tiposRepresion;
    private Boolean represaliasLegales;
    private Boolean represaliasFisicas;
    private List<Catalogo> actoresCensores;
    private List<String> consecuencias;
}
