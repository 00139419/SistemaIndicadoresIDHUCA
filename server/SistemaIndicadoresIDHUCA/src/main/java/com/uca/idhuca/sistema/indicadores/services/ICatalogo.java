package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;

public interface ICatalogo {
	
	/**
	 * Método para obtener un catálogo específico en función del DTO de entrada.
	 * Solo se debe activar un campo booleano en el DTO para indicar qué catálogo consultar.
	 *
	 * @param request Objeto que contiene los campos booleanos que indican el catálogo a recuperar.
	 * @return Una respuesta genérica que contiene la lista de elementos del catálogo solicitado.
	 * @throws ValidationException Si se activan múltiples campos o si no se activa ninguno.
	 * @throws NotFoundException Si el campo solicitado no corresponde a ningún catálogo conocido.
	 */
	GenericEntityResponse<List<Catalogo>> get(CatalogoDto request) throws ValidationException, Exception;

	/**
	 * Agrega un nuevo elemento al catálogo indicado en el DTO.
	 *
	 * @param request Objeto CatalogoDto que contiene la información del nuevo elemento y el catálogo correspondiente.
	 * @return Una respuesta genérica indicando el resultado de la operación.
	 * @throws ValidationException Si los datos proporcionados no son válidos.
	 * @throws NotFoundException Si el catálogo especificado no es reconocido.
	 */
	SuperGenericResponse add(CatalogoDto request) throws ValidationException, NotFoundException;

	/**
	 * Actualiza un elemento existente en el catálogo especificado en el DTO.
	 *
	 * @param request Objeto CatalogoDto con los datos actualizados y la referencia al catálogo.
	 * @return Una respuesta genérica indicando el resultado de la operación.
	 * @throws ValidationException Si los datos son inválidos o están incompletos.
	 * @throws NotFoundException Si el elemento o el catálogo no se encuentran.
	 */
	SuperGenericResponse update(CatalogoDto request) throws ValidationException, NotFoundException;

	/**
	 * Elimina un elemento del catálogo indicado en el DTO.
	 *
	 * @param request Objeto CatalogoDto que identifica el catálogo y el elemento a eliminar.
	 * @return Una respuesta genérica indicando si la eliminación fue exitosa.
	 * @throws ValidationException Si los datos proporcionados son inválidos.
	 * @throws NotFoundException Si el elemento o el catálogo especificado no existen.
	 */
	SuperGenericResponse delete(CatalogoDto request) throws ValidationException, NotFoundException;

}
