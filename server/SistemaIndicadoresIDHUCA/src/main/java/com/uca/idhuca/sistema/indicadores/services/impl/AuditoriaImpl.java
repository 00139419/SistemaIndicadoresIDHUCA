package com.uca.idhuca.sistema.indicadores.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.dto.AuditoriaDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Auditoria;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoAuditoria;
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
	private IRepoAuditoria auditoriaRepo;

	@Autowired
	private ObjectMapper objectMapper; // Asegúrate de tenerlo como @Bean o usar new ObjectMapper() si prefieres.

	@Override
	public GenericEntityResponse<List<Auditoria>> get() throws NotFoundException, ValidationException {
		String key = utils.obtenerUsuarioAutenticado().getEmail();
		List<Auditoria> list = null;
		try {
			list = auditoriaRepo.findAll();
		} catch (Exception e) {
			log.info("[{}] No existe informacion en la audiotria con esos requisitos.", key);
			throw new NotFoundException(ERROR, "No existe informacion en la audiotria con esos requisitos.");
		}
		log.info("[{}] Auditoria obtenida correctamente.", key);

		return new GenericEntityResponse<List<Auditoria>>(OK, "Auditoria obtenida correctamente.", list);
	}

	public <E> SuperGenericResponse add(AuditoriaDto<E> dto) throws ValidationException {
		Auditoria auditoria = new Auditoria();
		String key = utils.obtenerUsuarioAutenticado().getEmail();

		try {
			String tablaAfectada = dto.getEntity().getClass().getSimpleName().toLowerCase() + "s";
			auditoria.setTablaAfectada(tablaAfectada);
			auditoria.setRegistroId(dto.getRegistroModificado());
			auditoria.setUsuario(dto.getUsuario());
			auditoria.setOperacion(dto.getOperacion());

			String descripcion = "Se realizó la operación '" + dto.getOperacion() + "' sobre la tabla '" + tablaAfectada
					+ "' con los datos: " + objectMapper.writeValueAsString(dto.getEntity());
			
			auditoria.setDescripcion(descripcion);
			log.info("[{}] Auditoria creada {}", key, mapper.writeValueAsString(auditoria));

			auditoriaRepo.save(auditoria);
			log.info("[{}] Auditoria guardada correctamente.", key);

			return new SuperGenericResponse(OK, "Auditoría registrada correctamente.");
			
		} catch (Exception e) {
			throw new ValidationException(ERROR, "Error al registrar auditoría: " + e.getMessage());
		}
	}

}
