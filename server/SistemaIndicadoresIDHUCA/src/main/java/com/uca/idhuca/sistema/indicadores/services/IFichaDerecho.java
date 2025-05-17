package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.NotaDerechoArchivoDTO;
import com.uca.idhuca.sistema.indicadores.dto.NotaDerechoDTO;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.NotaDerechoArchivo;

public interface IFichaDerecho {

	SuperGenericResponse save(NotaDerechoRequest notaRequest, MultipartFile[] archivos) throws ValidationException;
	
	GenericEntityResponse<List<NotaDerechoDTO>> getAllPost(String codigoDerecho) throws ValidationException;

	Resource getFile(String nombreArchivo) throws ValidationException, NotFoundException, Exception;
	
	SuperGenericResponse updateNotePost(NotaDerechoRequest request) throws ValidationException, NotFoundException;
	
	SuperGenericResponse deletePost(NotaDerechoRequest request) throws ValidationException, NotFoundException;
}
