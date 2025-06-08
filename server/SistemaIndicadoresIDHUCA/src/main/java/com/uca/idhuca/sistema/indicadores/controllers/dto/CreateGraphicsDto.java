package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class CreateGraphicsDto {
	Catalogo derecho;
	Filtros filtros;
	Filtros categoriaEjeX;
	GraphicsRequest graphicsSettings;
}
