package com.uca.idhuca.sistema.indicadores.security;

import com.uca.idhuca.sistema.indicadores.utils.Constantes;

public class RutasPublicas {
	public static final String[] SIN_AUTENTICACION = {
	       Constantes.ROOT_CONTEXT +  "test/**",
	       Constantes.ROOT_CONTEXT +  "auth/login/**"
	    };
}
