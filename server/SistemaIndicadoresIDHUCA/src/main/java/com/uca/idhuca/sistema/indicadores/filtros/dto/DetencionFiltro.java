package com.uca.idhuca.sistema.indicadores.filtros.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class DetencionFiltro {
    private List<Catalogo> tiposDetencion;
    private Boolean ordenJudicial;
    private List<Catalogo> autoridadesInvolucradas;
    private Boolean huboTortura;
    private List<Catalogo> motivosDetencion;
    private Integer duracionDiasExactos;
    private Catalogo rangoDiasDetencion;
    private Boolean accesoAbogado;
    private List<String> resultados;
}

