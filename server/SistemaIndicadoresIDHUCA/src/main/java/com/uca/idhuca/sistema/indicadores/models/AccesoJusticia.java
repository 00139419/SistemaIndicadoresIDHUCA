package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "acceso_justicia")
@Data
public class AccesoJusticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "justicia_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    private PersonaAfectada persona;

    @ManyToOne
    @JoinColumn(name = "tipo_proceso_codigo", referencedColumnName = "codigo")
    private Catalogo tipoProceso;

    @Column(name = "fecha_denuncia")
    private java.sql.Date fechaDenuncia;

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
