package com.uca.idhuca.sistema.indicadores.filtros.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class PersonaAfectadaFiltro {
    private List<String> nombres;
    private List<Catalogo> generos;
    private List<Catalogo> nacionalidades;
    private List<Catalogo> departamentosResidencia;
    private List<Catalogo> municipiosResidencia;
    private List<Catalogo> tiposPersona;
    private List<Catalogo> estadosSalud;
    private RangoEdad rangoEdad;
    private List<Integer> edades;
}
