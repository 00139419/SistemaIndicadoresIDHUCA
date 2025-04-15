package com.uca.idhuca.sistema.indicadores.services;

import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.Jwt;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;

public interface IAuth {
	GenericEntityResponse<Jwt> login(LoginDto request) throws ValidationException;
}
