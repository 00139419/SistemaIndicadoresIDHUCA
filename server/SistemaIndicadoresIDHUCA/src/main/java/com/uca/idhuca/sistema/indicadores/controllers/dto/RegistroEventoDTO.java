package com.uca.idhuca.sistema.indicadores.controllers.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.Usuario;

import lombok.Data;


@Data
public class RegistroEventoDTO {

	private LocalDate fechaHecho;
    private LocalDateTime fechaRegistro;

    private Catalogo fuente;
    private Catalogo estadoActual;
    private Catalogo derechoAsociadoCodigo;

    private boolean flagViolencia;
    private boolean flagDetencion;
    private boolean flagExpresion;
    private boolean flagJusticia;
    private boolean flagCensura;
    private boolean flagRegimenExcepcion;

    private String observaciones;

    private Usuario creadoPor;

    private UbicacionDTO ubicacion;

    private List<PersonaAfectadaDTO> personasAfectadas;
}
