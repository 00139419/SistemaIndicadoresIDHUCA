package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.dto.AuditoriaDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Auditoria;

public interface IAuditoria {
	
	GenericEntityResponse<List<Auditoria>> get() throws NotFoundException, ValidationException;
	<E> SuperGenericResponse add(AuditoriaDto<E> dto) throws ValidationException;
}
