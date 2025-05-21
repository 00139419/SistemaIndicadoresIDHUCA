package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class DetencionIntegridadDTO {
	private Catalogo tipoDetencion;
	private boolean ordenJudicial;
	private Catalogo autoridadInvolucrada;
	private boolean huboTortura;
	private int duracionDias;
	private boolean accesoAbogado;
	private String resultado;
	private Catalogo motivoDetencion;
}
