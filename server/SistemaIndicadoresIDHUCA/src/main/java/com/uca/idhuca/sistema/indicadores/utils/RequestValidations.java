package com.uca.idhuca.sistema.indicadores.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
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
	
	public static List<String> validarChangePassword(UserDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getPassword() == null || request.getPassword().isEmpty()) {
			error = "La propiedad 'password' es obligatoria y debe de ser numero valido.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if(request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
			error = "La propiedad 'newPassword' es obligatoria y debe de ser numero valido.";
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

	public static List<String> validarCatalogoRequest(CatalogoDto request) {
		List<String> list = new ArrayList<>();
		String error = "";
		String key = "SYSTEM";
		
		if(request.getMunicipios() != null && request.getMunicipios().equals(Boolean.TRUE)) {
			if(request.getParentId() == null || request.getParentId().isEmpty()) {
				error = "Si desea el catalogo de municipio debe de mandar en el parentId el codigo del departamento.";
				list.add(error);
				log.info("[" + key + "] " + error);
			}
		}

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
	
	public static List<String> validarAddCatalogo(CatalogoDto request) {
		List<String> list = new ArrayList<>();
		String error = "";
		String key = "SYSTEM";
		
		if(request.getNuevoCatalogo() == null || request.getNuevoCatalogo().isEmpty()) {
			error = "la propiedad 'nuevoCatalogo' dentro del request es obligatoria.";
			list.add(error);
			log.info("[" + key + "] " + error);
		}
		
		if(request.getMunicipios() != null && request.getMunicipios().equals(Boolean.TRUE)) {
			if(request.getParentId() == null || request.getParentId().isEmpty()) {
				error = "Si desea el catalogo de municipio debe de mandar en el parentId el codigo del departamento.";
				list.add(error);
				log.info("[" + key + "] " + error);
			}
		}

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
	
	public static List<String> validarUpdateCatalogo(CatalogoDto request) {
		List<String> list = new ArrayList<>();
		String error = "";
		String key = "SYSTEM";
		
		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		Catalogo catalogo = request.getCatalogo();
		
		if(catalogo.getCodigo() == null || catalogo.getCodigo().isEmpty()) {
			error = "La propiedad 'codigo' dentro del objeto catalogo es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if(catalogo.getDescripcion() == null || catalogo.getDescripcion().isEmpty()) {
			error = "La propiedad 'descripcion' dentro del objeto catalogo es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}
	
	public static List<String> validarDeleteCatalogo(CatalogoDto request) {
		List<String> list = new ArrayList<>();
		String error = "";
		String key = "SYSTEM";
		
		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		Catalogo catalogo = request.getCatalogo();
		
		if(catalogo.getCodigo() == null || catalogo.getCodigo().isEmpty()) {
			error = "La propiedad 'codigo' dentro del objeto catalogo es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}
	
	public static List<String> validarSaveFicha(NotaDerechoRequest request, MultipartFile[] archivos) {
	    List<String> list = new ArrayList<>();
	    String key = "SYSTEM";

	    if (request == null) {
	        String error = "El servicio necesita un JSON de request.";
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }
	    
	    Catalogo derecho = request.getDerecho();
	    
	    if(derecho == null) {
	    	String error = "El catalogo de 'derecho' es obligatorio.";
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }

	    if (derecho.getCodigo() == null || derecho.getCodigo().trim().isEmpty()) {
	        String error = "El campo 'codigo' dentro del objeto 'derecho' es obligatorio.";
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }

	    if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
	        String error = "El campo 'titulo' es obligatorio.";
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }

	    if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
	        String error = "El campo 'descripcion' es obligatorio.";
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }

	    if (request.getArchivos() == null || request.getArchivos().isEmpty()) {
	        String error = "Debe enviar al menos un archivo en el campo 'archivos'.";
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }

	    if (archivos == null || archivos.length == 0) {
	        String error = "No se recibieron archivos físicos adjuntos (MultipartFile[]).";
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }

	    if (request.getArchivos() != null && archivos != null &&
	        request.getArchivos().size() != archivos.length) {
	        String error = String.format("La cantidad de archivos descritos (%d) no coincide con los archivos adjuntos enviados (%d).",
	                request.getArchivos().size(), archivos.length);
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }

	    return list;
	}


}
