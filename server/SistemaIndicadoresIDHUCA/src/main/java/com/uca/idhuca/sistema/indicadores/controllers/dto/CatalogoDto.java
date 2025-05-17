package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class CatalogoDto {
	Boolean derechos;
	Boolean roles;
	Boolean departamentos;
	Boolean municipios;
	Boolean securityQuestions;
	String parentId;
	String nuevoCatalogo;
	Catalogo catalogo;
}
