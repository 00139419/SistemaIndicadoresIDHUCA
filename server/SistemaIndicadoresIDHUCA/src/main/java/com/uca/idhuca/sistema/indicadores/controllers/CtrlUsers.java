package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoUsuario;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "users")
public class CtrlUsers {
	
	@Autowired
	IRepoUsuario repoUsuario;

	@GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<List<Usuario>>> getAll() {
		GenericEntityResponse<List<Usuario>> response = null;
		String key = "SYSTEM";
		log.info("[" + key + "] ------ Inicio de servicio 'users/get/all'");
		
		try {
			List<Usuario> list = repoUsuario.findAll();
			response = new GenericEntityResponse<>();
			response.setCodigo(OK);
			response.setMensaje("Datos obtenidos correctamente");
			response.setEntity(list);
				
			log.info("repoUsuario" + repoUsuario);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(response != null) {
				log.info("[" + key + "] ------ Fin de servicio 'users/get/all' " + response.toJson());
			}
		}
		return new ResponseEntity<GenericEntityResponse<List<Usuario>>>(response, HttpStatus.OK);
	}
}
