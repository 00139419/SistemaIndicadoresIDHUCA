package com.uca.idhuca.sistema.indicadores.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.uca.idhuca.sistema.indicadores.dto.AuditoriaDto;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoParametrosSistema;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoUsuario;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.MAX_INTENTOS_PREGUNTA_SEGURIDAD;

@Component
public class Utilidades {
	
	@Autowired
	IRepoUsuario usuarioRepo;
	
	@Autowired
	IRepoParametrosSistema sistema;
	
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

        return usuarioRepo.findByEmail(email)
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
}
