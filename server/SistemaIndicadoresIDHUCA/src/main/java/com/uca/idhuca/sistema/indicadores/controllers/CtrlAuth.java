package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.Jwt;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.services.IAuth;
import com.uca.idhuca.sistema.indicadores.services.IUser;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "auth")
public class CtrlAuth {

	@Autowired
	private Utilidades utils;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	IUser userService;
	
	@Autowired
	IAuth authService;
	
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<Jwt>> login(@RequestBody LoginDto request) {
		GenericEntityResponse<Jwt> response = null;
		String key =  request.getEmail() != null ?  request.getEmail() : "SYSTEM";
		
		try {
			log.info("[" + key + "] ------ Inicio de servicio 'test/testConnection' " + mapper.writeValueAsString(request));
			
			response = authService.login(request);
			return new ResponseEntity<GenericEntityResponse<Jwt>>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<GenericEntityResponse<Jwt>>(new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<GenericEntityResponse<Jwt>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio 'test/testConnection'");
		}
	}
	
	@PostMapping(value = "/recovery/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> recoverypassword(@RequestBody UserDto request) {
		String key =  "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = request.getEmail() != null ?  request.getEmail() : "SYSTEM";
			log.info("[" + key + "] ------ Inicio de servicio '/recovery/password' " + mapper.writeValueAsString(request));
			response = userService.recoveryPassword(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(-1, e.getMessage()), HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/recovery/password'");
		}
	}
	
	@GetMapping(value = "/get/securityQuestion", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> getSecurityQuestion(@RequestBody UserDto request) {
		String key =  "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = request.getEmail() != null ?  request.getEmail() : "SYSTEM";
			log.info("[" + key + "] ------ Inicio de servicio '/get/securityQuestion' " + mapper.writeValueAsString(request));
			response = userService.getSecurityQuestio(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(-1, e.getMessage()), HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/get/securityQuestion'");
		}
	}
	
}
