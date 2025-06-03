package com.uca.idhuca.sistema.indicadores.controllers.dto;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.DerechoVulnerado;

import lombok.Data;

@Data
public class PersonaAfectadaDTO {

	Long Id;
    private String nombre;
    private Integer edad;
    private Catalogo genero;
    private Catalogo nacionalidad;
    private Catalogo departamentoResidencia;
    private Catalogo municipioResidencia;
    private Catalogo tipoPersona;
    private Catalogo estadoSalud;
    
    private List<DerechoVulnerado> derechosVulnerados;
    
    private ViolenciaDTO violencia;
    private DetencionIntegridadDTO detencion;
    private ExpresionCensuraDTO expresion;
    private AccesoJusticiaDTO justicia;
    
}
