package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarAddCatalogo;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarCatalogoRequest;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdateCatalogo;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDeleteCatalogo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.repositories.CatalogoRepository;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.ICatalogo;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_TIPO_PROCESO_JUDICIAL;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_TIPO_DENUNCIANTE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_DURACION_PROCESO;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_TIPO_DE_REPRESION;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_MEDIO_DE_EXPRESION;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_MOTIVO_DETENCION;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_TIPO_DE_ARMA;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_TIPO_DE_DETENCION;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_TIPO_DE_VIOLENCIA;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_ESTADO_SALUD;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_TIPO_PERSONA;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_GENERO;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_LUGAR_EXACTO;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_ESTADO_DEL_REGISTROS;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_FUENTE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_PAISES;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_ROL;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_SECURITY_QUESTION;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_DEPARTAMENTO;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_MUNICIPIO;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_SUB_DERECHO;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CATALOGO_DERECHO;



import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;

@Slf4j
@Service
public class CatologoImpl implements ICatalogo {

	@Autowired
	private Utilidades utils;

	@Autowired
	CatalogoRepository catalogoRepository;
	
	@Autowired
	IAuditoria auditoriaService;

	@Override
	public GenericEntityResponse<List<Catalogo>> get(CatalogoDto request)
			throws ValidationException, NotFoundException {
		List<String> errorsList = validarCatalogoRequest(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		String nombreCampoActivo = null;
		for (Field field : request.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object value = field.get(request);
				if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
					if (nombreCampoActivo != null) {
						throw new IllegalArgumentException("Solo se puede activar un catálogo a la vez.");
					}
					nombreCampoActivo = field.getName();
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (nombreCampoActivo == null) {
			throw new IllegalArgumentException("Debe activar al menos un catálogo.");
		}

		List<Catalogo> list = null;
		
		switch (nombreCampoActivo) {
		case "tipoProcesoJudicial":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_TIPO_PROCESO_JUDICIAL);
			break;
		case "tipoDenunciante":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_TIPO_DENUNCIANTE);
			break;
		case "duracionProceso":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_DURACION_PROCESO);
			break;
		case "medioExpresion":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_MEDIO_DE_EXPRESION);
			break;
		case "tipoRepresion":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_TIPO_DE_REPRESION);
			break;
		case "motivoDetencion":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_MOTIVO_DETENCION);
			break;
		case "tipoArma":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_TIPO_DE_ARMA);
			break;
		case "tipoDetencion":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_TIPO_DE_DETENCION);
			break;
		case "tipoViolencia":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_TIPO_DE_VIOLENCIA);
			break;
		case "estadoSalud":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_ESTADO_SALUD);
			break;
		case "tipoPersona":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_TIPO_PERSONA);
			break;
		case "genero":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_GENERO);
			break;
		case "lugarExacto":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_LUGAR_EXACTO);
			break;
		case "estadoRegistro":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_ESTADO_DEL_REGISTROS);
			break;
		case "paises":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_PAISES);
			break;
		case "fuentes":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_FUENTE);
			break;
		case "roles":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_ROL);
			break;
		case "derechos":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_DERECHO);
			break;
		case "subDerechos":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_SUB_DERECHO + request.getParentId().replace(CATALOGO_DERECHO, "") + "_");
			break;
		case "departamentos":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_DEPARTAMENTO);
			break;
		case "municipios":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_MUNICIPIO + request.getParentId().replace(CATALOGO_DEPARTAMENTO, "") + "_");
			break;
		case "securityQuestions":
			list = catalogoRepository.obtenerCatalogo(CATALOGO_SECURITY_QUESTION);
			break;
		default:
			throw new NotFoundException(ERROR, "Catálogo no reconocido: " + nombreCampoActivo);
		}

		return new GenericEntityResponse<List<Catalogo>>(OK, "Catalogo obtenido correctamente.", list);
	}

	@Override
	public SuperGenericResponse add(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarAddCatalogo(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		String nombreCampoActivo = null;
		for (Field field : request.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object value = field.get(request);
				if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
					if (nombreCampoActivo != null) {
						throw new IllegalArgumentException("Solo se puede activar un catálogo a la vez.");
					}
					nombreCampoActivo = field.getName();
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (nombreCampoActivo == null) {
			throw new IllegalArgumentException("Debe activar al menos un catálogo.");
		}
		
		
		Catalogo nuevoCatalogo = new Catalogo();
		nuevoCatalogo.setDescripcion(request.getNuevoCatalogo());
		int indice = 0;
		
		switch (nombreCampoActivo) {
		case "tipoProcesoJudicial":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_TIPO_PROCESO_JUDICIAL, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_TIPO_PROCESO_JUDICIAL + indice);
			break;
		case "tipoDenunciante":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_TIPO_DENUNCIANTE, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_TIPO_DENUNCIANTE + indice);
			break;
		case "duracionProceso":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_DURACION_PROCESO, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_DURACION_PROCESO + indice);
			break;
		case "tipoRepresion":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_TIPO_DE_REPRESION, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_TIPO_DE_REPRESION + indice);
			break;
		case "medioExpresion":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_MEDIO_DE_EXPRESION, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_MEDIO_DE_EXPRESION + indice);
			break;
		case "motivoDetencion":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_MOTIVO_DETENCION, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_MOTIVO_DETENCION + indice);
			break;
		case "tipoArma":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_TIPO_DE_ARMA, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_TIPO_DE_ARMA + indice);
			break;
		case "tipoDetencion":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_TIPO_DE_DETENCION, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_TIPO_DE_DETENCION + indice);
			break;
		case "tipoViolencia":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_TIPO_DE_VIOLENCIA, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_TIPO_DE_VIOLENCIA + indice);
			break;
		case "estadoSalud":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_ESTADO_SALUD, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_ESTADO_SALUD + indice);
			break;
		case "tipoPersona":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_TIPO_PERSONA, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_TIPO_PERSONA + indice);
			break;
		case "genero":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_GENERO, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_GENERO + indice);
			break;
		case "lugarExacto":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_LUGAR_EXACTO, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_LUGAR_EXACTO + indice);
			break;
		case "estadoRegistro":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_ESTADO_DEL_REGISTROS, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_ESTADO_DEL_REGISTROS + indice);
			break;
		case "fuentes":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_FUENTE, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_FUENTE + indice);
			break;
		case "paises":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_PAISES, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_PAISES + indice);
			break;
		case "roles":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_ROL, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_ROL + indice);
			break;
		case "departamentos":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_DEPARTAMENTO, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_DEPARTAMENTO + indice);
			break;
		case "subDerechos":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_SUB_DERECHO, request.getParentId().replace(CATALOGO_DERECHO, "")) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_SUB_DERECHO + request.getParentId().replace(CATALOGO_DERECHO, "") + "_" + indice);
			break;
		case "derechos":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_DERECHO, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_DERECHO + indice);
			break;
		case "municipios":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_MUNICIPIO, request.getParentId().replace(CATALOGO_DEPARTAMENTO, "")) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_MUNICIPIO + request.getParentId().replace(CATALOGO_DEPARTAMENTO, "") + "_" + indice);
			break;
		case "securityQuestions":
			indice = utils.obtenerUltimoIndiceCatalogo(CATALOGO_SECURITY_QUESTION, null) + 1;
			nuevoCatalogo.setCodigo(CATALOGO_SECURITY_QUESTION + indice);
			break;
		default:
			throw new NotFoundException(ERROR, "Catálogo no reconocido: " + nombreCampoActivo);
		}
		
		Catalogo catalogoSaved = catalogoRepository.save(nuevoCatalogo);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), CREAR, catalogoSaved));
		log.info("[{}] Catalogo creada correctamente.",key);
		
		return new SuperGenericResponse(OK, "Catalogo agregado correctamente.");
	}

	@Override
	public SuperGenericResponse update(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarUpdateCatalogo(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		Catalogo catalogo = null;
		try {
			catalogo = catalogoRepository
					 .findByCodigo(request.getCatalogo().getCodigo());
		} catch (NoSuchElementException e) {
			System.out.println("Catalogo no existe.");
			throw new NotFoundException(ERROR, "Catalogo no existe.");
		}
		 log.info("[{}] Catalogo válido", key);
		 
		 catalogo.setDescripcion(request.getCatalogo().getDescripcion());
		 catalogoRepository.save(catalogo);
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, catalogo));
		 log.info("[{}] Catalogo actualizado correctamente.",key);
		 
		 return new SuperGenericResponse(OK, "Catalogo actualizado correctamente.");
	}

	@Override
	public SuperGenericResponse delete(CatalogoDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarDeleteCatalogo(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		Catalogo catalogo = null;
		try {
			catalogo = catalogoRepository
					 .findByCodigo(request.getCatalogo().getCodigo());
		} catch (NoSuchElementException e) {
			System.out.println("Catalogo no existe.");
			throw new NotFoundException(ERROR, "Catalogo no existe.");
		}
		 log.info("[{}] Catalogo válido", key);
		 
		 catalogoRepository.delete(catalogo);
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), DELETE, catalogo));
		 log.info("[{}] Catalogo eliminado correctamente.",key);
		 
		 return new SuperGenericResponse(OK, "Catalogo eliminado correctamente.");
	}

}
