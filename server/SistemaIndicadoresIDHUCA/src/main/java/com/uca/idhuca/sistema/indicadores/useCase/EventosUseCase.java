package com.uca.idhuca.sistema.indicadores.useCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.AccesoJusticiaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.DetencionIntegridadDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ExpresionCensuraDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ViolenciaDTO;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.AccesoJusticia;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.DerechoVulnerado;
import com.uca.idhuca.sistema.indicadores.models.DetencionIntegridad;
import com.uca.idhuca.sistema.indicadores.models.ExpresionCensura;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Ubicacion;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.models.Violencia;
import com.uca.idhuca.sistema.indicadores.repositories.CatalogoRepository;
import com.uca.idhuca.sistema.indicadores.repositories.RegistroEventoRepository;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EventosUseCase {
	
	@Autowired
	CatalogoRepository catalogoRepository;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	private RegistroEventoRepository eventoRepository;
	
	@Autowired
	private Utilidades utils;
	
	public PersonaAfectada crearPersonaAfectadaDesdeDTO(
	        PersonaAfectadaDTO dto,
	        RegistroEvento evento,
	        boolean flagViolencia,
	        boolean flagDetencion,
	        boolean flagExpresion,
	        boolean flagJusticia
	) throws ValidationException {
	    PersonaAfectada persona = new PersonaAfectada();
	    actualizarDatosPersonalesDesdeDTO(persona, dto, true);

	    log.info("[{}] Primero if violencia: " + (flagViolencia && dto.getViolencia() != null), "SISTEM");
	    
	    if (flagViolencia && dto.getViolencia() != null) {
	        Violencia violencia = new Violencia();
	        actualizarViolenciaDesdeDTO(violencia, dto.getViolencia());
	        violencia.setPersona(persona);
	        persona.setViolencia(violencia);
	        
	        log.info("[{}] Primero if violencia resultado: " + (violencia.toString()), "SISTEM");
	    }

	    log.info("[{}] Primero if detencion: " + (flagDetencion && dto.getDetencionIntegridad() != null), "SISTEM");
	    
	    if (flagDetencion && dto.getDetencionIntegridad() != null) {
	        DetencionIntegridad detencion = new DetencionIntegridad();
	        actualizarIntegridadDesdeDTO(detencion, dto.getDetencionIntegridad());
	        detencion.setPersona(persona);
	        persona.setDetencionIntegridad(detencion);
	        
	        log.info("[{}] Primero if detencion resultado: " + (detencion.toString()), "SISTEM");
	    }

	    log.info("[{}] Primero if expresion: " + (flagExpresion && dto.getExpresionCensura() != null), "SISTEM");
	    
	    if (flagExpresion && dto.getExpresionCensura() != null) {
	        ExpresionCensura expresion = new ExpresionCensura();
	        actualizarExpresionDesdeDTO(expresion, dto.getExpresionCensura());
	        expresion.setPersona(persona);
	        persona.setExpresionCensura(expresion);
	        
	        log.info("[{}] Primero if expresion resultado: " + (expresion.toString()), "SISTEM");
	    }

	    log.info("[{}] Primero if justicia: " + (flagJusticia && dto.getAccesoJusticia() != null), "SISTEM");
	    
	    if (flagJusticia && dto.getAccesoJusticia() != null) {
	        AccesoJusticia justicia = new AccesoJusticia();
	        actualizarJusticiaDesdeDTO(justicia, dto.getAccesoJusticia());
	        justicia.setPersona(persona);
	        persona.setAccesoJusticia(justicia);
	        
	        log.info("[{}] Primero if justicia resultado: " + (justicia.toString()), "SISTEM");
	    }

	    persona.setEvento(evento);
	    return persona;
	}

	
	public void actualizarDatosPersonalesDesdeDTO(PersonaAfectada entidad, PersonaAfectadaDTO dto, boolean applyList) throws ValidationException {
		entidad.setNombre(dto.getNombre());
		entidad.setEdad(dto.getEdad());
		entidad.setGenero(getCatalogoOrThrow(dto.getGenero().getCodigo(), "género"));
		entidad.setNacionalidad(getCatalogoOrThrow(dto.getNacionalidad().getCodigo(), "nacionalidad"));
		entidad.setDepartamentoResidencia(getCatalogoOrThrow(dto.getDepartamentoResidencia().getCodigo(), "departamento de residencia"));
		entidad.setMunicipioResidencia(getCatalogoOrThrow(dto.getMunicipioResidencia().getCodigo(), "municipio de residencia"));
		entidad.setTipoPersona(getCatalogoOrThrow(dto.getTipoPersona().getCodigo(), "tipo de persona"));
		entidad.setEstadoSalud(getCatalogoOrThrow(dto.getEstadoSalud().getCodigo(), "estado de salud"));
		
		if(applyList) {
			List<DerechoVulnerado> derechos = new ArrayList<>();
	        for (DerechoVulnerado derechoDto : dto.getDerechosVulnerados()) {
	            String codigo = derechoDto.getDerecho().getCodigo();
	            Catalogo catalogo = getCatalogoOrThrow(codigo, "derecho vulnerado");

	            DerechoVulnerado derecho = new DerechoVulnerado();
	            derecho.setDerecho(catalogo);
	            derecho.setPersonaAfectada(entidad);
	            derechos.add(derecho);
	        }

	        entidad.setDerechosVulnerados(derechos);
		}
	}
	
	public void actualizarDatosGeneralesDesdeDTO(RegistroEvento entidad, RegistroEventoDTO dto) throws ValidationException {
		entidad.setFechaHecho(dto.getFechaHecho());
		entidad.setFuente(getCatalogoOrThrow(dto.getFuente().getCodigo(), "fuente"));
		entidad.setEstadoActual(getCatalogoOrThrow(dto.getEstadoActual().getCodigo(), "estado actual"));
		entidad.setDerechoAsociado(getCatalogoOrThrow(dto.getDerechoAsociado().getCodigo(), "derecho asociado"));
		entidad.setFlagViolencia(dto.isFlagViolencia());
		entidad.setFlagDetencion(dto.isFlagDetencion());
		entidad.setFlagExpresion(dto.isFlagExpresion());
		entidad.setFlagJusticia(dto.isFlagJusticia());
		entidad.setFlagCensura(dto.isFlagCensura());
		entidad.setFlagRegimenExcepcion(dto.isFlagRegimenExcepcion());
		entidad.setObservaciones(dto.getObservaciones());
		
		Ubicacion ubicacion = new Ubicacion();
		
		ubicacion.setDepartamento(getCatalogoOrThrow(dto.getUbicacion().getDepartamento().getCodigo(), "Departamento"));
		ubicacion.setMunicipio(getCatalogoOrThrow(dto.getUbicacion().getMunicipio().getCodigo(), "Municipio"));
		ubicacion.setLugarExacto(getCatalogoOrThrow(dto.getUbicacion().getLugarExacto().getCodigo(), "Lugar Exacto"));
		ubicacion.setEvento(entidad);
		
		entidad.setUbicacion(ubicacion);
	}
	
	public void actualizarViolenciaDesdeDTO(Violencia entidad, ViolenciaDTO dto) throws ValidationException {
		entidad.setEsAsesinato(dto.getEsAsesinato());
		entidad.setTipoViolencia(getCatalogoOrThrow(dto.getTipoViolencia().getCodigo(), "tipo de violencia"));
		entidad.setArtefactoUtilizado(getCatalogoOrThrow(dto.getArtefactoUtilizado().getCodigo(), "Artefacto utilizado"));
		entidad.setContexto(getCatalogoOrThrow(dto.getContexto().getCodigo(), "Contexto"));
		entidad.setActorResponsable(getCatalogoOrThrow(dto.getActorResponsable().getCodigo(), "Actor responsable"));
		entidad.setEstadoSaludActorResponsable(getCatalogoOrThrow(dto.getEstadoSaludActorResponsable().getCodigo(), "Actor responsable"));
		entidad.setHuboProteccion(dto.getHuboProteccion());
		entidad.setInvestigacionAbierta(dto.getInvestigacionAbierta());
		entidad.setRespuestaEstado(dto.getRespuestaEstado());
	}
	
	public void actualizarIntegridadDesdeDTO(DetencionIntegridad entidad, DetencionIntegridadDTO dto) throws ValidationException {
		entidad.setTipoDetencion(getCatalogoOrThrow(dto.getTipoDetencion().getCodigo(), "Contexto"));	 
		entidad.setOrdenJudicial(dto.getOrdenJudicial());
		entidad.setAutoridadInvolucrada(getCatalogoOrThrow(dto.getAutoridadInvolucrada().getCodigo(), "Autoridad involucrada"));
		entidad.setHuboTortura(dto.getHuboTortura());
		entidad.setDuracionDias(dto.getDuracionDias());
		entidad.setAccesoAbogado(dto.getAccesoAbogado());
		entidad.setResultado(dto.getResultado());
		entidad.setMotivoDetencion(getCatalogoOrThrow(dto.getMotivoDetencion().getCodigo(), "Motivo detencion"));
	}
	
	public void actualizarExpresionDesdeDTO(ExpresionCensura entidad, ExpresionCensuraDTO dto) throws ValidationException {
		entidad.setMedioExpresion(getCatalogoOrThrow(dto.getMedioExpresion().getCodigo(), "Medio de expresion"));
		entidad.setTipoRepresion(getCatalogoOrThrow(dto.getMedioExpresion().getCodigo(), "Tipo de represion"));
		entidad.setRepresaliasLegales(dto.getRepresaliasLegales());
		entidad.setRepresaliasFisicas(dto.getRepresaliasFisicas());
		entidad.setActorCensor(getCatalogoOrThrow(dto.getActorCensor().getCodigo(), "Actor censor: Tipo de persona"));
		entidad.setConsecuencia(dto.getConsecuencia());
	}
	
	public void actualizarJusticiaDesdeDTO(AccesoJusticia entidad, AccesoJusticiaDTO dto) throws ValidationException{
		entidad.setTipoProceso(getCatalogoOrThrow(dto.getTipoProceso().getCodigo(), "Tipo de proceso"));
		entidad.setFechaDenuncia(dto.getFechaDenuncia());
		entidad.setTipoDenunciante(getCatalogoOrThrow(dto.getTipoDenunciante().getCodigo(), "Tipo de denunciante: tipo de persona"));	
		entidad.setDuracionProceso(getCatalogoOrThrow(dto.getDuracionProceso().getCodigo(), "Duracion de proceso"));	                        
		entidad.setAccesoAbogado(dto.getAccesoAbogado());
		entidad.setHuboParcialidad(dto.getHuboParcialidad());
		entidad.setResultadoProceso(dto.getResultadoProceso());
		entidad.setInstancia(dto.getInstancia());
	}
	
	public void actualizarDatosBasicosRegistro(RegistroEvento entidad, RegistroEventoDTO dto) throws ValidationException {
		entidad.setFechaHecho(dto.getFechaHecho());
		entidad.setFuente(getCatalogoOrThrow(dto.getFuente().getCodigo(), "Fuente del hecho"));
		entidad.setEstadoActual(getCatalogoOrThrow(dto.getEstadoActual().getCodigo(), "Estado actual"));
		entidad.setDerechoAsociado(getCatalogoOrThrow(dto.getDerechoAsociado().getCodigo(), "Derecho asociado"));
		entidad.setFlagRegimenExcepcion(dto.isFlagRegimenExcepcion());
		entidad.setObservaciones(dto.getObservaciones());
		
		
		Ubicacion ubicacion = entidad.getUbicacion();
		
		ubicacion.setDepartamento(getCatalogoOrThrow(dto.getUbicacion().getDepartamento().getCodigo(), "Departamento"));
		ubicacion.setMunicipio(getCatalogoOrThrow(dto.getUbicacion().getMunicipio().getCodigo(), "Municipio"));
		ubicacion.setLugarExacto(getCatalogoOrThrow(dto.getUbicacion().getLugarExacto().getCodigo(), "Lugar exacto"));
		
		entidad.setUbicacion(ubicacion);
		
	    boolean flagViolencia = false;
	    boolean flagDetencion = false;
	    boolean flagExpresion = false;
	    boolean flagJusticia = false;
	    boolean flagCensura = false;
	    boolean flagRegimenExcepcion = false;

	    for (PersonaAfectada persona : entidad.getPersonasAfectadas()) {
	        if (!flagViolencia && persona.getViolencia() != null) {
	            flagViolencia = true;
	        }

	        if (!flagDetencion && persona.getDetencionIntegridad() != null) {
	            flagDetencion = true;
	        }

	        if (!flagExpresion && persona.getExpresionCensura() != null) {
	            flagExpresion = true;
	        }

	        if (!flagJusticia && persona.getAccesoJusticia() != null) {
	            flagJusticia = true;
	        }

	        if (!flagCensura && persona.getDerechosVulnerados() != null && !persona.getDerechosVulnerados().isEmpty()) {
	            flagCensura = persona.getDerechosVulnerados().stream()
	                .anyMatch(dv -> dv.getDerecho() != null && dv.getDerecho().getDescripcion().toLowerCase().contains("censura"));
	        }

	        if (flagViolencia && flagDetencion && flagExpresion && flagJusticia && flagCensura && flagRegimenExcepcion) {
	            break;
	        }
	    }

	    entidad.setFlagViolencia(flagViolencia);
	    entidad.setFlagDetencion(flagDetencion);
	    entidad.setFlagExpresion(flagExpresion);
	    entidad.setFlagJusticia(flagJusticia);
	    entidad.setFlagCensura(flagCensura);
	    entidad.setFlagRegimenExcepcion(flagRegimenExcepcion);
	}
	
	public Catalogo getCatalogoOrThrow(String codigo, String campo) throws ValidationException {
	    Catalogo catalogo = catalogoRepository.findByCodigo(codigo);
	    if (catalogo == null) {
	        throw new ValidationException(ERROR, "No se encontró el catálogo para " + campo + " con código: " + codigo);
	    }
	    return catalogo;
	}
	
	@Transactional
	public SuperGenericResponse actualizarFlagsDerechosPorEvento(Long idEvento) throws ValidationException {
	    Usuario usuario = utils.obtenerUsuarioAutenticado();
	    String key = usuario.getEmail();

	    RegistroEvento evento = eventoRepository.findById(idEvento)
	        .orElseThrow(() -> {
	            log.info("[{}] No se encontró el evento con ID: {}", key, idEvento);
	            return new ValidationException(ERROR, "No se encontró el evento con ID: " + idEvento);
	        });

	    boolean flagViolencia = false;
	    boolean flagDetencion = false;
	    boolean flagExpresion = false;
	    boolean flagJusticia = false;
	    boolean flagCensura = false;
	    boolean flagRegimenExcepcion = false;

	    for (PersonaAfectada persona : evento.getPersonasAfectadas()) {
	        if (!flagViolencia && persona.getViolencia() != null) {
	            flagViolencia = true;
	        }

	        if (!flagDetencion && persona.getDetencionIntegridad() != null) {
	            flagDetencion = true;
	        }

	        if (!flagExpresion && persona.getExpresionCensura() != null) {
	            flagExpresion = true;
	        }

	        if (!flagJusticia && persona.getAccesoJusticia() != null) {
	            flagJusticia = true;
	        }

	        if (!flagCensura && persona.getDerechosVulnerados() != null && !persona.getDerechosVulnerados().isEmpty()) {
	            flagCensura = persona.getDerechosVulnerados().stream()
	                .anyMatch(dv -> dv.getDerecho() != null && dv.getDerecho().getDescripcion().toLowerCase().contains("censura"));
	        }

	        if (flagViolencia && flagDetencion && flagExpresion && flagJusticia && flagCensura && flagRegimenExcepcion) {
	            break;
	        }
	    }

	    evento.setFlagViolencia(flagViolencia);
	    evento.setFlagDetencion(flagDetencion);
	    evento.setFlagExpresion(flagExpresion);
	    evento.setFlagJusticia(flagJusticia);
	    evento.setFlagCensura(flagCensura);
	    evento.setFlagRegimenExcepcion(flagRegimenExcepcion);

	    eventoRepository.save(evento);

	    log.info("[{}] Flags actualizadas para el evento con ID: {}", key, idEvento);

	    return new SuperGenericResponse(OK, "Flags actualizadas correctamente");
	}
	
}
