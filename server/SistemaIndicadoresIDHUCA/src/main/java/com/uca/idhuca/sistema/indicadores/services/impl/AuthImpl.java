package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarLogin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.Jwt;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.UsuarioRepository;
import com.uca.idhuca.sistema.indicadores.security.JwtUtils;
import com.uca.idhuca.sistema.indicadores.services.IAuth;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthImpl implements IAuth{
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	PasswordEncoder pEncoder;
	
	@Autowired
    private JwtUtils jwtUtils;

	String key = "SYSTEM";
	
	@Override
	public GenericEntityResponse<Jwt> login(LoginDto request) throws ValidationException {
	    List<String> errorsList = validarLogin(request);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    String key = request.getEmail();
	    log.info("[{}] Request válido", key);

	    Usuario usuario = usuarioRepository
	        .findByEmail(key)
	        .orElseThrow(() -> new ValidationException(ERROR, "Email no encontrado: " + key));
	    
	    log.info("[{}] Usuario encontrado correctamente: {}", key, usuario.toString());

	    matchPasswords(request.getPassword(), usuario.getContrasenaHash(), key);
	    log.info("[{}] Autenticación correcta.", key);

	    // Generar tokens
	    String accessToken = jwtUtils.generateJwtToken(key); 
	    String refreshToken = UUID.randomUUID().toString();
	    Instant refreshTokenExpiry = Instant.now().plus(7, ChronoUnit.DAYS);

	    // Guardar refresh token en BD
	    usuario.setRefreshToken(refreshToken);
	    usuario.setRefreshTokenExpiry(refreshTokenExpiry);
	    usuarioRepository.save(usuario);

	    log.info("[{}] Tokens generados correctamente", key);

	    Jwt jwtResponse = new Jwt(accessToken, refreshToken);
	    return new GenericEntityResponse<>(OK, "¡Éxito!", jwtResponse);
	}

	private void matchPasswords(String password, String hash, String key) throws ValidationException {
	    if (!pEncoder.matches(password, hash)) {
	        log.error("[{}] Password incorrecta.", key);
	        throw new ValidationException(ERROR, "Error: usuario o contraseña no coinciden.");
	    }
	}
}
