package com.uca.idhuca.sistema.indicadores.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "recovery_password")
@Data
public class RecoveryPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Código de la pregunta de seguridad (referencia a la tabla catálogo)
    @Column(name = "pregunta_codigo", nullable = false)
    private String preguntaCodigo;

    // Respuesta del usuario, almacenada como hash
    @Column(name = "respuesta_hash", nullable = false)
    private String respuestaHash;

    // Intentos fallidos al responder
    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos = 0;

    // Fecha de última actualización
    @Column(name = "actualizado_en", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime actualizadoEn = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}