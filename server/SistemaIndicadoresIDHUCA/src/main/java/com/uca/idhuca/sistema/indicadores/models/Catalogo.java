package com.uca.idhuca.sistema.indicadores.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "catalogo")
public class Catalogo {

	@Id
	private String codigo;
	
	private String descripcion;

}