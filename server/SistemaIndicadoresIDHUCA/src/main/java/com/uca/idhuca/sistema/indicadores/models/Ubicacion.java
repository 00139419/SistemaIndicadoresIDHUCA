package com.uca.idhuca.sistema.indicadores.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ubicacion")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    @JsonIgnore
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
