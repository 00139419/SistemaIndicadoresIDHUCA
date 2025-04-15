package com.uca.idhuca.sistema.indicadores.utils;

import java.util.ArrayList;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.dto.LoginDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestValidations {
	
	public static List<String> validarLogin(LoginDto request){
		List<String> list = new ArrayList<>();
		
		String error = "";
		String key = "SYSTEM";
		
		if(request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if(request.getEmail() == null || request.getEmail().isEmpty()) {
			error = "La propiedad 'email' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		key = request.getEmail();
		
		if(request.getPassword() == null || request.getPassword().isEmpty()) {
			error = "La propiedad 'password' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		return list;
	}
}
