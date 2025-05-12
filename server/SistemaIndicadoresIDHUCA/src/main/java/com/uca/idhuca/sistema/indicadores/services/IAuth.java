package com.uca.idhuca.sistema.indicadores.services;

import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.Jwt;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;

public interface IAuth {
	
	/**
     * Realiza el proceso de autenticación de un usuario.
     * 
     * Verifica las credenciales proporcionadas en el objeto `LoginDto` y, si son válidas,
     * genera un token JWT de autenticación que se devuelve al cliente.
     * 
     * @param request Objeto con las credenciales del usuario (email y contraseña).
     * @return Una respuesta genérica que incluye el token JWT si la autenticación es exitosa.
     * @throws ValidationException Si las credenciales son inválidas o si hay errores de validación.
     */
	GenericEntityResponse<Jwt> login(LoginDto request) throws ValidationException;
}
