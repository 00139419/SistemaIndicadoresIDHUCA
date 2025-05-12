package com.uca.idhuca.sistema.indicadores.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.GetCatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestValidations {
	
	public static List<String> validarLogin(LoginDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getEmail() == null || request.getEmail().isEmpty()) {
			error = "La propiedad 'email' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		key = request.getEmail();

		if (request.getPassword() == null || request.getPassword().isEmpty()) {
			error = "La propiedad 'password' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}

	public static List<String> validarAddUser(UserDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getEmail() == null || request.getEmail().isEmpty()) {
			error = "La propiedad 'email' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		key = request.getEmail();

		if (request.getPassword() == null || request.getPassword().isEmpty()) {
			error = "La propiedad 'password' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getNombre() == null || request.getNombre().isEmpty()) {
			error = "La propiedad 'nombre' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		Catalogo rol = request.getRol();

		if (rol == null) {
			error = "La propiedad 'rol' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (rol.getCodigo() == null || rol.getCodigo().isEmpty()) {
			error = "La propiedad 'codigo' dentro del objeto 'rol' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (rol.getDescripcion() == null || rol.getDescripcion().isEmpty()) {
			error = "La propiedad 'descripcion' dentro del objeto 'rol' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}

	public static List<String> validarUpdateUser(UserDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getId() == null || request.getId() < 1) {
			error = "La propiedad 'email' es obligatoria y debe de ser numero valido.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}

	public static List<String> validarIdGiven(Integer id) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (id < 1) {
			error = "El ID no puede ser menor a 1.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}
	
	public static List<String> validarEmailGiven(String email) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (email == null || email.isEmpty()) {
			error = "La propiedad 'email' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}
	
	public static List<String> validarRecoveryPassword(UserDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";
		
		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getEmail() == null || request.getEmail().isEmpty()) {
			error = "La propiedad 'email' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if (request.getSecurityAnswer() == null || request.getSecurityAnswer().isEmpty()) {
			error = "La propiedad 'securityAnswer' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
			error = "La propiedad 'newPassword' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}

	public static List<String> validarCatalogoRequest(GetCatalogoDto request) {
		List<String> list = new ArrayList<>();
		String error = "";
		String key = "SYSTEM";

		int trueCount = 0;

		for (Field field : request.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object value = field.get(request);
				if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
					trueCount++;
				}
			} catch (Exception e) {
				log.info("[" + key + "]" + " ERROR: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		if (trueCount == 0) {
			error = "Debe seleccionar al menos un catálogo.";
			list.add(error);
			log.info("[" + key + "] " + error);
		} else if (trueCount > 1) {
			error = "No se puede seleccionar más de un catálogo a la vez.";
			list.add(error);
			log.info("[" + key + "] " + error);
		}

		return list;
	}

}
