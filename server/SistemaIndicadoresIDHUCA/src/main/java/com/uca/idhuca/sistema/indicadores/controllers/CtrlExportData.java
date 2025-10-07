package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ROOT_CONTEXT;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.services.IExcel;
import com.uca.idhuca.sistema.indicadores.services.IRegistros;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ROOT_CONTEXT + "registros")
public class CtrlExportData {
	
	@Autowired
	private Utilidades utils;	

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	IRegistros registrosServices;
	
	@Autowired
	IExcel excelService;
	
	@PostMapping(value = "/exportData", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> exportData(@RequestBody CatalogoDto request, HttpServletResponse response) throws ValidationException {
	    List<RegistroEvento> datos = null;
	    String key = "ADMIN";

	    try {
	        key = utils.obtenerUsuarioAutenticado().getEmail();
	        log.info("[" + key + "] ------ Inicio de servicio '/exportData' ");

	        // Formatear la fecha para el nombre del archivo
	        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
	        String nombreArchivo = "reporte_" + fecha + ".xlsx";

	        // Configurar cabeceras para forzar descarga con nombre dinámico
	        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);

	        try {
	        	// Obtener datos a exportar
		        datos = registrosServices.getAllByDerecho(request).getEntity();
			} catch (ValidationException e) {
				datos = Arrays.asList();
			}

	        // Generar el Excel en streaming y escribirlo al response
	        excelService.generarExcel(datos, response.getOutputStream(), key);

	        return ResponseEntity.ok().build();
	    } catch (ValidationException e) {
	        return new ResponseEntity<GenericEntityResponse<List<RegistroEvento>>>(
	                new GenericEntityResponse<>(ERROR, e.getMensaje()), HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        log.info("stacktrace: ", e);
	        e.printStackTrace();
	        return new ResponseEntity<GenericEntityResponse<List<RegistroEvento>>>(
	                new GenericEntityResponse<>(ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	    } finally {
	        log.info("[" + key + "] ------ Fin de servicio '/exportData'");
	    }
	}

}
