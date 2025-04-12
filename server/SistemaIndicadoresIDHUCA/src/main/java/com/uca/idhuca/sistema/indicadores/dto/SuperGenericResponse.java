package com.uca.idhuca.sistema.indicadores.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperGenericResponse {
	private int codigo;
	private String mensaje;
	
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        	return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Error al convertir a JSON\"}";
        }
    }
    
}
