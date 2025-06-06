package com.uca.idhuca.sistema.indicadores.filtros.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class DerechosVulneradosFiltro {
	private List<Catalogo> derechosVulnerados;
}
