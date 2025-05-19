package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarAddCatalogo;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarCatalogoRequest;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdateCatalogo;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDeleteCatalogo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoCatalogo;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.ICatalogo;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_ROL;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_SECURITY_QUESTION;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_DEPARTAMENTO;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_MUNICIPIO;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_DERECHO;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;

@Slf4j
@Service
public class CatologoImpl implements ICatalogo {

	@Autowired
	private Utilidades utils;

	@Autowired
	IRepoCatalogo repoCatalogo;
	
	@Autowired
	IAuditoria auditoriaService;

	@Override
	public GenericEntityResponse<List<Catalogo>> get(CatalogoDto request)
			throws ValidationException, NotFoundException {
		List<String> errorsList = validarCatalogoRequest(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		String nombreCampoActivo = null;
		for (Field field : request.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object value = field.get(request);
				if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
					if (nombreCampoActivo != null) {
						throw new IllegalArgumentException("Solo se puede activar un catálogo a la vez.");
					}
					nombreCampoActivo = field.getName();
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (nombreCampoActivo == null) {
			throw new IllegalArgumentException("Debe activar al menos un catálogo.");
		}

		List<Catalogo> list = null;

		switch (nombreCampoActivo) {
		case "roles":
			list = repoCatalogo.obtenerCatalogo(CATALOGO_ROL);
			break;
		case "derechos":
			list = repoCatalogo.obtenerCatalogo(CATALOGO_DERECHO);
			break;
		case "departamentos":
			list = repoCatalogo.obtenerCatalogo(CATALOGO_DEPARTAMENTO);
			break;
		case "municipios":
			list = repoCatalogo.obtenerCatalogo(CATALOGO_MUNICIPIO + request.getParentId().replace(CATALOGO_DEPARTAMENTO, "") + "_");
			break;
		case "securityQuestions":
			list = repoCatalogo.obtenerCatalogo(CATALOGO_SECURITY_QUESTION);
			break;
		default:
			throw new NotFoundException(ERROR, "Catálogo no reconocido: " + nombreCampoActivo);
		}

		return new GenericEntityResponse<List<Catalogo>>(OK, "Catalogo obtenido correctamente.", list);
	}

	@Override
	public SuperGenericResponse add(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarAddCatalogo(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		String nombreCampoActivo = null;
		for (Field field : request.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object value = field.get(request);
				if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
					if (nombreCampoActivo != null) {
						throw new IllegalArgumentException("Solo se puede activar un catálogo a la vez.");
					}
					nombreCampoActivo = field.getName();
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (nombreCampoActivo == null) {
			throw new IllegalArgumentException("Debe activar al menos un catálogo.");
		}

		Catalogo nuevoCatalogo = new Catalogo();
		nuevoCatalogo.setDescripcion(request.getNuevoCatalogo());
		int indice = 0;

		switch (nombreCampoActivo) {
		case "roles":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_ROL, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_ROL + indice);
			break;
		case "departamentos":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_DEPARTAMENTO, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_DEPARTAMENTO + indice);
			break;
		case "derechos":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_DERECHO, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_DERECHO + indice);
			break;
		case "municipios":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_MUNICIPIO, request.getParentId()) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_MUNICIPIO + request.getParentId() + "_" + indice);
			break;
		case "securityQuestions":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_SECURITY_QUESTION, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_SECURITY_QUESTION + indice);
			break;
		default:
			throw new NotFoundException(ERROR, "Catálogo no reconocido: " + nombreCampoActivo);
		}
		
		Catalogo catalogoSaved = repoCatalogo.save(nuevoCatalogo);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), CREAR, catalogoSaved));
		log.info("[{}] Catalogo creada correctamente.",key);
		
		return new SuperGenericResponse(OK, "Catalogo agregado correctamente.");
	}

	@Override
	public SuperGenericResponse update(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarUpdateCatalogo(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		Catalogo catalogo = null;
		try {
			catalogo = repoCatalogo
					 .findByCodigo(request.getCatalogo().getCodigo());
		} catch (NoSuchElementException e) {
			System.out.println("Catalogo no existe.");
			throw new NotFoundException(ERROR, "Catalogo no existe.");
		}
		 log.info("[{}] Catalogo válido", key);
		 
		 catalogo.setDescripcion(request.getCatalogo().getDescripcion());
		 repoCatalogo.save(catalogo);
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, catalogo));
		 log.info("[{}] Catalogo actualizado correctamente.",key);
		 
		 return new SuperGenericResponse(OK, "Catalogo actualizado correctamente.");
	}

	@Override
	public SuperGenericResponse delete(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarDeleteCatalogo(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		Catalogo catalogo = null;
		try {
			catalogo = repoCatalogo
					 .findByCodigo(request.getCatalogo().getCodigo());
		} catch (NoSuchElementException e) {
			System.out.println("Catalogo no existe.");
			throw new NotFoundException(ERROR, "Catalogo no existe.");
		}
		 log.info("[{}] Catalogo válido", key);
		 
		 repoCatalogo.delete(catalogo);
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), DELETE, catalogo));
		 log.info("[{}] Catalogo eliminado correctamente.",key);
		 
		 return new SuperGenericResponse(OK, "Catalogo eliminado correctamente.");
	}

}
