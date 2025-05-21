package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarGetAllRegistroPorDerecho;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDeleteEventoByID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.repositories.RegistroEventoRepository;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.IRegistros;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegistrosImpl implements IRegistros{
	
	@Autowired
	private Utilidades utils;

	@Autowired
	private RegistroEventoRepository registroEventoRepository;
	
	@Autowired
	private IAuditoria auditoriaService;
	
	@Override
	public GenericEntityResponse<List<RegistroEvento>> getAllByDerecho(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarGetAllRegistroPorDerecho(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();

		Catalogo derecho = utils.obtenerCatalogoPorCodigo(request.getDerecho().getCodigo(), key);
		log.info("[{}] Request válido", key);
		
		List<RegistroEvento> ls = registroEventoRepository.findByDerechoAsociadoCodigo(derecho);
		
		if(ls == null || ls.isEmpty()) {
			log.info("[{}] No se encontraron registros", key);
			throw new ValidationException(ERROR, "No se encontraron registros");
		}
		
		return new GenericEntityResponse<List<RegistroEvento>>(OK, "Datos obtenidos correctamente", ls);
	}

	@Override
	public SuperGenericResponse deleteEventoById(RegistroEventoDTO request) throws ValidationException {
		List<String> errorsList = validarDeleteEventoByID(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		
		RegistroEvento evento = utils.obtenerEventoPorID(request.getId(), key);
		log.info("[{}] Evento encontrado correctamente", key);
		
		
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), DELETE, evento));
		registroEventoRepository.delete(evento);
		
		log.info("[{}] Evento eliminado correctamente", key);
		return new SuperGenericResponse(OK, "Evento eliminado correctamente");
	}
	

}
