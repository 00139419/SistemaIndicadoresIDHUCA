package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ubicacion")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private RegistroEvento evento;

    @ManyToOne
    @JoinColumn(name = "DEP_artamento_codigo", referencedColumnName = "codigo")
    private Catalogo departamento;

    @ManyToOne
    @JoinColumn(name = "municipio_codigo", referencedColumnName = "codigo")
    private Catalogo municipio;

    @ManyToOne
    @JoinColumn(name = "lugar_exacto_codigo", referencedColumnName = "codigo")
    private Catalogo lugarExacto;

}
