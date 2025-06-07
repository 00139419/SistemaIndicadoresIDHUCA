package com.uca.idhuca.sistema.indicadores.graphics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CampoSeleccionado {
	private final String subFiltro;
	private final String nombreCampo;
	private final String etiquetaVisible;
}
