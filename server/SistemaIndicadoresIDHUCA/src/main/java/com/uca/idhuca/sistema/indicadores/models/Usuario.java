package com.uca.idhuca.sistema.indicadores.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	private String email;

	private String contrasenaHash;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rol_codigo", referencedColumnName = "codigo")
	private Catalogo rol;

	private boolean activo;

	@Column(name = "creado_en")
	private Date creadoEn;
}
