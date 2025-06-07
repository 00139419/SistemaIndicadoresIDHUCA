package com.uca.idhuca.sistema.indicadores.graphics.dto;

import java.util.List;

import lombok.Data;

@Data
public class GraphicsRequest {

	public enum ChartType { PIE, BAR, STACKED_BAR, LINE, AREA, STACKED_AREA }
	private boolean defaultStyle; // usar estilos por default
	private ChartType chartType;
	private String title; // Título principal
	private String subtitle; // Descripción (opcional)
	private String categoryAxisLabel;
	private String valueAxisLabel;
	private boolean threeD = false; // true = 3D (solo BAR / STACKED_BAR)
	private boolean showLegend = true;
	private int width = 800;
	private int height = 600;
	private StyleDTO style; // colores, fuentes
	private List<SeriesDTO> series;
}
