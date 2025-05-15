package com.uca.idhuca.sistema.indicadores.security;

import com.uca.idhuca.sistema.indicadores.utils.Constantes;

public class RutasAdministradores {
	public static final String[] RUTAS_ADMINISTRADORES = {
	       Constantes.ROOT_CONTEXT +  "user/**",
	       Constantes.ROOT_CONTEXT +  "auditoria/**",
	       Constantes.ROOT_CONTEXT +  "fichaDerecho/**"
	    };
}
