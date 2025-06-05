package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.dto.AuditoriaDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.models.Auditoria;

public interface IAuditoria {
	
	/**
	 * Obtiene la lista de registros de auditoría almacenados en el sistema.
	 * @param filtros 
	 *
	 * @return una respuesta genérica que contiene una lista de objetos Auditoria.
	 * @throws NotFoundException si no se encuentran registros de auditoría.
	 * @throws ValidationException si ocurre un error de validación al procesar la solicitud.
	 */
	GenericEntityResponse<List<Auditoria>> get(Filtros filtros) throws NotFoundException, ValidationException;

	/**
	 * Agrega un nuevo registro de auditoría al sistema.
	 *
	 * @param dto objeto AuditoriaDto que contiene la información a registrar. Puede ser de tipo genérico <E>.
	 * @param <E> tipo genérico que representa la entidad asociada al registro de auditoría.
	 * @return una respuesta genérica indicando el resultado de la operación.
	 * @throws ValidationException si los datos proporcionados en el DTO no son válidos.
	 */
	<E> SuperGenericResponse add(AuditoriaDto<E> dto) throws ValidationException;

}
