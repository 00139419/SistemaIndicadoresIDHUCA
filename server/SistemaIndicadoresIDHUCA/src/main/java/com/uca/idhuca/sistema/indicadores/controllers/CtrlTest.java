package com.uca.idhuca.sistema.indicadores.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uca.idhuca.sistema.indicadores.dto.EmailRequest;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.services.IEmailService;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;

@RestController
@RequestMapping(ROOT_CONTEXT + "test")
public class CtrlTest {
	
	@Autowired
	IEmailService mailService;
	
	@GetMapping(value = "/testConnection", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> testConnection() {
		SuperGenericResponse response = new SuperGenericResponse();
		
		response.setCodigo(OK);
		response.setMensaje("Success!");
		
		return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/testSendEmail", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> testSendEmail() {
		SuperGenericResponse response;
		EmailRequest emailDTO = new EmailRequest();
		
		emailDTO.setDestinatario("00139419@uca.edu.sv");
		emailDTO.setAsunto("Prueba de correo");
		emailDTO.setMensaje("Hola este es un coreo de prueba");
		
		response = mailService.enviarCorreo(emailDTO);
		
		return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/testSendEmail2", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> testSendEmail2() {
		EmailRequest emailDTO = new EmailRequest();
		
		emailDTO.setDestinatario("00139419@uca.edu.sv");
		emailDTO.setAsunto("Prueba de correo");
		emailDTO.setMensaje("Hola este es un coreo de prueba");
		mailService.sendEmail(emailDTO);
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
}
