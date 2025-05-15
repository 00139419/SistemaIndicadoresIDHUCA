package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "nota_derecho_archivo")
public class NotaDerechoArchivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_original")
    private String nombreOriginal;

    @Column(name = "archivo_url")
    private String archivoUrl;

    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_id")
    private NotaDerecho nota;

}
