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

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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
			if (response != null) {
				log.info("[" + key + "] ------ Fin de servicio 'users/get/all' " + response.toJson());
			}
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<Usuario>> addUser(@RequestBody Usuario usuario) {
		GenericEntityResponse<Usuario> response = new GenericEntityResponse<>();
		try {
			Usuario savedUser = repoUsuario.save(usuario);
			response.setCodigo(OK);
			response.setMensaje("Usuario agregado correctamente");
			response.setEntity(savedUser);
		} catch (Exception e) {
			e.printStackTrace();
			response.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMensaje("Error al agregar usuario");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping(value = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<Void>> deleteUser(@PathVariable Long id, @RequestBody Usuario usuario) {
		GenericEntityResponse<Void> response = new GenericEntityResponse<>();
		try {
			if (repoUsuario.existsById(id)) {
				// Ensure the ID in the path matches the ID in the request body
				if (!id.equals(usuario.getId())) {
					response.setCodigo(HttpStatus.BAD_REQUEST.value());
					response.setMensaje("El ID en la URL no coincide con el ID en el cuerpo de la solicitud");
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}
				repoUsuario.deleteById(id);
				response.setCodigo(OK);
				response.setMensaje("Usuario eliminado correctamente");
			} else {
				response.setCodigo(HttpStatus.NOT_FOUND.value());
				response.setMensaje("Usuario no encontrado");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMensaje("Error al eliminar usuario");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<GenericEntityResponse<Usuario>> updateUser(@PathVariable Long id, @RequestBody Usuario usuario) {
		GenericEntityResponse<Usuario> response = new GenericEntityResponse<>();
		try {
			if (repoUsuario.existsById(id)) {
				if (!id.equals(usuario.getId())) {
					response.setCodigo(HttpStatus.BAD_REQUEST.value());
					response.setMensaje("El ID en la URL no coincide con el ID en el cuerpo de la solicitud");
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}
				Usuario updatedUser = repoUsuario.save(usuario);
				response.setCodigo(OK);
				response.setMensaje("Usuario actualizado correctamente");
				response.setEntity(updatedUser);
			} else {
				response.setCodigo(HttpStatus.NOT_FOUND.value());
				response.setMensaje("Usuario no encontrado");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMensaje("Error al actualizar usuario");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}