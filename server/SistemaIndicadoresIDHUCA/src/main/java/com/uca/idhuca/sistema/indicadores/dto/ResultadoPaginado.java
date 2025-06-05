package com.uca.idhuca.sistema.indicadores.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResultadoPaginado<T> {
    private List<T> resultados;
    private long totalRegistros;

    public ResultadoPaginado(List<T> resultados, long totalRegistros) {
        this.resultados = resultados;
        this.totalRegistros = totalRegistros;
    }
}