package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detencion_integridad")
@Data
public class DetencionIntegridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detencion_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_id")
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
