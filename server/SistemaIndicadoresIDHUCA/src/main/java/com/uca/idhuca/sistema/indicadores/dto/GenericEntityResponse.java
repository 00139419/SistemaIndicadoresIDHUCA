package com.uca.idhuca.sistema.indicadores.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class GenericEntityResponse<T> extends SuperGenericResponse {
	T entity;
	
	public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "{\"error\": \"No se pudo convertir a JSON: " + e.getMessage() + "\"}";
        }
    }
	
	public GenericEntityResponse() {
		super();
	}

	public GenericEntityResponse(int codigo, String mensaje) {
		super(codigo,mensaje);
	}
	
	public GenericEntityResponse(int codigo, String mensaje, T entity) {
		super(codigo,mensaje);
		this.entity = entity;
	}
}
