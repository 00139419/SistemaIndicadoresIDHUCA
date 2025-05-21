package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class UbicacionDTO {

    private Catalogo departamento;
    private Catalogo municipio;
    private Catalogo lugarExacto;

}
