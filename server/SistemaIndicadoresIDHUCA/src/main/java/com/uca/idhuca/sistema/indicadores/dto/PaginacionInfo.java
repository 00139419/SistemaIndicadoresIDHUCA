package com.uca.idhuca.sistema.indicadores.dto;

import lombok.Data;

@Data
public class PaginacionInfo {
	private int paginaActual;
    private int totalPaginas;
    private long totalRegistros;
    private int registrosPorPagina;
}
