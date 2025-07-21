package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdateParametrosSistema;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.controllers.dto.ParametrosSistemaDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.ParametroSistema;
import com.uca.idhuca.sistema.indicadores.repositories.ParametrosSistemaRepository;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.IParametrosSistema;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ParametrosSistemaImpl implements IParametrosSistema {

	@Autowired
	private ParametrosSistemaRepository parametrosSistemaRepository;

	@Autowired
	private IAuditoria auditoriaService;

	@Autowired
	private Utilidades utils;

	@Override
	public GenericEntityResponse<List<ParametroSistema>> getAll() throws ValidationException {
		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		List<ParametroSistema> ls = parametrosSistemaRepository.findAll();
		log.info("[{}] parametos del sistema obtenidos correctamente.", key);

		return new GenericEntityResponse<>(OK, "parametos del sistema obtenidos correctamente.", ls);
	}


	@Override
	public SuperGenericResponse update(ParametrosSistemaDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarUpdateParametrosSistema(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();

		ParametroSistema parametro = null;
		try {
			parametro = parametrosSistemaRepository
					.findByClave(request.getClave());

			if(parametro == null) {
				throw new ValidationException(ERROR, "Parametro con clave " + request.getClave() + " no existe");
			}
		} catch (ValidationException e) {
			throw e;
		}

		String clave = request.getClave();

		if (clave.equalsIgnoreCase("max_tiempo_inactividad") ||
				clave.equalsIgnoreCase("max_intentos_pregunta_seguridad") ||
				clave.equalsIgnoreCase("tiempo_de_vida_de_sesion")) {
			try {
				Double.parseDouble(request.getValor());
			} catch (NumberFormatException e) {
				throw new ValidationException(ERROR, "El valor debe ser un número.");
			}
		}


		log.info("[{}] Request válido", key);

		parametro.setValor(request.getValor());
		parametro.actualizarFecha();

		log.info("[{}] Actualizando parametro del sistema...", key);

		parametrosSistemaRepository.save(parametro);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, parametro));

		log.info("[{}] Parametro actualizado correctamente.", key);
		return new SuperGenericResponse(OK, "Parametro actualizado correctamente.");
	}

	@Override
	public GenericEntityResponse<ParametroSistema> getOne(String clave) throws ValidationException {
		log.info("[{}] Request válido", "SYSTEM");
		ParametroSistema p = parametrosSistemaRepository.findByClave(clave);

		if (p == null) {
			throw new ValidationException(ERROR, "Parámetro con clave " + clave + " no encontrado");
		}

		log.info("[{}] Parámetro del sistema obtenido correctamente.", "SYSTEM");
		return new GenericEntityResponse<>(OK, "Parámetro del sistema obtenido correctamente.", p);
	}



}