package com.uca.idhuca.sistema.indicadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UsuarioSimple;

@Data
@AllArgsConstructor
public class NotaDerechoDTO {
    private Long id;
    private String derechoCodigo;
    private Date fecha;
    private String titulo;
    private String descripcion;
    private List<NotaDerechoArchivoDTO> archivos;
    private UsuarioSimple creadoPor;
    private Date creadoEn;
    private UsuarioSimple modificadoPor;
    private Date modificadoEn;
}

