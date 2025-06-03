package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class CatalogoDto {
	Boolean tipoProcesoJudicial;
	Boolean tipoDenunciante;
	Boolean duracionProceso;
	Boolean medioExpresion;
	Boolean tipoRepresion;
	Boolean motivoDetencion;
	Boolean tipoArma;
	Boolean tipoDetencion;
	Boolean tipoViolencia;
	Boolean estadoSalud;
	Boolean tipoPersona;
	Boolean genero;
	Boolean lugarExacto;
	Boolean estadoRegistro;
	Boolean fuentes;
	Boolean paises;
	Boolean subDerechos;
	Boolean derechos;
	Boolean roles;
	Boolean departamentos;
	Boolean municipios;
	Boolean securityQuestions;
	String parentId;
	String nuevoCatalogo;
	Catalogo catalogo;
	Catalogo derecho;
}
