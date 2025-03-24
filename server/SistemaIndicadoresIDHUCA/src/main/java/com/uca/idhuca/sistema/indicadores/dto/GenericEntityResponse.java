package com.uca.idhuca.sistema.indicadores.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class GenericEntityResponse<T> extends SuperGenericResponse {
	T entity;
}
