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
@Table(name = "derecho_vulnerado")
public class DerechoVulnerado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_afectada_id")
    @JsonIgnore
    private PersonaAfectada personaAfectada;

    @ManyToOne
    @JoinColumn(name = "derecho_vulnerado_codigo", referencedColumnName = "codigo")
    private Catalogo derecho;

}

