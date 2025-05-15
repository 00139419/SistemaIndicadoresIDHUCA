package com.uca.idhuca.sistema.indicadores.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.dto.AuditoriaDto;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoCatalogo;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoParametrosSistema;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoUsuario;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.MAX_INTENTOS_PREGUNTA_SEGURIDAD;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Utilidades {
	
	@Autowired
	IRepoUsuario usuarioRepo;
	
	@Autowired
	IRepoParametrosSistema sistema;
	
	@Autowired
	IRepoCatalogo catalogoRepo;
	
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
    
    public int obtenerUltimoIndiceCatalogo(String prefijo, String parentId) {
    	List<Catalogo> lista = new ArrayList<>();
    	
    	if(parentId != null) {
    		lista = catalogoRepo.obtenerCatalogo(prefijo + parentId + "_");
    	} else {
    		 lista = catalogoRepo.obtenerCatalogo(prefijo);
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

}
