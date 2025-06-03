package com.uca.idhuca.sistema.indicadores.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
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
@Table(name = "violencia")
public class Violencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violencia_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    @JsonIgnore
    private PersonaAfectada persona;

    @Column(name = "es_asesinato")
    private Boolean esAsesinato;

    @ManyToOne
    @JoinColumn(name = "tipo_violencia_codigo", referencedColumnName = "codigo")
    private Catalogo tipoViolencia;

    @ManyToOne
    @JoinColumn(name = "artefacto_utilizado_codigo", referencedColumnName = "codigo")
    private Catalogo artefactoUtilizado;

    @ManyToOne
    @JoinColumn(name = "contexto_codigo", referencedColumnName = "codigo")
    private Catalogo contexto;

    @ManyToOne
    @JoinColumn(name = "actor_responsable_codigo", referencedColumnName = "codigo")
    private Catalogo actorResponsable;

    @ManyToOne
    @JoinColumn(name = "estado_salud_actor_responsable_codigo", referencedColumnName = "codigo")
    private Catalogo estadoSaludActorResponsable;

    @Column(name = "hubo_proteccion")
    private Boolean huboProteccion;

    @Column(name = "investigacion_abierta")
    private Boolean investigacionAbierta;

    @Column(name = "respuesta_estado")
    private String respuestaEstado;
}
