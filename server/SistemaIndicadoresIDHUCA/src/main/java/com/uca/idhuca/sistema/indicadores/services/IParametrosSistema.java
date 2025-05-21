package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.ParametrosSistemaDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.ParametroSistema;

public interface IParametrosSistema {
	
	GenericEntityResponse<List<ParametroSistema>> getAll() throws ValidationException;
	SuperGenericResponse update(ParametrosSistemaDto request) throws ValidationException, NotFoundException;
	
}
