package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.services.IFichaDerecho;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "fichaDerecho")
public class CtrlFichaDerecho {

	@Autowired
	private Utilidades utils;

	@Autowired
	private IFichaDerecho fichaDerechoService;

	@Autowired
	ObjectMapper mapper;

	@PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SuperGenericResponse> save(@RequestPart("nota") String notaJson,
			@RequestPart("archivos") MultipartFile[] archivos) {
		String key = "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			NotaDerechoRequest notaRequest = mapper.readValue(notaJson, NotaDerechoRequest.class);
			log.info("[" + key + "] ------ Inicio de servicio '/save' " + mapper.writeValueAsString(notaRequest));
			response = fichaDerechoService.save(notaRequest, archivos);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/save' ");
		}

	}

}
