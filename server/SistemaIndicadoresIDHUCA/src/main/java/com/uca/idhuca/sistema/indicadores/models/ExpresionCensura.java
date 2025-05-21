package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "expresion_censura")
@Data
public class ExpresionCensura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expresion_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_id")
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
