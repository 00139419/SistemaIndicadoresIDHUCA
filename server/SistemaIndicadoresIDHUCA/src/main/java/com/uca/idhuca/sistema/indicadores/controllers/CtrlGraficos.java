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
import com.uca.idhuca.sistema.indicadores.controllers.dto.CreateGraphicsDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.services.IGraphics;
import com.uca.idhuca.sistema.indicadores.services.IRegistros;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "graphics")
public class CtrlGraficos {
	
	@Autowired
	private Utilidades utils;	

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	IGraphics graphicsServices;
	
	@PostMapping(value = "/generate",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<List<RegistroEvento>>> getAll(@RequestBody CreateGraphicsDto request) throws ValidationException {
		GenericEntityResponse<List<RegistroEvento>> response = null;
		String key = "ADMIN";
		
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/getAll' ");
			
			response = graphicsServices.generate(request);
			return new ResponseEntity<GenericEntityResponse<List<RegistroEvento>>>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<GenericEntityResponse<List<RegistroEvento>>>(new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<GenericEntityResponse<List<RegistroEvento>>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/getAll'");
		}
	}
	
}
