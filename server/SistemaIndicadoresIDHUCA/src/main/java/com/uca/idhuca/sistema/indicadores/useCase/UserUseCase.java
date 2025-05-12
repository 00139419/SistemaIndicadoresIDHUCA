package com.uca.idhuca.sistema.indicadores.useCase;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.models.RecoveryPassword;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoCatalogo;

@Component
public class UserUseCase {
	
	@Autowired
	PasswordEncoder pEncoder;
	
	@Autowired
	private IRepoCatalogo reporCatalogo;

	public Usuario darFormatoInsert(UserDto data) {
		Usuario newUser = new Usuario();
		
		newUser.setActivo(true);
		newUser.setCreadoEn(new Date(System.currentTimeMillis()));
		newUser.setEmail(data.getEmail());
		newUser.setContrasenaHash(pEncoder.encode(data.getPassword()));
		newUser.setNombre(data.getNombre());
		newUser.setRol(reporCatalogo.findByCodigo((data.getRol().getCodigo())));
		
		return newUser;
	}
	
	public Usuario darFormatoUpdate(UserDto data, Usuario usuarioActual) {
		usuarioActual.setContrasenaHash(pEncoder.encode(data.getPassword()));
		usuarioActual.setNombre(data.getNombre());
		usuarioActual.setRol(reporCatalogo.findByCodigo((data.getRol().getCodigo())));
		
		return usuarioActual;
	}

	public RecoveryPassword darFormatoRecovery(UserDto request, Usuario usuario) {
		RecoveryPassword recovery = new RecoveryPassword();
		
		recovery.setUsuario(usuario);
		recovery.setIntentosFallidos(0);
		recovery.setPreguntaCodigo(request.getSecurityQuestion().getCodigo());
		recovery.setRespuestaHash(pEncoder.encode(request.getSecurityAnswer()));
		
		return recovery;
	}

	public void darFormatoUpdateRecovery(UserDto request, RecoveryPassword recovery) {
		recovery.setIntentosFallidos(request.isDebloquearUsuario() ? 0 : recovery.getIntentosFallidos());
		recovery.setPreguntaCodigo(request.getSecurityQuestion() != null ? request.getSecurityQuestion().getCodigo() : recovery.getPreguntaCodigo());
		recovery.setRespuestaHash(request.getSecurityAnswer() != null ? pEncoder.encode(request.getSecurityAnswer()) : recovery.getRespuestaHash());
	}
}
