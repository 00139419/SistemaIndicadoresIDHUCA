package com.uca.idhuca.sistema.indicadores.useCase;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.uca.idhuca.sistema.indicadores.controllers.dto.AddUserDto;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoCatalogo;

@Component
public class UserUseCase {
	
	@Autowired
	PasswordEncoder pEncoder;
	
	@Autowired
	private IRepoCatalogo reporCatalogo;

	public Usuario darFormatoInsert(AddUserDto data) {
		Usuario newUser = new Usuario();
		
		newUser.setActivo(true);
		newUser.setCreadoEn(new Date(System.currentTimeMillis()));
		newUser.setEmail(data.getEmail());
		newUser.setContrasenaHash(pEncoder.encode(data.getPassword()));
		newUser.setNombre(data.getNombre());
		newUser.setRol(reporCatalogo.findByCodigo((data.getRol().getCodigo())));
		
		return newUser;
	}
	
	public Usuario darFormatoUpdate(AddUserDto data, Usuario usuarioActual) {
		usuarioActual.setContrasenaHash(pEncoder.encode(data.getPassword()));
		usuarioActual.setNombre(data.getNombre());
		usuarioActual.setRol(reporCatalogo.findByCodigo((data.getRol().getCodigo())));
		
		return usuarioActual;
	}
}
