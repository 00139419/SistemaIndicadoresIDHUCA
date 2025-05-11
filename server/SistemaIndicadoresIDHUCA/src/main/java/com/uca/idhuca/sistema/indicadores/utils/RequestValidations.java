package com.uca.idhuca.sistema.indicadores.utils;

import java.util.ArrayList;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.AddUserDto;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.Usuario;

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
	
	public static List<String> validarAddUser(AddUserDto request){
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
		
		if(request.getNombre() == null || request.getNombre().isEmpty()) {
			error = "La propiedad 'nombre' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		Catalogo rol = request.getRol();
		
		if(rol == null) {
			error = "La propiedad 'rol' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if(rol.getCodigo() == null || rol.getCodigo().isEmpty()) {
			error = "La propiedad 'codigo' dentro del objeto 'rol' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if(rol.getDescripcion() == null || rol.getDescripcion().isEmpty()) {
			error = "La propiedad 'descripcion' dentro del objeto 'rol' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		return list;
	}
	
	public static List<String> validarUpdateUser(AddUserDto request){
		List<String> list = new ArrayList<>();
		
		String error = "";
		String key = "SYSTEM";
		
		if(request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if(request.getId() == null || request.getId() < 1) {
			error = "La propiedad 'email' es obligatoria y debe de ser numero valido.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		return list;
	}
	
	public static List<String> validarIdGiven(Integer id){
		List<String> list = new ArrayList<>();
		
		String error = "";
		String key = "SYSTEM";
		
		if(id < 1) {
			error = "El ID no puede ser menor a 1.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		
		return list;
	}
}
