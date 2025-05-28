package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarGetAllRegistroPorDerecho;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDeleteEventoByID;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdateEventoByID;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarAddRegistroEvento;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.DerechoVulnerado;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Ubicacion;
import com.uca.idhuca.sistema.indicadores.repositories.CatalogoRepository;
import com.uca.idhuca.sistema.indicadores.repositories.RegistroEventoRepository;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.ICatalogo;
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
	private EventosUseCase eventoUseCase;
	
	@Autowired
	private IAuditoria auditoriaService;
	
	@Autowired
	private CatalogoRepository catalogoRepository;
	
	@Override
	public GenericEntityResponse<List<RegistroEvento>> getAllByDerecho(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarGetAllRegistroPorDerecho(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();

		Catalogo derecho = utils.obtenerCatalogoPorCodigo(request.getDerecho().getCodigo(), key);
		log.info("[{}] Request válido", key);
		
		List<RegistroEvento> ls = registroEventoRepository.findByDerechoAsociadoCodigo(derecho);
		
		if(ls == null || ls.isEmpty()) {
			log.info("[{}] No se encontraron registros", key);
			throw new ValidationException(ERROR, "No se encontraron registros");
		}
		
		return new GenericEntityResponse<List<RegistroEvento>>(OK, "Datos obtenidos correctamente", ls);
	}

	@Override
	public SuperGenericResponse deleteEventoById(RegistroEventoDTO request) throws ValidationException {
		List<String> errorsList = validarDeleteEventoByID(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		
		RegistroEvento evento = utils.obtenerEventoPorID(request.getId(), key);
		log.info("[{}] Evento encontrado correctamente", key);
		
		registroEventoRepository.delete(evento);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), DELETE, evento));
		
		log.info("[{}] Evento eliminado correctamente", key);
		return new SuperGenericResponse(OK, "Evento eliminado correctamente");
	}

	@Override
	public SuperGenericResponse updateEventoById(RegistroEvento request) throws ValidationException {
		List<String> errorsList = validarUpdateEventoByID(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		
		RegistroEvento evento = utils.obtenerEventoPorID(request.getId(), key);
		log.info("[{}] Evento encontrado correctamente", key);
		
		evento = request;
		log.info("[{}] Actualizando registro del evento...", key);
		
		registroEventoRepository.save(evento);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, evento));
		
		log.info("[{}] Registro del evento actualizado correctamente.", key);
		return new SuperGenericResponse(OK, "Registro del evento actualizado correctamente.");
	}

	@Override
	public SuperGenericResponse addEvento(RegistroEventoDTO request) throws ValidationException {
		List<String> errorsList = validarAddRegistroEvento(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);
		
		RegistroEvento nuevoEvento = new RegistroEvento();
		
		nuevoEvento.setFechaRegistro(utils.fechaActual());
		nuevoEvento.setFechaHecho(request.getFechaHecho());
		nuevoEvento.setFuente(catalogoRepository.findByCodigo(request.getFuente().getCodigo()));
		nuevoEvento.setEstadoActual(catalogoRepository.findByCodigo(request.getEstadoActual().getCodigo()));
		nuevoEvento.setDerechoAsociado(catalogoRepository.findByCodigo(request.getDerechoAsociado().getCodigo()));
		
		nuevoEvento.setFlagViolencia(request.isFlagViolencia());
		nuevoEvento.setFlagDetencion(request.isFlagDetencion());
		nuevoEvento.setFlagExpresion(request.isFlagDetencion());
		nuevoEvento.setFlagJusticia(request.isFlagJusticia());
		nuevoEvento.setFlagCensura(request.isFlagCensura());
		nuevoEvento.setFlagRegimenExcepcion(request.isFlagRegimenExcepcion());
		
		nuevoEvento.setObservaciones(request.getObservaciones());
		nuevoEvento.setCreadoPor(utils.obtenerUsuarioAutenticado());
		
		Ubicacion ubicacion = eventoUseCase.mapearUbicacionDesdeDto(request.getUbicacion());
		nuevoEvento.setUbicacion(ubicacion);

		List<PersonaAfectada> personasAfectadas = new ArrayList<>();
		
		for(PersonaAfectadaDTO persona: request.getPersonasAfectadas()) {
			PersonaAfectada entity = new PersonaAfectada();
			
			entity.setNombre(persona.getNombre());
			entity.setGenero(catalogoRepository.findByCodigo(persona.getGenero().getCodigo()));
			entity.setEdad(persona.getEdad());
			entity.setNacionalidad(catalogoRepository.findByCodigo(persona.getNacionalidad().getCodigo()));
			entity.setDepartamentoResidencia(catalogoRepository.findByCodigo(persona.getDepartamentoResidencia().getCodigo()));
			entity.setMunicipioResidencia(catalogoRepository.findByCodigo(persona.getMunicipioResidencia().getCodigo()));
			entity.setTipoPersona(catalogoRepository.findByCodigo(persona.getTipoPersona().getCodigo()));
			entity.setEstadoSalud(catalogoRepository.findByCodigo(persona.getEstadoSalud().getCodigo()));
			
			entity.setDerechosVulnerados(new ArrayList<>());
			for(DerechoVulnerado derecho: persona.getDerechosVulnerados()) {
				Catalogo derechoVulnerado = null;
				
				derechoVulnerado = catalogoRepository.findByCodigo(derecho.getDerecho().getCodigo());
				
				if(derechoVulnerado == null) {
					System.out.println("No se encontro el derecho vulnerado con codigo " + derecho.getDerecho().getCodigo());
					throw new ValidationException(ERROR, 
							"No se encontro el derecho vulnerado con codigo " + derecho.getDerecho().getCodigo());					
				}
				
				DerechoVulnerado derechoFormateado = new DerechoVulnerado();
				derechoFormateado.setDerecho(derechoVulnerado);
				entity.getDerechosVulnerados().add(derechoFormateado);
			}
			
			personasAfectadas.add(entity);
		}
		nuevoEvento.setPersonasAfectadas(personasAfectadas);

		RegistroEvento nuevoEventoSaved = registroEventoRepository.save(nuevoEvento);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, nuevoEventoSaved));
		
		log.info("[{}] Registro del evento agregado correctamente.", key);
		return new SuperGenericResponse(OK, "Registro del evento agregado correctamente.");
	}
	

}
