package com.uca.idhuca.sistema.indicadores.controllers.dto;

import java.util.Date;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class AccesoJusticiaDTO {

	private Catalogo tipoProceso;
    private Date fechaDenuncia;
    private Catalogo tipoDenunciante;
    private Catalogo duracionProceso;
    private boolean accesoAbogado;
    private boolean huboParcialidad;
    private String resultadoProceso;
    private String instancia;
}
