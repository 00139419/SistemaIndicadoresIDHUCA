package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarCatalogoRequest;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.controllers.dto.GetCatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoCatalogo;
import com.uca.idhuca.sistema.indicadores.services.ICatalogo;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_ROL;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_SECURITY_QUESTION;

@Slf4j
@Service
public class CatologoImpl implements ICatalogo {
	
	@Autowired
	private Utilidades utils;
	
	@Autowired
	IRepoCatalogo repoCatalogo;

	@Override
	public GenericEntityResponse<List<Catalogo>> get(GetCatalogoDto request)
			throws ValidationException, NotFoundException {
		List<String> errorsList = validarCatalogoRequest(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);
		
		String nombreCampoActivo = null;
		for (Field field : request.getClass().getDeclaredFields()) {
	        field.setAccessible(true);
	        try {
	            Object value = field.get(request);
	            if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
	                if (nombreCampoActivo != null) {
	                    throw new IllegalArgumentException("Solo se puede activar un catálogo a la vez.");
	                }
	                nombreCampoActivo = field.getName();
	            }
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	    }

	    if (nombreCampoActivo == null) {
	        throw new IllegalArgumentException("Debe activar al menos un catálogo.");
	    }

	    List<Catalogo> list = null;
	    
	    switch (nombreCampoActivo) {
	        case "roles":
	            list = repoCatalogo.obtenerCatalogo(CATALOGO_ROL);
	            break;
	        case "securityQuestion":
	        	 list = repoCatalogo.obtenerCatalogo(CATALOGO_SECURITY_QUESTION);
	        	break;
	        default:
	            throw new NotFoundException(ERROR,"Catálogo no reconocido: " + nombreCampoActivo);
	    }
	    
		return new GenericEntityResponse<List<Catalogo>>(OK, "Catalogo obtenido correctamente.", list);
	}

}
