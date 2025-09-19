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
    private RangoNumero duracionDiasExactos;
    private List<Integer> diasExactos; // para generacion de graficos
    private Boolean accesoAbogado;
    private List<String> resultados;
}

