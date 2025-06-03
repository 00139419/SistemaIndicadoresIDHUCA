package com.uca.idhuca.sistema.indicadores.auditoria.dto;

import java.time.LocalDate;
import java.util.Date;

import lombok.Data;

@Data
public class AuditoriaRegistroEventoDTO {
	private Long id;
    private LocalDate fechaHecho;
    private String fuente;
    private String estadoActual;
    private String derechoAsociado;

    private Boolean flagViolencia;
    private Boolean flagDetencion;
    private Boolean flagExpresion;
    private Boolean flagJusticia;
    private Boolean flagCensura;
    private Boolean flagRegimenExcepcion;

    private Integer totalPersonasAfectadas;

    private String creadoPor;
    private Date fechaRegistro;
    private String observaciones;
}
