package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.ParametrosSistemaDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.ParametroSistema;

public interface IParametrosSistema {
	
	/**
	 * Obtiene todos los parámetros del sistema disponibles.
	 *
	 * @return una respuesta genérica que contiene una lista de objetos ParametroSistema.
	 * @throws ValidationException si ocurre un error de validación durante la obtención de los datos.
	 */
	GenericEntityResponse<List<ParametroSistema>> getAll() throws ValidationException;

	/**
	 * Actualiza un parámetro del sistema con los datos proporcionados en el DTO.
	 *
	 * @param request objeto ParametrosSistemaDto que contiene los datos a actualizar.
	 * @return una respuesta genérica indicando el resultado de la operación.
	 * @throws ValidationException si los datos proporcionados no cumplen con las reglas de validación.
	 * @throws NotFoundException si el parámetro a actualizar no se encuentra en el sistema.
	 */
	SuperGenericResponse update(ParametrosSistemaDto request) throws ValidationException, NotFoundException;
}
