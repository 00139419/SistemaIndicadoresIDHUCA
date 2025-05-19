package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Auditoria;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "auditoria")
public class CtrlAuditoria {
	
	@Autowired
	private Utilidades utils;

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	IAuditoria auditoriaService;
	
	@PostMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<List<Auditoria>>> get() throws ValidationException {
		GenericEntityResponse<List<Auditoria>> response = null;
		String key = "ADMIN";
		
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/get' ");
			
			response = auditoriaService.get();
			return new ResponseEntity<GenericEntityResponse<List<Auditoria>>>(response, HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<GenericEntityResponse<List<Auditoria>>>(new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<GenericEntityResponse<List<Auditoria>>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/get'");
		}
	}
	
}
