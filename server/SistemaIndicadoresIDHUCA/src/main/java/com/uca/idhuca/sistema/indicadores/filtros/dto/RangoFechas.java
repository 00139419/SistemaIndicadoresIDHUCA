package com.uca.idhuca.sistema.indicadores.filtros.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RangoFechas {
	private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
