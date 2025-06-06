package com.uca.idhuca.sistema.indicadores.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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

@Table(name = "acceso_justicia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccesoJusticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "justicia_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    @JsonIgnore
    private PersonaAfectada persona;

    @ManyToOne
    @JoinColumn(name = "tipo_proceso_codigo", referencedColumnName = "codigo")
    private Catalogo tipoProceso;

    @Column(name = "fecha_denuncia")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/El_Salvador")
    private Date fechaDenuncia;

    @ManyToOne
    @JoinColumn(name = "tipo_denunciante_codigo", referencedColumnName = "codigo")
    private Catalogo tipoDenunciante;

    @ManyToOne
    @JoinColumn(name = "duracion_proceso_codigo", referencedColumnName = "codigo")
    private Catalogo duracionProceso;

    @Column(name = "acceso_abogado")
    private Boolean accesoAbogado;

    @Column(name = "hubo_parcialidad")
    private Boolean huboParcialidad;

    @Column(name = "resultado_proceso")
    private String resultadoProceso;

    @Column(name = "instancia")
    private String instancia;
}
