package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "registro_evento")
public class RegistroEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_registro")
    private Date fechaRegistro;

    @Column(name = "fecha_hecho")
    private LocalDate fechaHecho;

    @ManyToOne
    @JoinColumn(name = "fuente_codigo", referencedColumnName = "codigo")
    private Catalogo fuente;

    @ManyToOne
    @JoinColumn(name = "estado_actual_codigo", referencedColumnName = "codigo")
    private Catalogo estadoActual;

    @ManyToOne
    @JoinColumn(name = "derecho_asociado_codigo", referencedColumnName = "codigo")
    private Catalogo derechoAsociado;

    @Column(name = "flag_violencia")
    private Boolean flagViolencia;

    @Column(name = "flag_detencion")
    private Boolean flagDetencion;

    @Column(name = "flag_expresion")
    private Boolean flagExpresion;

    @Column(name = "flag_justicia")
    private Boolean flagJusticia;

    @Column(name = "flag_censura")
    private Boolean flagCensura;

    @Column(name = "flag_regimen_excepcion")
    private Boolean flagRegimenExcepcion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;
    
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ubicacion> ubicaciones;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaAfectada> personasAfectadas;


}
