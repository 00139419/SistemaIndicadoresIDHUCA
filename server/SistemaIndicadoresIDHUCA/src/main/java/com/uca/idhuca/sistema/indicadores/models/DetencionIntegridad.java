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
@Table(name = "detencion_integridad")
public class DetencionIntegridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detencion_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    @JsonIgnore
    private PersonaAfectada persona;

    @ManyToOne
    @JoinColumn(name = "tipo_detencion_codigo", referencedColumnName = "codigo")
    private Catalogo tipoDetencion;

    @Column(name = "orden_judicial")
    private Boolean ordenJudicial;

    @ManyToOne
    @JoinColumn(name = "autoridad_involucrada_codigo", referencedColumnName = "codigo")
    private Catalogo autoridadInvolucrada;

    @Column(name = "hubo_tortura")
    private Boolean huboTortura;

    @Column(name = "duracion_dias")
    private Integer duracionDias;

    @Column(name = "acceso_abogado")
    private Boolean accesoAbogado;

    @Column(name = "resultado")
    private String resultado;

    @ManyToOne
    @JoinColumn(name = "motivo_detencion_codigo", referencedColumnName = "codigo")
    private Catalogo motivoDetencion;
}
