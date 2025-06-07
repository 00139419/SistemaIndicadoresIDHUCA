package com.uca.idhuca.sistema.indicadores.graphics.dto;

import java.util.Map;

import lombok.Data;

@Data
public class SeriesDTO {
	private String name; 					// Nombre de la serie
	private Map<String, Number> data; 		// clave categoría -> valor
}
