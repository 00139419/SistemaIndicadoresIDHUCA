package com.uca.idhuca.sistema.indicadores.utils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ViolenciaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.AccesoJusticiaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CreateGraphicsDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.DetencionIntegridadDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ExpresionCensuraDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.FichaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ParametrosSistemaDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UbicacionDTO;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.DerechoVulnerado;
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
	
	public static List<String> validarUpdateNameUser(UserDto request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
		}

		if (request.getNombre() == null || request.getNombre().isEmpty()) {
			error = "La propiedad 'nombre' es obligatoria y no ser vacia.";
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
	
	public static List<String> validarCreateGraphics(CreateGraphicsDto request) {
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
		
	    Filtros categoriaEjeX = request.getCategoriaEjeX();
	    if (categoriaEjeX == null) {
	        list.add("Debes especificar un filtro para elegir el eje X.");
	        return list;
	    }

	    // 1. Contar cuántos sub-filtros vienen instanciados
	    Object subFiltroSeleccionado = null;
	    int subFiltrosInstanciados = 0;

	    for (Field field : Filtros.class.getDeclaredFields()) {
	        field.setAccessible(true);
	        try {
	            Object valor = field.get(categoriaEjeX);
	            if (valor != null) {
	                subFiltrosInstanciados++;
	                subFiltroSeleccionado = valor;
	            }
	        } catch (IllegalAccessException e) {
	            // nunca debería ocurrir
	        }
	    }

	    if (subFiltrosInstanciados == 0) {
	        list.add("Debes seleccionar una categoría (un sub-filtro) para el eje X.");
	        return list;
	    }
	    if (subFiltrosInstanciados > 1) {
	        list.add("Solo puedes seleccionar **una** categoría a la vez para el eje X.");
	        return list;
	    }

	    // 2. Dentro del sub-filtro elegido, validar que solo un campo contenga datos
	    int camposConValor = 0;
	    for (Field f : subFiltroSeleccionado.getClass().getDeclaredFields()) {
	        f.setAccessible(true);
	        try {
	            Object v = f.get(subFiltroSeleccionado);
	            if (v == null) continue;

	            boolean tieneValor;
	            if (v instanceof Collection<?>) {
	                tieneValor = !((Collection<?>) v).isEmpty();
	            } else {
	                // Boolean, Catalogo, RangoFechas, String, etc.
	                // Consideramos que Boolean tiene valor si no es null
	                tieneValor = true;
	            }

	            if (tieneValor) camposConValor++;

	        } catch (IllegalAccessException ignored) {}
	    }

	    if (camposConValor == 0) {
	        list.add("La categoría elegida no contiene ningún valor; debes indicar al menos uno.");
	    }
	    if (camposConValor > 1) {
	        list.add("Solo puedes indicar **un** campo dentro de la categoría seleccionada para el eje X.");
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
	
	public static List<String> validarDetelePersonByIDEvento(RegistroEventoDTO request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if (request.getId() == null || request.getId() < 0) {
			error = "La propiedad 'id' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		for(PersonaAfectadaDTO persona: request.getPersonasAfectadas()) {
			if(persona.getId() == null || persona.getId() < 1) {
				error = "La propiedad 'id' dentro de la lista de personas afectadas es obligatoria.";
				list.add(error);
				log.info("[" + key + "]" + " " + error);
				return list;
			}
		}

		return list;
	}
	
	public static List<String> validarUpdatePersonByIDEvento(PersonaAfectadaDTO persona) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (persona == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		

		if(persona.getId() == null || persona.getId() < 1) {
			error = "La propiedad 'id' dentro de la lista de personas afectada es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(persona.getNombre() == null || persona.getNombre().isEmpty()) {
			error = "La propiedad 'nombre' dentro del objeto persona dentro de la afectada es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(persona.getGenero() == null || persona.getGenero().getCodigo() == null) {
			error = "La propiedad 'codigo' dentro del objeto 'genero' dentro de la personas afectada es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(persona.getNacionalidad() == null || persona.getNacionalidad().getCodigo() == null) {
			error = "La propiedad 'codigo' dentro del objeto 'nacionalidad' dentro de la personas afectada es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(persona.getDepartamentoResidencia() == null || persona.getDepartamentoResidencia().getCodigo() == null) {
			error = "La propiedad 'codigo' dentro del objeto 'departamentoResidencia' dentro de la personas afectada es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(persona.getMunicipioResidencia() == null || persona.getMunicipioResidencia().getCodigo() == null) {
			error = "La propiedad 'codigo' dentro del objeto 'municipioResidencia' dentro de la personas afectada es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(persona.getTipoPersona() == null || persona.getTipoPersona().getCodigo() == null) {
			error = "La propiedad 'codigo' dentro del objeto 'tipoPersona' dentro de la personas afectada es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if(persona.getEstadoSalud() == null || persona.getEstadoSalud().getCodigo() == null) {
			error = "La propiedad 'codigo' dentro del objeto 'estadoSalud' dentro de la personas afectada es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		for(DerechoVulnerado derechos: persona.getDerechosVulnerados()) {
			if(derechos.getDerecho() == null || derechos.getDerecho().getCodigo() == null) {
				error = "La propiedad 'codigo' dentro del objeto 'derechosVulnerados' dentro de la personas afectada es obligatoria.";
				list.add(error);
				log.info("[" + key + "]" + " " + error);
				return list;
			}
		}
		
		ViolenciaDTO violencia = persona.getViolencia();

		if (violencia != null) {
		    if (violencia.getActorResponsable() == null || violencia.getActorResponsable().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'actorResponsable' dentro del objeto 'violencia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (violencia.getArtefactoUtilizado() == null || violencia.getArtefactoUtilizado().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'artefactoUtilizado' dentro del objeto 'violencia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (violencia.getContexto() == null || violencia.getContexto().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'contexto' dentro del objeto 'violencia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (violencia.getEstadoSaludActorResponsable() == null || violencia.getEstadoSaludActorResponsable().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'estadoSaludActorResponsable' dentro del objeto 'violencia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (violencia.getRespuestaEstado() == null || violencia.getRespuestaEstado().isEmpty()) {
		        error = "La propiedad 'respuestaEstado' dentro del objeto 'violencia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (violencia.getTipoViolencia() == null || violencia.getTipoViolencia().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'tipoViolencia' dentro del objeto 'violencia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }
		}

		AccesoJusticiaDTO justicia = persona.getAccesoJusticia();

		if (justicia != null) {
		    if (justicia.getTipoDenunciante() == null || justicia.getTipoDenunciante().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'tipoDenunciante' dentro del objeto 'accesoJusticia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (justicia.getDuracionProceso() == null || justicia.getDuracionProceso().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'duracionProceso' dentro del objeto 'accesoJusticia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (justicia.getResultadoProceso() == null || justicia.getResultadoProceso().isEmpty()) {
		        error = "La propiedad 'resultadoProceso' dentro del objeto 'accesoJusticia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (justicia.getInstancia() == null || justicia.getInstancia().isEmpty()) {
		        error = "La propiedad 'instancia' dentro del objeto 'accesoJusticia' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }
		}
		
		DetencionIntegridadDTO integridad = persona.getDetencionIntegridad();
		
		if(integridad != null) {
			if (integridad.getTipoDetencion() == null || integridad.getTipoDetencion().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'tipoDetencion' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (integridad.getAutoridadInvolucrada() == null || integridad.getAutoridadInvolucrada().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'autoridadInvolucrada' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (integridad.getMotivoDetencion() == null || integridad.getMotivoDetencion().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'motivoDetencion' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (integridad.getResultado() == null) {
		        error = "La propiedad 'resultado' dentro del objeto 'detencionIntegridad'dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }
		}
		
		ExpresionCensuraDTO expresion = persona.getExpresionCensura();
		
		if(expresion != null) {
			if (expresion.getMedioExpresion() == null || expresion.getMedioExpresion().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'medioExpresion' dentro del objeto 'expresionCensura' dentro de la personas afectada es obligatoria";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (expresion.getTipoRepresion() == null || expresion.getTipoRepresion().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'tipoRepresion' dentro del objeto 'expresionCensura' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (expresion.getActorCensor() == null || expresion.getActorCensor().getCodigo() == null) {
		        error = "La propiedad 'codigo' dentro del objeto 'actorCensor' dentro del objeto 'expresionCensura' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }

		    if (expresion.getConsecuencia() == null) {
		        error = "La propiedad 'consecuencia' dentro del objeto 'expresionCensura' dentro de la personas afectada es obligatoria.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
		    }
		}
		
		return list;
	}
	
	public static List<String> validarGetOnePersonaAfectadaById(PersonaAfectadaDTO request) {
		List<String> list = new ArrayList<>();

		String error = "";
		String key = "SYSTEM";

		if (request == null) {
			error = "El servicio necesita un json de request.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
		}
		
		if (request.getId() == null || request.getId() < 0) {
			error = "La propiedad 'id' es obligatoria.";
			list.add(error);
			log.info("[" + key + "]" + " " + error);
			return list;
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
		
		if(request.getSubDerechos() != null && request.getSubDerechos().equals(Boolean.TRUE)) {
			if(request.getParentId() == null || request.getParentId().isEmpty()) {
				error = "Si desea el catalogo de subDerechos debe de mandar en el parentId el codigo del derecho.";
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
	
	public static List<String> validarObtenerDetalleArchivos(FichaDerechoRequest request) {
	    List<String> list = new ArrayList<>();
	    String key = "SYSTEM";

	    if (request == null || request.getCodigoDerecho().isEmpty()|| !request.getCodigoDerecho().startsWith(Constantes.CATALOGO_DERECHO)) {
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
			
			List<DerechoVulnerado> derechosVulnerados = persona.getDerechosVulnerados();
			
			if(derechosVulnerados == null || derechosVulnerados.size() < 0) {
				error = "La lista de 'derechosVulnerados' dentro del objeto 'personasAfectadas' debe de tener al menos una derecho vulnerado.";
				list.add(error);
				log.info("[" + key + "]" + " " + error);
				return list;
			}
			
			for(DerechoVulnerado derechoVulnerado: derechosVulnerados) {
				if(derechoVulnerado == null || derechoVulnerado.getDerecho() == null || 
						derechoVulnerado.getDerecho().getCodigo() == null){
					error = "La lista de 'derechosVulnerados' dentro del objeto 'personasAfectadas' "
							+ "debe de tener al menos una derecho vulnerado con formato valido.";
					list.add(error);
					log.info("[" + key + "]" + " " + error);
					return list;
				}
			}

			Utilidades.formatDto(persona);
			
			if(!flagViolencia) {
				persona.setViolencia(null);
			}
			
			if(!flagDetencion) {
				persona.setDetencionIntegridad(null);
			}
			
			if(!flagExpresion) {
				persona.setExpresionCensura(null);
			}
			
			if(!flagJusticia) {
				persona.setAccesoJusticia(null);
			}
			
			if(!flagCensura) {
				persona.setExpresionCensura(null);
			}
			
			Utilidades.formatDto(persona.getViolencia());
			Utilidades.formatDto(persona.getAccesoJusticia());
			Utilidades.formatDto(persona.getDetencionIntegridad());
			Utilidades.formatDto(persona.getExpresionCensura());
			
			listaFormateada.add(persona);
		}
		
		request.setPersonasAfectadas(listaFormateada);
		
		return list;
	}
	
	public static List<String> validarUpdateRegistroEvento(RegistroEventoDTO request) {
	    List<String> list = new ArrayList<>();
	    String error = "";
	    String key = "SYSTEM";

	    if (request == null) {
	        error = "El servicio necesita un json de request.";
	        list.add(error);
	        log.info("[" + key + "] " + error);
	        return list;
	    }

	    if (request.getId() == null || request.getId() <= 0) {
	        error = "La propiedad 'id' es obligatoria para actualizar un registro existente.";
	        list.add(error);
	        log.info("[" + key + "] " + error);
	        return list;
	    }
	    
	    if (request.getFechaHecho() == null) {
	        error = "La propiedad 'fechaHecho' es obligatoria para actualizar un registro existente.";
	        list.add(error);
	        log.info("[" + key + "] " + error);
	        return list;
	    }

	    if (request.getFuente() == null || request.getFuente().getCodigo() == null || request.getFuente().getCodigo().isEmpty()) {
	        error = "La propiedad 'codigo' dentro del objeto 'fuente' es obligatoria para actualizar un registro existente.";
	        list.add(error);
	        log.info("[" + key + "] " + error);
	        return list;
	    }
	    
	    if (request.getEstadoActual() == null || request.getEstadoActual().getCodigo() == null || request.getEstadoActual().getCodigo().isEmpty()) {
	        error = "La propiedad 'codigo' dentro del objeto 'EstadoActual' es obligatoria para actualizar un registro existente.";
	        list.add(error);
	        log.info("[" + key + "] " + error);
	        return list;
	    }
	    
	    if (request.getDerechoAsociado() == null || request.getDerechoAsociado().getCodigo() == null || request.getDerechoAsociado().getCodigo().isEmpty()) {
	        error = "La propiedad 'codigo' dentro del objeto 'derechoAsociado' es obligatoria para actualizar un registro existente.";
	        list.add(error);
	        log.info("[" + key + "] " + error);
	        return list;
	    }

	    if (request.getObservaciones() == null || request.getObservaciones().isEmpty()) {
	        error = "La propiedad 'observaciones' es obligatoria para actualizar un registro existente.";
	        list.add(error);
	        log.info("[" + key + "] " + error);
	        return list;
	    }
	    
	    UbicacionDTO ubicaion = request.getUbicacion();
	    
	    if(ubicaion == null) {
	    	 error = "La propiedad 'ubicaion' es obligatoria para actualizar un registro existente.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
	    }

	    if(ubicaion.getDepartamento().getCodigo() == null || ubicaion.getDepartamento().getCodigo().isEmpty()) {
	    	 error = "La propiedad 'codigo' dentro del objeto 'departamento' es obligatoria para actualizar un registro existente.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
	    }
	    
	    if(ubicaion.getMunicipio().getCodigo() == null || ubicaion.getMunicipio().getCodigo().isEmpty()) {
	    	 error = "La propiedad 'codigo' dentro del objeto 'municipio' es obligatoria para actualizar un registro existente.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
	    }
	    
	    if(ubicaion.getLugarExacto().getCodigo() == null || ubicaion.getLugarExacto().getCodigo().isEmpty()) {
	    	 error = "La propiedad 'codigo' dentro del objeto 'lugarExacto' es obligatoria para actualizar un registro existente.";
		        list.add(error);
		        log.info("[" + key + "] " + error);
		        return list;
	    }
	    

	    return list;
	}

	
}
