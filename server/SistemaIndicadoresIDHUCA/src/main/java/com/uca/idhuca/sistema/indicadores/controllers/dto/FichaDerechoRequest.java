package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;

import lombok.Data;

@Data
public class FichaDerechoRequest {
	private String codigoDerecho;
	private Filtros filtros;
}
