package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "tabla_afectada", nullable = false)
    private String tablaAfectada;

    @Column(nullable = false)
    private String operacion;

    @Column(name = "registro_id")
    private String registroId;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha = new Date();
}
