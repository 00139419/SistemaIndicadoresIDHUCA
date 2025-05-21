package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "derecho_vulnerado")
public class DerechoVulnerado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_afectada_id")
    private PersonaAfectada personaAfectada;

    @ManyToOne
    @JoinColumn(name = "derecho_vulnerado_codigo", referencedColumnName = "codigo")
    private Catalogo derecho;

}

