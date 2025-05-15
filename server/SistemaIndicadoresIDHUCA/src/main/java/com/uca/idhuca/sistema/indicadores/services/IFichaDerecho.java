package com.uca.idhuca.sistema.indicadores.services;

import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;

public interface IFichaDerecho {

	SuperGenericResponse save(NotaDerechoRequest notaRequest, MultipartFile[] archivos) throws ValidationException;

}
