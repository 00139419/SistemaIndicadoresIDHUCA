package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;

public interface IRegistros {
	
	GenericEntityResponse<List<RegistroEvento>> getAllByDerecho(CatalogoDto request) throws ValidationException, NotFoundException;
	
	GenericEntityResponse<RegistroEvento> getOne(RegistroEventoDTO request) throws ValidationException;

	SuperGenericResponse deleteEventoById(RegistroEventoDTO request) throws ValidationException;
	
	SuperGenericResponse updateEvento(RegistroEventoDTO request) throws ValidationException;
	
	SuperGenericResponse addEvento(RegistroEventoDTO request) throws ValidationException, Exception;
	
	
}
