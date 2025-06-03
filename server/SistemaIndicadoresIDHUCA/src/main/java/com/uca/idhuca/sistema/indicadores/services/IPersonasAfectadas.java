package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;

public interface IPersonasAfectadas {
	GenericEntityResponse<List<PersonaAfectada>> getAllByDerecho(CatalogoDto request) throws ValidationException;
	GenericEntityResponse<List<PersonaAfectada>> getAllByRegistroEvento(RegistroEventoDTO request) throws ValidationException;
	SuperGenericResponse deletePerson(RegistroEventoDTO request) throws ValidationException;
	SuperGenericResponse update(PersonaAfectadaDTO request) throws ValidationException;
	GenericEntityResponse<PersonaAfectada> getOneById(PersonaAfectadaDTO request) throws ValidationException;
}
