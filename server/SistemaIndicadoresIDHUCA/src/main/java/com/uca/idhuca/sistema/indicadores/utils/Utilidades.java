package com.uca.idhuca.sistema.indicadores.utils;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.MAX_INTENTOS_PREGUNTA_SEGURIDAD;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.auditoria.dto.AuditoriaRegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.AuditoriaDto;
import com.uca.idhuca.sistema.indicadores.dto.RegistroEventoAuditDto;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
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
    
    public String generarHashArchivo(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(file.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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

    
}
