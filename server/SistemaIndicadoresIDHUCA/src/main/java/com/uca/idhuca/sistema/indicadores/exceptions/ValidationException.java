package com.uca.idhuca.sistema.indicadores.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5394034746880781318L;

	private int codigo;
	private String mensaje;
}
