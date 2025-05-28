package com.uca.idhuca.sistema.indicadores.utils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ParametrosSistemaDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UbicacionDTO;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.DerechoVulnerado;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;

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
	
	public static List<String> validarGetAllRegistroPorDerecho(CatalogoDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		Catalogo derecho = request.getDerecho();
		
		if (derecho == null) {
			error = "La propiedad 'derecho' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if (derecho.getCodigo() == null) {
			error = "La propiedad 'codigo' dentro del objeto 'derecho' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}
	
	public static List<String> validarDeleteEventoByID(RegistroEventoDTO request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if (request.getId() == null || request.getId() < 0) {
			error = "La propiedad 'id' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}
	
	public static List<String> validarUpdateEventoByID(RegistroEvento request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if (request.getId() == null || request.getId() < 0) {
			error = "La propiedad 'id' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		return list;
	}
	
	public static List<String> validarUpdateParametrosSistema(ParametrosSistemaDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getClave() == null || request.getClave().isEmpty()) {
			error = "La propiedad 'clave' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}
		
		if (request.getValor() == null || request.getValor().isEmpty()) {
			error = "La propiedad 'valor' es obligatoria.";
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
	
	public static List<String> validarUnlockUser(UserDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getId() == null || request.getId() < 0) {
			error = "La propiedad 'id' es obligatoria y debe de ser numero valido.";
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
	
	public static List<String> validarObtenerDetalleArchivos(String derecho) {
	    List<String> list = new ArrayList<>();
	    String key = "SYSTEM";

	    if (derecho == null || derecho.isEmpty() || !derecho.startsWith(Constantes.CATALOGO_DERECHO)) {
	        String error = "El servicio necesita un JSON de request valido.";
	        list.add(error);
	        log.info("[{}] {}", key, error);
	        return list;
	    }
	    
	    return list;
	}
	
	public static List<String> validarUpdateNote(NotaDerechoRequest request) {
		List<String> list = new ArrayList<>();
		String error = "";
		String key = "SYSTEM";
		
		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		

		if(request.getId() == null) {
			error = "La propiedad 'id' es obligatoria y debe de ser valida.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(request.getTitulo() == null || request.getTitulo().isEmpty()) {
			error = "La propiedad 'titulo' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}

		if(request.getDescripcion() == null || request.getDescripcion().isEmpty()) {
			error = "La propiedad 'descripcion' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}

		return list;
	}
	
	public static List<String> validarDeleteNote(NotaDerechoRequest request) {
		List<String> list = new ArrayList<>();
		String error = "";
		String key = "SYSTEM";
		
		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		

		if(request.getId() == null) {
			error = "La propiedad 'id' es obligatoria y debe de ser valida.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}

		return list;
	}
	
	public static List<String> validarAddRegistroEvento(RegistroEventoDTO request) {
		List<String> list = new ArrayList<>();
		
		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(request.getFechaHecho() == null || request.getFechaHecho().isAfter(LocalDate.now())) {
			error = "La propiedad 'fechaHecho' es obligatoria y debe de ser menor a la fecha actual.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		Catalogo fuente = request.getFuente();
		
		if(fuente == null) {
			error = "El objeto 'fuente' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(fuente.getCodigo() == null || fuente.getCodigo().isEmpty()) {
			error = "El propiedad 'codigo' dentro del objeto 'fuente' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		Catalogo estadoActual = request.getEstadoActual();
		
		if(estadoActual == null) {
			error = "El objeto 'estadoActual' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(estadoActual.getCodigo() == null || estadoActual.getCodigo().isEmpty()) {
			error = "El propiedad 'codigo' dentro del objeto 'estadoActual' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		Catalogo derechoAsociado = request.getDerechoAsociado();
		
		if(derechoAsociado == null) {
			error = "El objeto 'derechoAsociado' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(derechoAsociado.getCodigo() == null || derechoAsociado.getCodigo().isEmpty()) {
			error = "El propiedad 'codigo' dentro del objeto 'derechoAsociado' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		UbicacionDTO ubicacion = request.getUbicacion();
		
		if(ubicacion == null) {
			error = "El objeto 'ubicacion' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		Catalogo departamentoHecho = request.getUbicacion().getDepartamento();
		
		if(departamentoHecho.getCodigo() == null || departamentoHecho.getCodigo().isEmpty()) {
			error = "El propiedad 'codigo' dentro del objeto 'departamento' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		Catalogo municipioHecho = request.getUbicacion().getDepartamento();
		
		if(municipioHecho.getCodigo() == null || municipioHecho.getCodigo().isEmpty()) {
			error = "El propiedad 'codigo' dentro del objeto 'municipio' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		String observaciones = request.getObservaciones();
		
		if(observaciones == null) {
			observaciones = "Sin observaciones";
			request.setObservaciones(observaciones);
		}
		
		
		List<PersonaAfectadaDTO> personasAfectadas = request.getPersonasAfectadas();
	    List<PersonaAfectadaDTO> listaFormateada = new ArrayList<>();
		
		if(personasAfectadas == null) {
			error = "La lista de 'personasAfectadas' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(personasAfectadas.isEmpty()) {
			error = "La lista de 'personasAfectadas' debe de tener al menos una persona.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		boolean flagViolencia = request.isFlagViolencia();
	    boolean flagDetencion = request.isFlagDetencion();
	    boolean flagExpresion = request.isFlagExpresion();
	    boolean flagJusticia = request.isFlagJusticia();
	    boolean flagCensura = request.isFlagCensura();
	    
		for(PersonaAfectadaDTO persona: personasAfectadas) {
			
			if(persona.getNombre() == null) {
				persona.setNombre("");
			}
			
			List<DerechoVulnerado> derechosVulnerados = persona.getDerechosVulnerados();
			
			if(derechosVulnerados == null || derechosVulnerados.isEmpty()) {
				error = "La lista de 'derechosVulnerados' dentro del objeto 'personasAfectadas' debe de tener al menos una derecho vulnerado.";
				list.add(error);
				log.info("[" + key + "]" + " " + error);
				return list;
			}
			
			for(DerechoVulnerado derechoVulnerado: derechosVulnerados) {
				if(derechoVulnerado != null && derechoVulnerado.getDerecho() != null || 
						derechoVulnerado.getDerecho().getCodigo() != null){
					error = "La lista de 'derechosVulnerados' dentro del objeto 'personasAfectadas' "
							+ "debe de tener al menos una derecho vulnerado con formato valido.";
					list.add(error);
					log.info("[" + key + "]" + " " + error);
					return list;
				}
			}
			
			if(persona.getEdad() == null) {
				persona.setEdad(-1);
			}
			
			Catalogo genero = persona.getGenero();
			
			if(genero == null || genero.getCodigo() == null || genero.getCodigo().isEmpty()) {
				Catalogo generoDesconocido = new Catalogo();
				
				generoDesconocido.setCodigo("GEN_0");
				generoDesconocido.setDescripcion("Desconocido");
				
				persona.setGenero(generoDesconocido);
			}
			
			Catalogo pais = persona.getNacionalidad();
			
			if(pais == null || pais.getCodigo() == null || pais.getCodigo().isEmpty()) {
				Catalogo paisDesconocido = new Catalogo();
				
				paisDesconocido.setCodigo("PAIS_0");
				paisDesconocido.setDescripcion("Otros paises");
				
				persona.setNacionalidad(paisDesconocido);
			}
			
			Catalogo departamentoResidencia = persona.getDepartamentoResidencia();
			
			if(departamentoResidencia == null || departamentoResidencia.getCodigo() == null || departamentoResidencia.getCodigo().isEmpty()) {
				Catalogo departamentoDesconocido = new Catalogo();
				
				departamentoDesconocido.setCodigo("DEP_0");
				departamentoDesconocido.setDescripcion("OTROS PAISES");
				
				persona.setDepartamentoResidencia(departamentoDesconocido);
			}
			
			Catalogo municipioResidencia = persona.getDepartamentoResidencia();
			
			if(municipioResidencia == null || municipioResidencia.getCodigo() == null || municipioResidencia.getCodigo().isEmpty()) {
				Catalogo municipioDesconocido = new Catalogo();
				
				municipioDesconocido.setCodigo("MUN_0_0");
				municipioDesconocido.setDescripcion("OTROS PAISES");
				
				persona.setMunicipioResidencia(municipioDesconocido);
			}
			
			Catalogo tipoPersona = persona.getTipoPersona();
			
			if(tipoPersona == null || tipoPersona.getCodigo() == null || tipoPersona.getCodigo().isEmpty()) {
				Catalogo tipoPersonaParticular = new Catalogo();
				
				tipoPersonaParticular.setCodigo("TIPOPER_1");
				tipoPersonaParticular.setDescripcion("Persona particular");
				
				persona.setTipoPersona(tipoPersonaParticular);
			}
			
			Catalogo estadoSalud = persona.getEstadoSalud();
			
			if(estadoSalud == null || estadoSalud.getCodigo() == null || estadoSalud.getCodigo().isEmpty()) {
				Catalogo estadoSaludDesconocido = new Catalogo();
				
				estadoSaludDesconocido.setCodigo("ESTSALUD_7");
				estadoSaludDesconocido.setDescripcion("Desconocido");
				
				persona.setEstadoSalud(estadoSaludDesconocido);
			}
			
			if(!flagViolencia) {
				persona.setViolencia(null);
			}
			
			if(!flagDetencion) {
				persona.setDetencion(null);
			}
			
			if(!flagExpresion) {
				persona.setExpresion(null);
			}
			
			if(!flagJusticia) {
				persona.setJusticia(null);
			}
			
			if(!flagCensura) {
				persona.setExpresion(null);
			}
			
			listaFormateada.add(persona);
		}
		
		request.setPersonasAfectadas(listaFormateada);
		
		return list;
	}
	
}
