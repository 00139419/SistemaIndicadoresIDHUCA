package com.uca.idhuca.sistema.indicadores.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegistroEventoAuditDto {
    private Long id;
    private LocalDate fechaHecho;
    private String fuente;
    private String estadoActual;
    private String derechoAsociado;
    private Boolean flagViolencia;
    private Boolean flagDetencion;
    private String observaciones;
}
