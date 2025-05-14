package com.uca.idhuca.sistema.indicadores.controllers.dto;

import lombok.Data;

@Data
public class CatalogoDto {
	Boolean roles;
	Boolean departamentos;
	Boolean municipios;
	Boolean securityQuestions;
	String parentId;
	String nuevoCatalogo;
}
