package com.uca.idhuca.sistema.indicadores.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BusquedaRegistroEventoResultado {
	private List<RegistroEvento> resultados;
	private long total;
}

