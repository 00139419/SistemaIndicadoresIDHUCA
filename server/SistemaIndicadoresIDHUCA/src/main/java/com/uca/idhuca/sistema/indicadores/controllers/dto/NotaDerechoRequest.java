package com.uca.idhuca.sistema.indicadores.controllers.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class NotaDerechoRequest {
	private Long id;
    private Catalogo derecho;
    private String titulo;
    private String descripcion;
    private List<ArchivoAdjuntoRequest> archivos;
}

