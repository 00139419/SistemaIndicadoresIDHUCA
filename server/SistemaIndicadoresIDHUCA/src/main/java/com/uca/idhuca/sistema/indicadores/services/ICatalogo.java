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
	GenericEntityResponse<List<Catalogo>> get(CatalogoDto request) throws ValidationException, NotFoundException;
	
	SuperGenericResponse add(CatalogoDto request) throws ValidationException, NotFoundException;
	
	SuperGenericResponse update(CatalogoDto request) throws ValidationException, NotFoundException;
	
	SuperGenericResponse delete(CatalogoDto request) throws ValidationException, NotFoundException;
}
