package com.uca.idhuca.sistema.indicadores.controllers.dto;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;

import lombok.Data;

@Data
public class UserDto {
	private Long id;
	private String nombre;
	private String email;
	private String provisionalPassword;
	private String password;
	private Catalogo rol;
	private Catalogo securityQuestion;
	private String securityAnswer;
	private String newPassword;
	private boolean debloquearUsuario;

	public UserDto(String nombre, String email) {
		this.nombre = nombre;
		this.email = email;
	}
}
