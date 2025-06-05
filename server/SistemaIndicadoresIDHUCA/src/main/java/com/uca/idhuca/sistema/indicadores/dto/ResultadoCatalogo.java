package com.uca.idhuca.sistema.indicadores.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoCatalogo {
    private List<Catalogo> datos;
    private long totalRegistros;
}
