package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class DetencionIntegridadDTO {
	private Catalogo tipoDetencion;
	private Boolean ordenJudicial;
	private Catalogo autoridadInvolucrada;
	private Boolean huboTortura;
	private int duracionDias;
	private Boolean accesoAbogado;
	private String resultado;
	private Catalogo motivoDetencion;
}
