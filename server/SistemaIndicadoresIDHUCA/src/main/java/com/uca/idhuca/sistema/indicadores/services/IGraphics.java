package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CreateGraphicsDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;

public interface IGraphics {
	GenericEntityResponse<List<RegistroEvento>> generate(CreateGraphicsDto request) throws ValidationException, NotFoundException, Exception;
}
