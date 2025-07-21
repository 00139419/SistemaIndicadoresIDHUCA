package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.services.IUser;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "users")
public class CtrlUsers {
	
	@Autowired
	private Utilidades utils;

	@Autowired
	IUser userServices;
	
	@Autowired
	ObjectMapper mapper;

	@PostMapping(value = "/get/current", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<Usuario>> getCurrent() {
		String key =  "SYSTEM";
		GenericEntityResponse<Usuario> response = new GenericEntityResponse<>();
		try {
			Usuario currentUser = utils.obtenerUsuarioAutenticado();
			key = currentUser.getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/get/current/'");
			response = new GenericEntityResponse<>(OK, "Data obtenida correctamente", currentUser);
			return new ResponseEntity<GenericEntityResponse<Usuario>>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<GenericEntityResponse<Usuario>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/get/current/'");
		}
	}
	
	@PostMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<List<Usuario>>> getAll() {
		GenericEntityResponse<List<Usuario>> response = new GenericEntityResponse<>();
		String key =  "SYSTEM";
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/get/all' ");
			response = userServices.getAll();
			return new ResponseEntity<GenericEntityResponse<List<Usuario>>>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<GenericEntityResponse<List<Usuario>>>(new GenericEntityResponse<>(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseEntity<GenericEntityResponse<List<Usuario>>>(new GenericEntityResponse<>(e.getCodigo(), e.getMensaje()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<GenericEntityResponse<List<Usuario>>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/get/all' ");
		}
	}
	
	@PostMapping(value = "/get/one/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<Usuario>> getOne(@PathVariable Integer id) {
		String key =  "SYSTEM";
		GenericEntityResponse<Usuario> response = new GenericEntityResponse<>();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/get/one/'" + " ID: " + id);
			response = userServices.getOne(id);
			return new ResponseEntity<GenericEntityResponse<Usuario>>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<GenericEntityResponse<Usuario>>(new GenericEntityResponse<>(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseEntity<GenericEntityResponse<Usuario>>(new GenericEntityResponse<>(e.getCodigo(), e.getMensaje()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<GenericEntityResponse<Usuario>>(new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/get/one/'");
		}
	}

	@PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> addUser(@RequestBody UserDto request) {
		String key =  "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/add' " + mapper.writeValueAsString(request));
			response = userServices.add(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/add'");
		}
	}

	@PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> deleteUser(@PathVariable Integer id) {
		String key =  "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/delete/'" + " ID: " + id);
			response = userServices.delete(id);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		}  catch (NotFoundException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/delete/'");
		}
	}

	@PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> updateUser(@RequestBody UserDto request) {
		String key =  "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/update' " + mapper.writeValueAsString(request));
			response = userServices.update(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		}  catch (NotFoundException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/update' ");
		}
	}
	
	@PostMapping(value = "/update/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> updateCurrentUser(@RequestBody UserDto request) {
		String key =  "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/update/current' " + mapper.writeValueAsString(request));
			response = userServices.update(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		}  catch (NotFoundException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/update/current' ");
		}
	}

	/**
	 * Actualiza el nombre del usuario autenticado.
	 * Este método permite cambiar el nombre del usuario actual en el sistema.
	 *
	 * @param request Objeto {@link UserDto} que contiene los datos del usuario y el nuevo nombre.
	 * @return {@link SuperGenericResponse} indicando el resultado de la operación.
	 */
	@PostMapping(value = "/update/name/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> updateNameCurrent(@RequestBody UserDto request) {
		String key = "SYSTEM";
		SuperGenericResponse response;
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[{}] ------ Inicio de servicio '/update/name/current' ", key);

			response = userServices.updateNameCurrent(request);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (ValidationException e) {
			return new ResponseEntity<>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()),
					HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("[{}] Error: ", key, e);
			return new ResponseEntity<>(new SuperGenericResponse(ERROR, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[{}] ------ Fin de servicio '/update/name/current' ", key);
		}
	}
	
	@PostMapping(value = "/change/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> changePassword(@RequestBody UserDto request) {
		String key =  "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/update' " + mapper.writeValueAsString(request));
			response = userServices.changePassword(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		}  catch (NotFoundException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/update' ");
		}
	}

	/**
	 * Cambia la contraseña de un usuario de forma simple, sin validaciones adicionales.
	 * Este método es útil para casos donde se requiere un cambio rápido de contraseña.
	 *
	 * @param request Objeto {@link UserDto} que contiene los datos del usuario y la nueva contraseña.
	 * @return {@link SuperGenericResponse} indicando el resultado de la operación.
	 */
	@PostMapping(value = "/change/password/simple", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> changePasswordSimple(@RequestBody UserDto request) {
		String key = "SYSTEM";
		SuperGenericResponse response;
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[{}] ------ Inicio de servicio '/change/password/simple' ", key);

			response = userServices.changePasswordSimple(request);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (ValidationException e) {
			return new ResponseEntity<>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()),
					HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("[{}] Error: ", key, e);
			return new ResponseEntity<>(new SuperGenericResponse(ERROR, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[{}] ------ Fin de servicio '/change/password/simple' ", key);
		}
	}

	@PostMapping(value = "/change/provsional/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> changeProvisionalPassword(@RequestBody UserDto request) {
		try {
			SuperGenericResponse response = userServices.changePassword(request);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (ValidationException | NotFoundException e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/unlock", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SuperGenericResponse> unlockUser(@RequestBody UserDto request) {
		String key =  "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/update' " + mapper.writeValueAsString(request));
			response = userServices.unlockUser(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		}  catch (NotFoundException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.BAD_REQUEST);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/update' ");
		}
	}
	
}