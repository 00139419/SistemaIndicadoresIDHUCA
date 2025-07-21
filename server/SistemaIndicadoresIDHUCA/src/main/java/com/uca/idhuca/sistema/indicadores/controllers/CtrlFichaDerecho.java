package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.FichaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.NotaDerechoDTO;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.repositories.NotaDerechoRepository;
import com.uca.idhuca.sistema.indicadores.services.IFichaDerecho;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "fichaDerecho")
public class CtrlFichaDerecho {

	@Autowired
	private Utilidades utils;

	@Autowired
	private IFichaDerecho fichaDerechoService;


	@Autowired
	private NotaDerechoRepository notaRepository;
	
	@Autowired
	ObjectMapper mapper;

	@PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SuperGenericResponse> save(@RequestPart("nota") String notaJson,
			@RequestPart("archivos") MultipartFile[] archivos) {
		String key = "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			NotaDerechoRequest notaRequest = mapper.readValue(notaJson, NotaDerechoRequest.class);
			log.info("[" + key + "] ------ Inicio de servicio '/save' " + mapper.writeValueAsString(notaRequest));
			response = fichaDerechoService.save(notaRequest, archivos);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/save' ");
		}
	}
	
	@PostMapping(value = "/delete/post", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SuperGenericResponse> deletePost(@RequestBody NotaDerechoRequest request) {
		String key = "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/delete/post " + mapper.writeValueAsString(request));
			response = fichaDerechoService.deletePost(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/delete/post' ");
		}
	}
	
	@PutMapping(value = "/update/post/note", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SuperGenericResponse> updateNote(@RequestBody NotaDerechoRequest request) {
		String key = "SYSTEM";
		SuperGenericResponse response = new SuperGenericResponse();
		try {
			key = utils.obtenerUsuarioAutenticado().getEmail();
			log.info("[" + key + "] ------ Inicio de servicio '/update/note' " + mapper.writeValueAsString(request));
			response = fichaDerechoService.updateNotePost(request);
			return new ResponseEntity<SuperGenericResponse>(response, HttpStatus.OK);
		} catch (ValidationException e) {
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.info("stacktrace: ", e);
			e.printStackTrace();
			return new ResponseEntity<SuperGenericResponse>(new SuperGenericResponse(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[" + key + "] ------ Fin de servicio '/update/note' ");
		}
	}
	
	@PostMapping("/getAll/post")
	public ResponseEntity<GenericEntityResponse<List<NotaDerechoDTO>>> getAllPost(@RequestBody FichaDerechoRequest request) {
	    String key = "SYSTEM";
	    try {
	        key = utils.obtenerUsuarioAutenticado().getEmail();
	        log.info("[{}] Inicio de servicio '/getAll/post' con código: {}", key, request);
	        GenericEntityResponse<List<NotaDerechoDTO>> response = fichaDerechoService.getAllPost(request);
	        return new ResponseEntity<GenericEntityResponse<List<NotaDerechoDTO>>>(response, HttpStatus.OK);
	    } catch (ValidationException e) {
			return new ResponseEntity<GenericEntityResponse<List<NotaDerechoDTO>>>(new GenericEntityResponse<>(e.getCodigo(), e.getMensaje()), HttpStatus.BAD_REQUEST);
		}  catch (Exception e) {
			log.info("stacktrace: ", e);
	    	e.printStackTrace();
	        log.error("[{}] Error en servicio '/getAll/post': {}", key, e.getMessage(), e);
	        return new ResponseEntity<>(new GenericEntityResponse<List<NotaDerechoDTO>>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	    } finally {
	        log.info("[{}] Fin de servicio '/getAll/post'", key);
	    }
	}
	
	@PostMapping("/get/file/{nombre}")
	public ResponseEntity<Resource> getFile(@PathVariable("nombre") String nombreArchivo) {
	    String key = "SYSTEM";
	    try {
	        key = utils.obtenerUsuarioAutenticado().getEmail();
	        log.info("[{}] Inicio servicio '/get/file' para nombre: {}", key, nombreArchivo);

	        String nombreOriginal = notaRepository.findNombreOriginalByArchivoUrl(nombreArchivo);
	        
	        Resource archivo = fichaDerechoService.getFile(nombreArchivo);
	
	        String contentDisposition = "attachment; filename=\"" + nombreOriginal + "\"";
	        
	        log.info("contentDisposition " + contentDisposition);
	        
	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(archivo);

	    } catch (ValidationException e) {
	    	e.printStackTrace();
	        log.warn("[{}] Validación fallida en '/get/file': {}", key, e.getMessage());
	        return ResponseEntity.badRequest().build();
	    } catch (NotFoundException e) {
	    	e.printStackTrace();
	        log.warn("[{}] Archivo no encontrado en '/get/file': {}", key, e.getMessage());
	        return ResponseEntity.noContent().build();
	    } catch (Exception e) {
	    	log.info("stacktrace: ", e);
	    	e.printStackTrace();
	        log.error("[{}] Error en servicio '/archivo': {}", key, e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    } finally {
	        log.info("[{}] Fin servicio '/get/file'", key);
	    }
	}
}
