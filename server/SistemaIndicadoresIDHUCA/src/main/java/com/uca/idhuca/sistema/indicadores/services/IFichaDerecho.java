package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.controllers.dto.FichaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.NotaDerechoDTO;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;

public interface IFichaDerecho {

	/**
	 * Guarda una nueva nota relacionada con un derecho humano, junto con sus archivos adjuntos.
	 *
	 * @param notaRequest Objeto que contiene la información de la nota a guardar.
	 * @param archivos Arreglo de archivos que se adjuntan a la nota.
	 * @return Una respuesta genérica que indica el resultado de la operación.
	 * @throws ValidationException Si los datos de entrada no son válidos.
	 * @throws Exception Si ocurre un error inesperado durante el guardado.
	 */
	SuperGenericResponse save(NotaDerechoRequest notaRequest, MultipartFile[] archivos) throws ValidationException, Exception;

	/**
	 * Recupera todas las notas asociadas a un derecho humano específico.
	 *
	 * @param codigoDerecho Código que identifica el derecho humano cuyas notas se desean consultar.
	 * @return Una respuesta genérica con la lista de notas correspondientes al derecho.
	 * @throws ValidationException Si el código del derecho es inválido o nulo.
	 */
	GenericEntityResponse<List<NotaDerechoDTO>> getAllPost(FichaDerechoRequest request) throws ValidationException;

	/**
	 * Recupera un archivo físico asociado a una nota, dado su nombre en el sistema.
	 *
	 * @param nombreArchivo Nombre con el que se almacenó el archivo (puede estar hasheado).
	 * @return El recurso del archivo solicitado para su descarga o visualización.
	 * @throws ValidationException Si el nombre del archivo es inválido.
	 * @throws NotFoundException Si el archivo no se encuentra en el sistema.
	 * @throws Exception Si ocurre un error inesperado al recuperar el archivo.
	 */
	Resource getFile(String nombreArchivo) throws ValidationException, NotFoundException, Exception;

	/**
	 * Actualiza el contenido de una nota ya existente, sin modificar sus archivos adjuntos.
	 *
	 * @param request Objeto que contiene la información actualizada de la nota.
	 * @return Una respuesta genérica indicando el resultado de la operación.
	 * @throws ValidationException Si los datos proporcionados no son válidos.
	 * @throws NotFoundException Si la nota que se desea actualizar no existe.
	 */
	SuperGenericResponse updateNotePost(NotaDerechoRequest request) throws ValidationException, NotFoundException;

	/**
	 * Elimina una nota existente relacionada con un derecho humano.
	 *
	 * @param request Objeto que identifica la nota a eliminar.
	 * @return Una respuesta genérica indicando si la eliminación fue exitosa.
	 * @throws ValidationException Si la solicitud no cumple con los criterios necesarios.
	 * @throws NotFoundException Si la nota que se desea eliminar no se encuentra en el sistema.
	 */
	SuperGenericResponse deletePost(NotaDerechoRequest request) throws ValidationException, NotFoundException;

}
