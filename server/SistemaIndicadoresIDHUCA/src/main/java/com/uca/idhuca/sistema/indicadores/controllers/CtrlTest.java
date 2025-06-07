package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.graphics.GraphicsGeneratorService;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest;
import com.uca.idhuca.sistema.indicadores.jdbc.ConexionJDBC;
import com.uca.idhuca.sistema.indicadores.security.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "test")
public class CtrlTest {
	
	@Autowired
	ConexionJDBC conexionJDBC;
	
	@Autowired
	private GraphicsGeneratorService graphicsService;
	
	@Autowired
    private JwtUtils jwtUtils;
	
	@PostMapping(value = "/testConnection", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> testConnection() {
		SuperGenericResponse response = null;
		String key = "SYSTEM";
		log.info("[" + key + "] ------ Inicio de servicio 'test/testConnection'");
		
		response = new SuperGenericResponse();
		response.setCodigo(OK);
		response.setMensaje("Success!");
		
		log.info("[" + key + "] ------ Fin de servicio 'test/testConnection'");
		return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/testDbConnection", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> testDbConnection() {
		SuperGenericResponse response = null;
		String key = "SYSTEM";
		log.info("[" + key + "] ------ Inicio de servicio 'test/testDbConnection'");
		
		response = conexionJDBC.testDbConecction();
		
		log.info("[" + key + "] ------ Fin de servicio 'test/testDbConnection'");
		return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/testJwtGenerate", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> testJwtGenerate() {
		SuperGenericResponse response = null;
		String key = "SYSTEM";
		log.info("[" + key + "] ------ Inicio de servicio 'test/testJwtGenerate'");
		
		response = new SuperGenericResponse();
		
		response.setCodigo(OK);
		response.setMensaje(jwtUtils.generateJwtToken("00139419@uca.edu.sv"));
		
		log.info("[" + key + "] ------ Fin de servicio 'test/testJwtGenerate'");
		return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/graphics", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericEntityResponse<String>> crearGrafico(@RequestBody GraphicsRequest request) {
		GenericEntityResponse<String> res = new GenericEntityResponse<>();
		
		res.setCodigo(OK);
		res.setMensaje("Grafico generado correctamente");
		res.setEntity(graphicsService.generate(request).getBase64());
		
        return new ResponseEntity<GenericEntityResponse<String>>(res, HttpStatus.OK);
    }
	
}
