package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class ExpresionCensuraDTO {

	private Catalogo medioExpresion;
    private Catalogo tipoRepresion;
    private Boolean represaliasLegales;
    private Boolean represaliasFisicas;
    private Catalogo actorCensor;
    private String consecuencia;
}
