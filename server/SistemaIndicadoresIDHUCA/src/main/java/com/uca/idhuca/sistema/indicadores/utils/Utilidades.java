package com.uca.idhuca.sistema.indicadores.utils;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.MAX_INTENTOS_PREGUNTA_SEGURIDAD;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.auditoria.dto.AuditoriaRegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.auditoria.dto.PersonaAfectadaAuditoriaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.AccesoJusticiaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.DetencionIntegridadDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ExpresionCensuraDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ViolenciaDTO;
import com.uca.idhuca.sistema.indicadores.dto.AuditoriaDto;
import com.uca.idhuca.sistema.indicadores.dto.RegistroEventoAuditDto;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.models.Violencia;
import com.uca.idhuca.sistema.indicadores.repositories.CatalogoRepository;
import com.uca.idhuca.sistema.indicadores.repositories.ParametrosSistemaRepository;
import com.uca.idhuca.sistema.indicadores.repositories.RegistroEventoRepository;
import com.uca.idhuca.sistema.indicadores.repositories.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Utilidades {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	ParametrosSistemaRepository sistema;
	
	@Autowired
	CatalogoRepository catalogoRepository;
	
	@Autowired
	RegistroEventoRepository eventoRepository;
	
	/**
     * Obtiene el usuario autenticado a partir del JWT (usando el email del token).
     */
    public Usuario obtenerUsuarioAutenticado() throws ValidationException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException(Constantes.ERROR, "Usuario no encontrado con email: " + email));
    }
    
    public <E> AuditoriaDto<E> crearDto(Usuario usuario, String operacion, E entidad) {
        AuditoriaDto<E> dto = new AuditoriaDto<>();
        dto.setUsuario(usuario);
        dto.setOperacion(operacion);
        dto.setEntity(entidad);

        try {
            Object id = entidad.getClass().getMethod("getId").invoke(entidad);
            if (id instanceof Number) {
                dto.setRegistroModificado(((Number) id).intValue() + "");
            }
        } catch (Exception e) {
        	try {
                Object id = entidad.getClass().getMethod("getCodigo").invoke(entidad);
                if (id instanceof String) {
                    dto.setRegistroModificado((String) id);
                }
            } catch (Exception e2) {
                // Puedes loggear si deseas
                dto.setRegistroModificado("0"); 
            }
        }

        return dto;
    }
    
    public int maximoIntentosPreguntaSeguridad() {
    	return Integer.parseInt(sistema.findByClave(MAX_INTENTOS_PREGUNTA_SEGURIDAD).getValor());
    }
    
    public int obtenerUltimoIndiceCatalogo(String prefijo, String parentId) {
    	List<Catalogo> lista = new ArrayList<>();
    	
    	if(parentId != null) {
    		lista = catalogoRepository.obtenerCatalogo(prefijo + parentId + "_");
    	} else {
    		 lista = catalogoRepository.obtenerCatalogo(prefijo);
    	}
    	
        if (lista.isEmpty()) return 0;

        Catalogo ultimo = lista.get(lista.size() - 1);
        String codigo = ultimo.getCodigo();

        String[] aux = codigo.split("_");
        String codigoMunicipio = aux[aux.length - 1];
        return Integer.parseInt(codigoMunicipio);
    }
    
    public String generarHashArchivo() throws IOException, NoSuchAlgorithmException {
    	// Formato con milisegundos para asegurar unicidad
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        String timestamp = LocalDateTime.now().format(formatter);
        return "archivo_" + timestamp;
    }

    public String getExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    public Catalogo obtenerCatalogoPorCodigo(String codigo, String key) throws ValidationException {
    	Catalogo catalogo = null;
		try {
			catalogo = catalogoRepository
					 .findByCodigo(codigo);
			
			if(catalogo == null) {
				log.info("[{}] Catalogo con ID" + codigo + " no existe.", key);
				throw new ValidationException(ERROR, "Catalogo con ID" + codigo + " no existe.");
			}
			
			return catalogo;
		} catch (ValidationException e) {
			throw e;
		} catch (Exception e) {
			log.info("Catalogo con ID" + codigo + " no existe.");
			throw new ValidationException(ERROR, "Catalogo con ID" + codigo + " no existe.");
		}
    }
    
    
    public RegistroEvento obtenerEventoPorID(Long id, String key) throws ValidationException {
    	RegistroEvento registroEvento = null;
		try {
			registroEvento = eventoRepository
					.findById(id).get();
					
			if(registroEvento == null) {
				log.info("[{}] Evento con ID " + id + " no existe.", key);
				throw new ValidationException(ERROR, "Catalogo con ID " + id + " no existe.");
			}
			
			return registroEvento;
		} catch (ValidationException e) {
			throw e;
		} catch (Exception e) {
			log.info("Evento con ID " + id + " no existe.");
			throw new ValidationException(ERROR, "Evento con ID " + id + " no existe.");
		}
    }
    
    public Date fechaActual() {
        return Date.from(ZonedDateTime.now(ZoneId.of("America/El_Salvador")).toInstant());
    }
    
    public RegistroEventoAuditDto toAuditDto(RegistroEvento evento) {
        RegistroEventoAuditDto dto = new RegistroEventoAuditDto();
        dto.setId(evento.getId());
        dto.setFechaHecho(evento.getFechaHecho());
        dto.setFuente(evento.getFuente().getDescripcion());
        dto.setEstadoActual(evento.getEstadoActual().getDescripcion());
        dto.setDerechoAsociado(evento.getDerechoAsociado().getDescripcion());
        dto.setFlagViolencia(evento.getFlagViolencia());
        dto.setFlagDetencion(evento.getFlagDetencion());
        dto.setObservaciones(evento.getObservaciones());
        return dto;
    }

    public AuditoriaRegistroEventoDTO fromRegistroEvento(RegistroEvento evento) throws ValidationException {
        if (evento == null) {
            return null;
        }

        AuditoriaRegistroEventoDTO dto = new AuditoriaRegistroEventoDTO();

        dto.setId(evento.getId());
        dto.setFechaHecho(evento.getFechaHecho());
        dto.setFuente(evento.getFuente() != null ? evento.getFuente().getDescripcion() : null);
        dto.setEstadoActual(evento.getEstadoActual() != null ? evento.getEstadoActual().getDescripcion() : null);
        dto.setDerechoAsociado(evento.getDerechoAsociado() != null ? evento.getDerechoAsociado().getDescripcion() : null);

        dto.setFlagViolencia(evento.getFlagViolencia());
        dto.setFlagDetencion(evento.getFlagDetencion());
        dto.setFlagExpresion(evento.getFlagExpresion());
        dto.setFlagJusticia(evento.getFlagJusticia());
        dto.setFlagCensura(evento.getFlagCensura());
        dto.setFlagRegimenExcepcion(evento.getFlagRegimenExcepcion());
        dto.setObservaciones(evento.getObservaciones());

        dto.setTotalPersonasAfectadas(
            evento.getPersonasAfectadas() != null ? evento.getPersonasAfectadas().size() : 0
        );

        dto.setCreadoPor(evento.getCreadoPor() != null ? evento.getCreadoPor().getEmail() : null);
        dto.setFechaRegistro(evento.getFechaRegistro());

        return dto;
    }
    
    public PersonaAfectadaAuditoriaDTO crearAuditoriaDesdePersona(PersonaAfectada persona) {
        if (persona == null) return null;

        return new PersonaAfectadaAuditoriaDTO(
            persona.getId(),
            persona.getEvento() != null ? persona.getEvento().getId() : null,
            persona.getNombre(),
            persona.getEdad(),
            persona.getGenero() != null ? persona.getGenero().getCodigo() : null,
            persona.getNacionalidad() != null ? persona.getNacionalidad().getCodigo() : null,
            persona.getDepartamentoResidencia() != null ? persona.getDepartamentoResidencia().getCodigo() : null,
            persona.getMunicipioResidencia() != null ? persona.getMunicipioResidencia().getCodigo() : null,
            persona.getTipoPersona() != null ? persona.getTipoPersona().getCodigo() : null,
            persona.getEstadoSalud() != null ? persona.getEstadoSalud().getCodigo() : null,
            persona.getDerechosVulnerados() != null ? persona.getDerechosVulnerados().size() : 0,
            persona.getViolencia() != null,
            persona.getDetencionIntegridad() != null,
            persona.getExpresionCensura() != null,
            persona.getAccesoJusticia() != null
        );
    }
    
    
    public static void formatDto(ViolenciaDTO violencia) {
    	if(violencia != null) {
    		if (violencia.getActorResponsable() == null || violencia.getActorResponsable().getCodigo() == null) {
    			violencia.setActorResponsable(new Catalogo(Constantes.CATALOGO_TIPO_PERSONA + "0",""));
    		}
    	
	    	if (violencia.getArtefactoUtilizado() == null || violencia.getArtefactoUtilizado().getCodigo() == null) {
	    		violencia.setArtefactoUtilizado(new Catalogo(Constantes.CATALOGO_TIPO_DE_ARMA + "0",""));
		    }
	    	
	    	if (violencia.getContexto() == null || violencia.getContexto().getCodigo() == null) {
	    		violencia.setContexto(new Catalogo(Constantes.CATALOGO_CONTEXTO + "0",""));
		    }
	    	
	    	if (violencia.getEstadoSaludActorResponsable() == null || violencia.getEstadoSaludActorResponsable().getCodigo() == null) {
	    		violencia.setEstadoSaludActorResponsable(new Catalogo(Constantes.CATALOGO_ESTADO_SALUD + "0",""));
		    }
	    	
	    	if (violencia.getRespuestaEstado() == null) {
	    		violencia.setRespuestaEstado("");
		    }
	    	
	    	if (violencia.getTipoViolencia() == null || violencia.getTipoViolencia().getCodigo() == null) {
	    		violencia.setTipoViolencia(new Catalogo(Constantes.CATALOGO_TIPO_DE_VIOLENCIA+ "0",""));
		    }
    	}
    }
    
    
    public static void formatDto(AccesoJusticiaDTO justicia) {
    	if (justicia != null) {
		    if (justicia.getTipoDenunciante() == null || justicia.getTipoDenunciante().getCodigo() == null) {
		    	justicia.setTipoDenunciante(new Catalogo(Constantes.CATALOGO_TIPO_DENUNCIANTE + "0",""));
		    }

		    if (justicia.getDuracionProceso() == null || justicia.getDuracionProceso().getCodigo() == null) {
		    	justicia.setDuracionProceso(new Catalogo(Constantes.CATALOGO_DURACION_PROCESO + "0",""));
		    }

		    if (justicia.getResultadoProceso() == null) {
		    	justicia.setResultadoProceso("");
		    }

		    if (justicia.getInstancia() == null) {
		    	justicia.setInstancia("");
		    }
		    
		    if (justicia.getTipoProceso() == null || justicia.getTipoProceso().getCodigo() == null) {
		    	justicia.setTipoProceso(new Catalogo(Constantes.CATALOGO_TIPO_PROCESO_JUDICIAL + "0",""));
		    }
		}
    }
    
    public static void formatDto(DetencionIntegridadDTO integridad) {
    	if(integridad != null) {
			if (integridad.getTipoDetencion() == null || integridad.getTipoDetencion().getCodigo() == null) {
				integridad.setTipoDetencion(new Catalogo(Constantes.CATALOGO_TIPO_DE_DETENCION + "0",""));
		    }

		    if (integridad.getAutoridadInvolucrada() == null || integridad.getAutoridadInvolucrada().getCodigo() == null) {
		    	integridad.setAutoridadInvolucrada(new Catalogo(Constantes.CATALOGO_TIPO_PERSONA+ "0",""));
		    }

		    if (integridad.getMotivoDetencion() == null || integridad.getMotivoDetencion().getCodigo() == null) {
		    	integridad.setMotivoDetencion(new Catalogo(Constantes.CATALOGO_MOTIVO_DETENCION + "0",""));
		    }

		    if (integridad.getResultado() == null) {
		    	integridad.setResultado("");
		    }
		}
    }
    
    
    public static void formatDto(ExpresionCensuraDTO expresion) {
    	if(expresion != null) {
			if (expresion.getMedioExpresion() == null || expresion.getMedioExpresion().getCodigo() == null) {
				expresion.setMedioExpresion(new Catalogo(Constantes.CATALOGO_MEDIO_DE_EXPRESION+ "0",""));
		    }

		    if (expresion.getTipoRepresion() == null || expresion.getTipoRepresion().getCodigo() == null) {
		    	expresion.setTipoRepresion(new Catalogo(Constantes.CATALOGO_TIPO_DE_REPRESION+ "0",""));
		    }

		    if (expresion.getActorCensor() == null || expresion.getActorCensor().getCodigo() == null) {
		    	expresion.setActorCensor(new Catalogo(Constantes.CATALOGO_TIPO_PERSONA + "0",""));
		    }

		    if (expresion.getConsecuencia() == null) {
		    	expresion.setConsecuencia("");
		    }
		}
    }
    
    public static void formatDto(PersonaAfectadaDTO persona) {
    	if(persona != null) {
    		if(persona.getNombre() == null) {
				persona.setNombre("");
			}
    		
    		if(persona.getGenero() == null || persona.getGenero().getCodigo() == null) {
    			persona.setGenero(new Catalogo(Constantes.CATALOGO_GENERO + "0",""));
    		}
    		
    		if(persona.getNacionalidad() == null || persona.getNacionalidad().getCodigo() == null) {
    			persona.setNacionalidad(new Catalogo(Constantes.CATALOGO_PAISES + "0",""));
    			persona.setDepartamentoResidencia(new Catalogo(Constantes.CATALOGO_DEPARTAMENTO + "0",""));
    			persona.setMunicipioResidencia(new Catalogo(Constantes.CATALOGO_MUNICIPIO + "0_0",""));
    		}
    		
    		if(persona.getNacionalidad() != null && persona.getNacionalidad().getCodigo() != null && persona.getNacionalidad().getCodigo() != "9300") {
    			persona.setDepartamentoResidencia(new Catalogo(Constantes.CATALOGO_DEPARTAMENTO + "0",""));
    			persona.setMunicipioResidencia(new Catalogo(Constantes.CATALOGO_MUNICIPIO + "0_0",""));
    		}
    		
    		if(persona.getNacionalidad() != null && persona.getNacionalidad().getCodigo() != null && persona.getNacionalidad().getCodigo() == "9300") {
    			
    			if(persona.getDepartamentoResidencia() == null || persona.getDepartamentoResidencia().getCodigo() == null) {
    				persona.setDepartamentoResidencia(new Catalogo(Constantes.CATALOGO_DEPARTAMENTO + "0",""));
        			persona.setMunicipioResidencia(new Catalogo(Constantes.CATALOGO_MUNICIPIO + "0_0",""));
    			}
    			
    			if(persona.getMunicipioResidencia() == null || persona.getMunicipioResidencia().getCodigo() == null) {
    				persona.setMunicipioResidencia(new Catalogo(Constantes.CATALOGO_MUNICIPIO + "0_0",""));
    			}
    		}
    		
    		if(persona.getTipoPersona() == null || persona.getTipoPersona().getCodigo() == null) {
    			persona.setTipoPersona(new Catalogo(Constantes.CATALOGO_TIPO_PERSONA + "0",""));
    		}
    		
    		if(persona.getEstadoSalud() == null || persona.getEstadoSalud().getCodigo() == null) {
    			persona.setEstadoSalud(new Catalogo(Constantes.CATALOGO_ESTADO_SALUD + "0",""));
    		}
    		
    		
    	}
    }
}
