package com.uca.idhuca.sistema.indicadores.repositories.custom;

import com.uca.idhuca.sistema.indicadores.controllers.dto.FichaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.ResultadoPaginado;
import com.uca.idhuca.sistema.indicadores.models.NotaDerecho;

public interface NotaDerechoRepositoryCustom {
	ResultadoPaginado<NotaDerecho> findNotas(FichaDerechoRequest request);
}
