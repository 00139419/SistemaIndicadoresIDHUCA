package com.uca.idhuca.sistema.indicadores.filtros.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class ViolenciaFiltro {
    private Boolean esAsesinato;
    private List<Catalogo> tiposViolencia;
    private List<Catalogo> artefactosUtilizados;
    private List<Catalogo> contextos;
    private List<Catalogo> actoresResponsables;
    private List<Catalogo> estadosSaludActorResponsable;
    private Boolean huboProteccion;
    private Boolean investigacionAbierta;
}
