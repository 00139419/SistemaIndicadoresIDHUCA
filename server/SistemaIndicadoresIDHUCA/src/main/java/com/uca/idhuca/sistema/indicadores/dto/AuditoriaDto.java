package com.uca.idhuca.sistema.indicadores.dto;

import com.uca.idhuca.sistema.indicadores.models.Usuario;

import lombok.Data;

@Data
public class AuditoriaDto<E> {
	private Usuario usuario;
	private String operacion;
	private String registroModificado;
	private E entity;
}
