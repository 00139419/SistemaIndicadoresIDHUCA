package com.uca.idhuca.sistema.indicadores.auditoria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaAfectadaAuditoriaDTO {
    private Long id;
    private Long eventoId;
    private String nombre;
    private Integer edad;

    private String generoCodigo;
    private String nacionalidadCodigo;
    private String departamentoResidenciaCodigo;
    private String municipioResidenciaCodigo;
    private String tipoPersonaCodigo;
    private String estadoSaludCodigo;

    private Integer cantidadDerechosVulnerados;

    private boolean tieneViolencia;
    private boolean tieneDetencionIntegridad;
    private boolean tieneExpresionCensura;
    private boolean tieneAccesoJusticia;
}