package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class AddUserDto {
	private Long id;
	private String nombre;
	private String email;
	private String password;
	private Catalogo rol;
}
