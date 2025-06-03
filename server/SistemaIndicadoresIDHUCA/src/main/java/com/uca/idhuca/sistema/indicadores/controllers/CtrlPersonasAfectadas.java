package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.services.IPersonasAfectadas;
import com.uca.idhuca.sistema.indicadores.services.IRegistros;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "registros/personasAfectadas")
public class CtrlPersonasAfectadas {
	
	@Autowired
	private Utilidades utils;	

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	IRegistros registrosServices;
	
	
	@Autowired
	private IPersonasAfectadas personasaAfectadasServices;
	
	@PostMapping(value = "/getAllByDerecho",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>> getAllByDerecho(@RequestBody CatalogoDto request) throws ValidationException {
		GenericEntityResponse<List<PersonaAfectada>> response = null;
		String key = "ADMIN";
		
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/getAllByDerecho' " + mapper.writeValueAsString(request));
			
			response = personasaAfectadasServices.getAllByDerecho(request);
			return new ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>>(new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/getAllByDerecho'");
		}
	}
	
	@PostMapping(value = "/getAllByRegistroEvento",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>> getAllByRegistroEvento(@RequestBody RegistroEventoDTO request) throws ValidationException {
		GenericEntityResponse<List<PersonaAfectada>> response = null;
		String key = "ADMIN";
		
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/getAllByRegistroEvento' " + mapper.writeValueAsString(request));
			
			response = personasaAfectadasServices.getAllByRegistroEvento(request);
			return new ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>>(new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/getAllByRegistroEvento'");
		}
	}
	
	@PostMapping(value = "/getOneById",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<PersonaAfectada>> getOneById(@RequestBody PersonaAfectadaDTO request) throws ValidationException {
		GenericEntityResponse<PersonaAfectada> response = null;
		String key = "ADMIN";
		
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/getOneById' " + mapper.writeValueAsString(request));
			
			response = personasaAfectadasServices.getOneById(request);
			return new ResponseEntity<GenericEntityResponse<PersonaAfectada>>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<GenericEntityResponse<PersonaAfectada>>(new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<GenericEntityResponse<PersonaAfectada>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/getOneById'");
		}
	}
	
	@PostMapping(value = "/delete",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> delteByRegistroEvento(@RequestBody RegistroEventoDTO request) throws ValidationException {
		SuperGenericResponse response = null;
		String key = "ADMIN";
		
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/delete' " + mapper.writeValueAsString(request));
			
			response = personasaAfectadasServices.deletePerson(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/delete'");
		}
	}
	
	@PostMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> update(@RequestBody PersonaAfectadaDTO request) throws ValidationException {
		SuperGenericResponse response = null;
		String key = "ADMIN";
		
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/update' " + mapper.writeValueAsString(request));
			
			response = personasaAfectadasServices.update(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/update'");
		}
	}
	
}
