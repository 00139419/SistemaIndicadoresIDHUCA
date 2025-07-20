package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class ViolenciaDTO {

	private Boolean esAsesinato;
    private Catalogo tipoViolencia;
    private Catalogo artefactoUtilizado;
    private Catalogo contexto;
    private Catalogo actorResponsable;
    private Catalogo estadoSaludActorResponsable;
    private Boolean huboProteccion;
    private Boolean investigacionAbierta;
    private String respuestaEstado;
}
