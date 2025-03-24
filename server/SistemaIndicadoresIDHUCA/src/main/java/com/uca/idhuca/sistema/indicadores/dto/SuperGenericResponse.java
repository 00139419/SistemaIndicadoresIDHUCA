package com.uca.idhuca.sistema.indicadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperGenericResponse {
	private int codigo;
	private String mensaje;
}
