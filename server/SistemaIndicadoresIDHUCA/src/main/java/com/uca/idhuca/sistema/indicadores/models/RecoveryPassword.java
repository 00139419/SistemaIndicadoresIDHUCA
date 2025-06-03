package com.uca.idhuca.sistema.indicadores.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
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
@Table(name = "recovery_password")
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