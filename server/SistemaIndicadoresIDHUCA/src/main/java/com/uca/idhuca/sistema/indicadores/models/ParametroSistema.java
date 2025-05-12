package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "parametro_sistema")
@Data
public class ParametroSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Clave del parámetro (única)
    @Column(nullable = false, unique = true, length = 100)
    private String clave;

    // Valor del parámetro, almacenado como texto
    @Column(nullable = false, columnDefinition = "TEXT")
    private String valor;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Fecha de última actualización
    @Column(name = "actualizado_en", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime actualizadoEn = LocalDateTime.now();

    @PreUpdate
    @PrePersist
    public void actualizarFecha() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
