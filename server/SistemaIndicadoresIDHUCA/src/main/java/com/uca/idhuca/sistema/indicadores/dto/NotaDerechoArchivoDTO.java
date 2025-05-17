package com.uca.idhuca.sistema.indicadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotaDerechoArchivoDTO {
    private String nombreOriginal;
    private String tipo;
    private String archivoUrl;
}