package com.uca.idhuca.sistema.indicadores.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.dto.AuditoriaDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.PaginacionInfo;
import com.uca.idhuca.sistema.indicadores.dto.ResultadoPaginado;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Paginacion;
import com.uca.idhuca.sistema.indicadores.models.Auditoria;
import com.uca.idhuca.sistema.indicadores.repositories.AuditoriaRepository;
import com.uca.idhuca.sistema.indicadores.repositories.custom.AuditoriaRepositoryCustom;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuditoriaImpl implements IAuditoria {

	@Autowired
	private Utilidades utils;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private AuditoriaRepository auditoriaRepository;
	
	@Autowired AuditoriaRepositoryCustom auditoriaRepositoryCustom;

	@Autowired
	private ObjectMapper objectMapper; // Asegúrate de tenerlo como @Bean o usar new ObjectMapper() si prefieres.

	@Override
	public GenericEntityResponse<List<Auditoria>> get(Filtros filtros) throws NotFoundException, ValidationException {
	    String key = utils.obtenerUsuarioAutenticado().getEmail();

	    ResultadoPaginado<Auditoria> resultado = auditoriaRepositoryCustom.findByFiltros(filtros);
	    List<Auditoria> list = resultado.getResultados();
	    Long total = resultado.getTotalRegistros();

	    if (list == null || list.isEmpty()) {
	        log.info("[{}] No existe información en la auditoría con esos requisitos.", key);
	        throw new ValidationException(ERROR, "No existe información en la auditoría con esos requisitos.");
	    }

	    Paginacion pag = filtros.getPaginacion();
	    int paginaActual;
	    int registrosPorPagina;
	    int totalPaginas;

	    if (pag == null) {
	        paginaActual = 1;
	        registrosPorPagina = total.intValue();
	        totalPaginas = 1;
	    } else {
	        paginaActual = pag.getPaginaActual() > 0 ? pag.getPaginaActual() : 1;
	        registrosPorPagina = pag.getRegistrosPorPagina() > 0 ? pag.getRegistrosPorPagina() : 10;
	        totalPaginas = (int) Math.ceil((double) total / registrosPorPagina);
	    }


	    PaginacionInfo paginacionInfo = new PaginacionInfo();
	    paginacionInfo.setPaginaActual(paginaActual);
	    paginacionInfo.setTotalPaginas(totalPaginas);
	    paginacionInfo.setTotalRegistros(total);
	    paginacionInfo.setRegistrosPorPagina(registrosPorPagina);

	    GenericEntityResponse<List<Auditoria>> response =
	        new GenericEntityResponse<>(OK, "Auditoría obtenida correctamente.", list, paginacionInfo);

	    log.info("[{}] Auditoría obtenida correctamente.", key);
	    return response;
	}


	public <E> SuperGenericResponse add(AuditoriaDto<E> dto) throws ValidationException {
		Auditoria auditoria = new Auditoria();
		String key = utils.obtenerUsuarioAutenticado().getEmail();

		try {
			String tablaAfectada = dto.getEntity().getClass().getSimpleName().toLowerCase() + "s";
			
			if(tablaAfectada.equalsIgnoreCase("auditoriaregistroeventodtos")) {
				tablaAfectada = "Registro de eventos";
			}
			
			if(tablaAfectada.equalsIgnoreCase("PersonaAfectadaAuditoriaDTO")) {
				tablaAfectada = "Persona afectada";
			}
			
			if(tablaAfectada.equalsIgnoreCase("parametrosistemas")) {
				tablaAfectada = "Parámetros del sistema";
			}
			
			if(tablaAfectada.equalsIgnoreCase("notaderechos")) {
				tablaAfectada = "Ficha del derecho";
			}
			
			if(tablaAfectada.equalsIgnoreCase("catalogos")) {
				tablaAfectada = "Catálogos";
			}
			
			if(tablaAfectada.equalsIgnoreCase("usuarios")) {
				tablaAfectada = "Usuarios";
			}
			
			if(tablaAfectada.equalsIgnoreCase("notaderechoarchivos")) {
				tablaAfectada = "Archivo de una ficha del derecho";
			}
			
			auditoria.setTablaAfectada(tablaAfectada);
			auditoria.setRegistroId(dto.getRegistroModificado());
			auditoria.setUsuario(dto.getUsuario());
			auditoria.setOperacion(dto.getOperacion());

			String descripcion = "Se realizó la operación '" + dto.getOperacion() + "' sobre la tabla '" + tablaAfectada
					+ "' con los datos: " + objectMapper.writeValueAsString(dto.getEntity());
			
			auditoria.setDescripcion(descripcion);

			Auditoria auditoriaSaved = auditoriaRepository.save(auditoria);
			log.info("[{}] Auditoria creada correctamente: {}", key, mapper.writeValueAsString(auditoriaSaved));

			return new SuperGenericResponse(OK, "Auditoría registrada correctamente.");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ValidationException(ERROR, "Error al registrar auditoría: " + e.getMessage());
		}
	}

}
