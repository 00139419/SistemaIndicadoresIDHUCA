package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarGetAllRegistroPorDerecho;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDeleteEventoByID;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDetelePersonByIDEvento;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarGetOnePersonaAfectadaById;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdatePersonByIDEvento;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.auditoria.dto.AuditoriaRegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.DerechoVulnerado;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
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

	    PersonaAfectada entity = personaRepository.findById(request.getId()).orElseThrow(() -> {
	    	log.info("[{}] Persona afectada encontrada con ID: {}", key, request.getId());
	    	return new ValidationException(ERROR, "Persona afectada encontrada con ID: " + request.getId());
	    });
	    log.info("[{}] Request válido", key);
		  
	    
	    
	    List<DerechoVulnerado> nuevos = new ArrayList<>();
	    List<DerechoVulnerado> modificados = new ArrayList<>();
	    List<DerechoVulnerado> eliminados = new ArrayList<>();

	    Map<Long, DerechoVulnerado> dtoMap = request.getDerechosVulnerados().stream()
	        .filter(dv -> dv.getId() != null)
	        .collect(Collectors.toMap(DerechoVulnerado::getId, dv -> dv));

	    List<DerechoVulnerado> nuevosDesdeDTO = request.getDerechosVulnerados().stream()
	        .filter(dv -> dv.getId() == null)
	        .collect(Collectors.toList());

	    Map<Long, DerechoVulnerado> entityMap = entity.getDerechosVulnerados().stream()
	        .filter(dv -> dv.getId() != null)
	        .collect(Collectors.toMap(DerechoVulnerado::getId, dv -> dv));

	    for (Map.Entry<Long, DerechoVulnerado> entry : entityMap.entrySet()) {
	        Long id = entry.getKey();
	        DerechoVulnerado existente = entry.getValue();

	        if (!dtoMap.containsKey(id)) {
	            eliminados.add(existente);
	        } else {
	            DerechoVulnerado actualizado = dtoMap.get(id);
	            if (!existente.getDerecho().getCodigo().equals(actualizado.getDerecho().getCodigo())) {
	                existente.setDerecho(actualizado.getDerecho());
	                modificados.add(existente);
	            }
	        }
	    }
	    
	    
		return null;
	}

}
