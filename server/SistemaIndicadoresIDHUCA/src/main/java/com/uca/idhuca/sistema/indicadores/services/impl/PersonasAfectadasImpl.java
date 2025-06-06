package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarGetAllRegistroPorDerecho;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDeleteEventoByID;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDetelePersonByIDEvento;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarGetOnePersonaAfectadaById;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdatePersonByIDEvento;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.auditoria.dto.AuditoriaRegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.auditoria.dto.PersonaAfectadaAuditoriaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.AccesoJusticiaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.DetencionIntegridadDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ExpresionCensuraDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ViolenciaDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.AccesoJusticia;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.DerechoVulnerado;
import com.uca.idhuca.sistema.indicadores.models.DetencionIntegridad;
import com.uca.idhuca.sistema.indicadores.models.ExpresionCensura;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.models.Violencia;
import com.uca.idhuca.sistema.indicadores.repositories.PersonasAfectadasRepository;
import com.uca.idhuca.sistema.indicadores.repositories.RegistroEventoRepository;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.IPersonasAfectadas;
import com.uca.idhuca.sistema.indicadores.useCase.EventosUseCase;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PersonasAfectadasImpl implements IPersonasAfectadas{

	@Autowired
	private PersonasAfectadasRepository personaRepository;
	
	@Autowired
	private RegistroEventoRepository eventoRepository;
	
	@Autowired
	private Utilidades utils;
	
	@Autowired
	private EventosUseCase eventoUseCase;
	
	@Autowired
	private IAuditoria auditoriaService;
	
	@Override
	public GenericEntityResponse<List<PersonaAfectada>> getAllByDerecho(CatalogoDto request)
			throws ValidationException {
		List<String> errorsList = validarGetAllRegistroPorDerecho(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		Usuario usuarioAutenticado = utils.obtenerUsuarioAutenticado();
		String key = usuarioAutenticado.getEmail();
		
		Catalogo derecho = utils.obtenerCatalogoPorCodigo(request.getDerecho().getCodigo(), key);
		log.info("[{}] Request válido", key);

		List<PersonaAfectada> ls = personaRepository.findByEvento_DerechoAsociado(derecho);
		log.info("[{}] Datos encontroados correctamente.", key);
		
		if(ls == null || ls.isEmpty()) {
			throw new ValidationException(ERROR, "No se encuentran registros actualemente en la base de datos");
		}
		
		return new GenericEntityResponse<List<PersonaAfectada>>(OK, "Datos encontroados correctamente", ls);
	}

	@Override
	public GenericEntityResponse<List<PersonaAfectada>> getAllByRegistroEvento(RegistroEventoDTO request)
			throws ValidationException {
		List<String> errorsList = validarDeleteEventoByID(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		Usuario usuarioAutenticado = utils.obtenerUsuarioAutenticado();
		String key = usuarioAutenticado.getEmail();
		
		RegistroEvento evento = eventoRepository.findById(request.getId()).orElseThrow(() -> {
			log.info("[{}] No existe el evento con ID: {}", key, request.getId());
			return new ValidationException(ERROR, " No existe el evento con ID: " + request.getId());
		});
		
		log.info("[{}] Request válido", key);

		List<PersonaAfectada> ls = evento.getPersonasAfectadas();
		log.info("[{}] Datos encontrados correctamente.", key);
		
		return new GenericEntityResponse<List<PersonaAfectada>>(OK, "Datos encontrados correctamente.", ls);
	}

	@Override
	public SuperGenericResponse deletePerson(RegistroEventoDTO request) throws ValidationException {
	    List<String> errorsList = validarDetelePersonByIDEvento(request);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    Usuario usuarioAutenticado = utils.obtenerUsuarioAutenticado();
	    String key = usuarioAutenticado.getEmail();

	    RegistroEvento evento = eventoRepository.findById(request.getId())
	        .orElseThrow(() -> {
	            log.info("[{}] No existe el evento con ID: {}", key, request.getId());
	            return new ValidationException(ERROR, "No existe el evento con ID: " + request.getId());
	        });
	    
	    log.info("[{}] Request válido", key);

	    Set<Long> idsAEliminar = request.getPersonasAfectadas().stream()
	        .map(PersonaAfectadaDTO::getId)
	        .collect(Collectors.toSet());

	    log.info("[{}] Eliminando personas afectadas con IDs: {}", key, idsAEliminar);

	    for (Long id : idsAEliminar) {
	        PersonaAfectada persona = personaRepository.findById(id)
	            .orElseThrow(() -> {
	                log.info("[{}] No existe la persona afectada con ID: {}", key, id);
	                return new ValidationException(ERROR, "No existe la persona afectada con ID: " + id);
	            });

	        personaRepository.delete(persona);
	    }
	    
	    eventoUseCase.actualizarFlagsDerechosPorEvento(evento.getId());
	    AuditoriaRegistroEventoDTO auditoria = utils.fromRegistroEvento(evento);
	    auditoriaService.add(utils.crearDto(usuarioAutenticado, DELETE, auditoria));

	    log.info("[{}] Personas afectadas eliminadas correctamente", key);
	    return new SuperGenericResponse(OK, "Personas afectadas eliminadas correctamente");
	}

	@Override
	public GenericEntityResponse<PersonaAfectada> getOneById(PersonaAfectadaDTO request) throws ValidationException {
		List<String> errorsList = validarGetOnePersonaAfectadaById(request);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    Usuario usuarioAutenticado = utils.obtenerUsuarioAutenticado();
	    String key = usuarioAutenticado.getEmail();
	    
	    log.info("[{}] Request válido", key);
	    
	    PersonaAfectada persona = personaRepository.findById(request.getId())
	    		.orElseThrow(() -> {
		            log.info("[{}] No existe la persona afectada con ID: {}", key, request.getId());
		            return new ValidationException(ERROR, "No existe la persona afectada con ID: " + request.getId());
		        });
	    
	    log.info("[{}] Datos obtenidos correctamente.", key);
		return new GenericEntityResponse<>(OK, "Datos obtenidos correctamente", persona);
	}

	@Override
	public SuperGenericResponse update(PersonaAfectadaDTO request) throws ValidationException {
	    List<String> errorsList = validarUpdatePersonByIDEvento(request);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    Usuario usuarioAutenticado = utils.obtenerUsuarioAutenticado();
	    String key = usuarioAutenticado.getEmail();

	    PersonaAfectada entity = personaRepository.findById(request.getId())
	        .orElseThrow(() -> {
	            log.info("[{}] Persona afectada no encontrada con ID: {}", key, request.getId());
	            return new ValidationException(ERROR, "Persona afectada no encontrada con ID: " + request.getId());
	        });

	    log.info("[{}] Request válido", key);
	    
	    eventoUseCase.actualizarDatosPersonalesDesdeDTO(entity, request, false);
	    
	    List<DerechoVulnerado> derechosActuales = entity.getDerechosVulnerados();
	    if (derechosActuales != null) {
	        derechosActuales.clear();
	    } else {
	        derechosActuales = new ArrayList<>();
	        entity.setDerechosVulnerados(derechosActuales);
	    }

	    for (DerechoVulnerado dto : request.getDerechosVulnerados()) {
	        DerechoVulnerado nuevo = new DerechoVulnerado();
	        nuevo.setDerecho(dto.getDerecho());
	        nuevo.setPersonaAfectada(entity);
	        derechosActuales.add(nuevo);
	    }
	    
	    log.info("[{}] Lista de derechos vulnerados actualizada", key);
	    
	    ViolenciaDTO violenciaDTO = request.getViolencia();
	    Violencia violenciaEntidad = entity.getViolencia();

	    if (violenciaDTO != null) {
	        if (violenciaEntidad == null) {
	        	
	            violenciaEntidad = new Violencia();
	            violenciaEntidad.setPersona(entity); 
	        }
	        eventoUseCase.actualizarViolenciaDesdeDTO(violenciaEntidad, violenciaDTO);
	        entity.setViolencia(violenciaEntidad);
	    } else {
	        entity.setViolencia(null);
	    }
	    
	    log.info("[{}] Violencia actualizada", key);
	    
	    DetencionIntegridadDTO detencionDTO = request.getDetencionIntegridad();
	    DetencionIntegridad detencionEntidad = entity.getDetencionIntegridad();

	    if (detencionDTO != null) {
	        if (detencionEntidad == null) {
	        	
	        	detencionEntidad = new DetencionIntegridad();
	        	detencionEntidad.setPersona(entity); 
	        }
	        eventoUseCase.actualizarIntegridadDesdeDTO(detencionEntidad, detencionDTO);
	        entity.setDetencionIntegridad(detencionEntidad);
	    } else {
	        entity.setDetencionIntegridad(null);
	    }
	    
	    log.info("[{}] DetencionIntegridad actualizada", key);
	    
	    ExpresionCensuraDTO expresionDTO = request.getExpresionCensura();
	    ExpresionCensura expresionEntidad = entity.getExpresionCensura();

	    if (expresionDTO != null) {
	        if (expresionEntidad == null) {
	        	
	        	expresionEntidad = new ExpresionCensura();
	        	expresionEntidad.setPersona(entity); 
	        }
	        eventoUseCase.actualizarExpresionDesdeDTO(expresionEntidad, expresionDTO);
	        entity.setExpresionCensura(expresionEntidad);
	    } else {
	        entity.setExpresionCensura(null);
	    }
	    
	    log.info("[{}] DetencionIntegridad actualizada", key);
	    
	    AccesoJusticiaDTO justiciaDTO = request.getAccesoJusticia();
	    AccesoJusticia justiciaEntidad = entity.getAccesoJusticia();

	    if (justiciaDTO != null) {
	        if (justiciaEntidad == null) {
	        	
	        	justiciaEntidad = new AccesoJusticia();
	        	justiciaEntidad.setPersona(entity); 
	        }
	        eventoUseCase.actualizarJusticiaDesdeDTO(justiciaEntidad, justiciaDTO);
	        entity.setAccesoJusticia(justiciaEntidad);
	    } else {
	        entity.setAccesoJusticia(null);
	    }
	    
	    log.info("[{}] AccesoJusticia actualizada", key);

	    personaRepository.save(entity);
	    log.info("[{}] Entidad personas afectada actualizada correctamente", key);
	    
	    PersonaAfectadaAuditoriaDTO auditoria = utils.crearAuditoriaDesdePersona(entity);
	    auditoriaService.add(utils.crearDto(usuarioAutenticado, UPDATE, auditoria));
	    log.info("[{}] Auditoria creada correctamente", key);
	    
	    RegistroEvento evento = personaRepository.findEventoByPersona(entity);
	    eventoUseCase.actualizarFlagsDerechosPorEvento(evento.getId());

	    return new SuperGenericResponse(OK, "Actualizado correctamente");
	}
}
