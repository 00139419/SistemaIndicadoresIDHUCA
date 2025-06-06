package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarAddRegistroEvento;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDeleteEventoByID;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarGetAllRegistroPorDerecho;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdateRegistroEvento;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.auditoria.dto.AuditoriaRegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.BusquedaRegistroEventoResultado;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.PaginacionInfo;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Paginacion;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.RegistroEventoRepository;
import com.uca.idhuca.sistema.indicadores.repositories.custom.RegistroEventoRepositoryCustom;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.IRegistros;
import com.uca.idhuca.sistema.indicadores.useCase.EventosUseCase;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegistrosImpl implements IRegistros{
	
	@Autowired
	private Utilidades utils;

	@Autowired
	private RegistroEventoRepository registroEventoRepository;
	
	@Autowired
	private RegistroEventoRepositoryCustom registroEventoRepositoryCustom;
	
	@Autowired
	private EventosUseCase eventoUseCase;
	
	@Autowired
	private IAuditoria auditoriaService;
	
	@Autowired
	ObjectMapper mapper;
	
	@Override
	public GenericEntityResponse<List<RegistroEvento>> getAllByDerecho(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarGetAllRegistroPorDerecho(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();

		Catalogo derechoAsociado = utils.obtenerCatalogoPorCodigo(request.getDerecho().getCodigo(), key);
		request.setDerecho(derechoAsociado);

		log.info("[{}] Request válido", key);

		BusquedaRegistroEventoResultado resultado = registroEventoRepositoryCustom.buscarEventos(request);

		List<RegistroEvento> registros = resultado.getResultados();
		if (registros == null || registros.isEmpty()) {
			log.info("[{}] No hay mas registros que mostrar", key);
			throw new ValidationException(ERROR, "No hay mas registros que mostrar");
		}
		
		Paginacion pag = request.getFiltros() != null ? request.getFiltros().getPaginacion() : null;
		PaginacionInfo info = new PaginacionInfo();
		
		if(pag == null) {
			info.setPaginaActual(0);
			info.setRegistrosPorPagina(registros.size());
			info.setTotalRegistros(registros.size());
			info.setTotalPaginas(1);
		} else {
			info.setPaginaActual(pag.getPaginaActual());
			info.setRegistrosPorPagina(pag.getRegistrosPorPagina());
			info.setTotalRegistros(resultado.getTotal());
			info.setTotalPaginas((int) Math.ceil((double) resultado.getTotal() / pag.getRegistrosPorPagina()));
		}
		
		return new GenericEntityResponse<>(OK, "Datos obtenidos correctamente", registros, info);
	}

	@Override
	public SuperGenericResponse deleteEventoById(RegistroEventoDTO request) throws ValidationException {
		List<String> errorsList = validarDeleteEventoByID(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		Usuario usuario = utils.obtenerUsuarioAutenticado();
		String key = usuario.getEmail();
		
		RegistroEvento evento = utils.obtenerEventoPorID(request.getId(), key);
		log.info("[{}] Evento encontrado correctamente", key);
		
		registroEventoRepository.delete(evento);
		auditoriaService.add(utils.crearDto(usuario, DELETE, evento));
		
		log.info("[{}] Evento eliminado correctamente", key);
		return new SuperGenericResponse(OK, "Evento eliminado correctamente");
	}

	@Override
	public SuperGenericResponse updateEvento(RegistroEventoDTO request) throws ValidationException {
		List<String> errorsList = validarUpdateRegistroEvento(request);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    Usuario usuario = utils.obtenerUsuarioAutenticado();
	    String key = usuario.getEmail();
	    
	    RegistroEvento evento = registroEventoRepository.findById(request.getId()).orElseThrow(() -> {
	    	log.info("[{}] No existe el evento con ID: {}" , key, request.getId());
	    	return new ValidationException(ERROR, " No existe el evento con ID " + request.getId());
	    });
	    
	    log.info("[{}] Request válido", key);
	    
	    eventoUseCase.actualizarDatosBasicosRegistro(evento, request);
	    registroEventoRepository.save(evento);
	    
	    AuditoriaRegistroEventoDTO auditoria = utils.fromRegistroEvento(evento);
	    auditoriaService.add(utils.crearDto(usuario, UPDATE, auditoria));
	    
	    log.info("[{}] Datos basicos del evento actualizados", key);
		return new SuperGenericResponse(OK, "Datos basicos del evento actualizados.");
	}

	@Override
	public SuperGenericResponse addEvento(RegistroEventoDTO request) throws ValidationException, Exception {
	    List<String> errorsList = validarAddRegistroEvento(request);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    Usuario usuario = utils.obtenerUsuarioAutenticado();
	    String key = usuario.getEmail();
	    log.info("[{}] Request válido", key);

	    RegistroEvento nuevoEvento = new RegistroEvento(usuario);
	    eventoUseCase.actualizarDatosGeneralesDesdeDTO(nuevoEvento, request);

	    boolean flagViolencia = request.isFlagViolencia();
	    boolean flagDetencion = request.isFlagDetencion();
	    boolean flagExpresion = request.isFlagExpresion();
	    boolean flagJusticia = request.isFlagJusticia();

	    List<PersonaAfectada> personasAfectadas = new ArrayList<>();
	    for (PersonaAfectadaDTO dto : request.getPersonasAfectadas()) {
	        PersonaAfectada persona = 
	        		eventoUseCase.crearPersonaAfectadaDesdeDTO(dto, nuevoEvento, flagViolencia, flagDetencion, flagExpresion, flagJusticia);
	        personasAfectadas.add(persona);
	        
	        log.info("[{}] Persona afectada creada correctamente: {}", key, mapper.writeValueAsString(persona));
	    }

	    nuevoEvento.setPersonasAfectadas(personasAfectadas);

	    RegistroEvento nuevoEventoSaved = registroEventoRepository.save(nuevoEvento);
	    AuditoriaRegistroEventoDTO auditoria = utils.fromRegistroEvento(nuevoEventoSaved);
	    auditoriaService.add(utils.crearDto(usuario, CREAR, auditoria));

	    log.info("[{}] Registro del evento agregado correctamente.", key);
	    return new SuperGenericResponse(OK, "Registro del evento agregado correctamente.");
	}

	@Override
	public GenericEntityResponse<RegistroEvento> getOne(RegistroEventoDTO request) throws ValidationException {
		List<String> errorsList = validarDeleteEventoByID(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		
		RegistroEvento evento = utils.obtenerEventoPorID(request.getId(), key);
		log.info("[{}] Evento encontrado correctamente", key);
		
		
		log.info("[{}] Evento encontrado correctamente", key);
		return new GenericEntityResponse<RegistroEvento>(OK, "Evento obtenido correctamente", evento);
	}
	
	
}
