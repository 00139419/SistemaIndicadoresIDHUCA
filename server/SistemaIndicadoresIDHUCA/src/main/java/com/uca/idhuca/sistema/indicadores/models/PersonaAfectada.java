package com.uca.idhuca.sistema.indicadores.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "persona_afectada")
public class PersonaAfectada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    @JsonIgnore
    private RegistroEvento evento;

    @Column(name = "nombre")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "genero_codigo", referencedColumnName = "codigo")
    private Catalogo genero;

    @Column(name = "edad")
    private Integer edad;

    @ManyToOne
    @JoinColumn(name = "nacionalidad_codigo", referencedColumnName = "codigo")
    private Catalogo nacionalidad;

    @ManyToOne
    @JoinColumn(name = "DEP_artamento_residencia_codigo", referencedColumnName = "codigo")
    private Catalogo departamentoResidencia;

    @ManyToOne
    @JoinColumn(name = "municipio_residencia_codigo", referencedColumnName = "codigo")
    private Catalogo municipioResidencia;

    @ManyToOne
    @JoinColumn(name = "tipo_persona_codigo", referencedColumnName = "codigo")
    private Catalogo tipoPersona;

    @ManyToOne
    @JoinColumn(name = "estado_salud_codigo", referencedColumnName = "codigo")
    private Catalogo estadoSalud;
    
    @OneToMany(mappedBy = "personaAfectada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DerechoVulnerado> derechosVulnerados;

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Violencia violencia;

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DetencionIntegridad detencionIntegridad;

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ExpresionCensura expresionCensura;

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AccesoJusticia accesoJusticia;
}
