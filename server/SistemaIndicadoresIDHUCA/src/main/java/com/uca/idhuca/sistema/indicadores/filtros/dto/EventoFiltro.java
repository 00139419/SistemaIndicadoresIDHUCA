package com.uca.idhuca.sistema.indicadores.filtros.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class EventoFiltro {
    private RangoFechas fechaHechoRango;
    private List<Catalogo> fuentes;
    private List<Catalogo> estadosActuales;
    private Boolean flagViolencia;
    private Boolean flagDetencion;
    private Boolean flagExpresion;
    private Boolean flagJusticia;
    private Boolean flagCensura;
    private Boolean flagRegimenExcepcion;
    private List<Catalogo> departamentos;
    private List<Catalogo> municipios;
    private List<Catalogo> lugaresExactos;
}
