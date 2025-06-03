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

@Table(name = "expresion_censura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ExpresionCensura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expresion_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    @JsonIgnore
    private PersonaAfectada persona;

    @ManyToOne
    @JoinColumn(name = "medio_expresion_codigo", referencedColumnName = "codigo")
    private Catalogo medioExpresion;

    @ManyToOne
    @JoinColumn(name = "tipo_represion_codigo", referencedColumnName = "codigo")
    private Catalogo tipoRepresion;

    @Column(name = "represalias_legales")
    private Boolean represaliasLegales;

    @Column(name = "represalias_fisicas")
    private Boolean represaliasFisicas;

    @ManyToOne
    @JoinColumn(name = "actor_censor_codigo", referencedColumnName = "codigo")
    private Catalogo actorCensor;

    @Column(name = "consecuencia")
    private String consecuencia;
}
