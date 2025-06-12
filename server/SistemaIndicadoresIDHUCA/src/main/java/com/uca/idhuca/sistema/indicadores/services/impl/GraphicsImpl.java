package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarCreateGraphics;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CreateGraphicsDto;
import com.uca.idhuca.sistema.indicadores.dto.BusquedaRegistroEventoResultado;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.graphics.CampoSeleccionado;
import com.uca.idhuca.sistema.indicadores.graphics.GraphicsGeneratorService;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsResponseDTO;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.repositories.custom.RegistroEventoRepositoryCustom;
import com.uca.idhuca.sistema.indicadores.services.IGraphics;
import com.uca.idhuca.sistema.indicadores.useCase.GraphicsUseUcase;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GraphicsImpl implements IGraphics {

	@Autowired
	private Utilidades utils;

	@Autowired
	private RegistroEventoRepositoryCustom registroEventoRepositoryCustom;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private GraphicsGeneratorService generatorService;

	@Autowired
	GraphicsUseUcase graphicsUseUcase;

	@Override
	public GenericEntityResponse<GraphicsResponseDTO> generate(CreateGraphicsDto request)
			throws ValidationException, NotFoundException, Exception {
		log.info("Inicia método generate para usuario {}", utils.obtenerUsuarioAutenticado().getEmail());

		List<String> errorsList = validarCreateGraphics(request);
		if (!errorsList.isEmpty()) {
			log.info("Validación fallida: {}", errorsList.get(0));
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();

		Catalogo derechoAsociado = null;
		try {
			derechoAsociado = utils.obtenerCatalogoPorCodigo(request.getDerecho().getCodigo(), key);
			request.setDerecho(derechoAsociado);
			log.info("[{}] Derecho asociado obtenido: {}", key, derechoAsociado.getDescripcion());
		} catch (Exception e) {
			log.info("[{}] Error al obtener derecho asociado: {}", key, e.getMessage(), e);
			throw e;
		}

		log.info("[{}] Request válido, buscando eventos...", key);

		BusquedaRegistroEventoResultado resultado = registroEventoRepositoryCustom
				.buscarEventos(new CatalogoDto(request.getDerecho(), request.getFiltros()));

		List<RegistroEvento> registros = resultado.getResultados();
		if (registros == null || registros.isEmpty()) {
			log.info("[{}] No hay más registros que mostrar", key);
			throw new ValidationException(ERROR, "No hay más registros que mostrar");
		}
		log.info("[{}] Registros obtenidos: {}", key, registros.size());
		
		/* ---------- 1. Detectar campo eje X ------------ */
		CampoSeleccionado ejeX = null;
		try {
			ejeX = graphicsUseUcase.detectarCampo(request.getCategoriaEjeX());
			log.info("[{}] Campo eje X detectado: subFiltro={}, nombreCampo={}", key, ejeX.getSubFiltro(),
					ejeX.getNombreCampo());
		} catch (Exception e) {
			log.info("[{}] Error al detectar campo eje X: {}", key, e.getMessage(), e);
			throw e;
		}

		/* ---------- 2. Agrupar registros -------------- */
		Map<String, Long> conteo = null;
		try {
			conteo = graphicsUseUcase.agruparPorCampo(ejeX, registros);
			log.info("[{}] Agrupación completada, {} grupos encontrados", key, conteo.size());
			log.info("[{}] Conteo agrupado: {}", key, conteo);
		} catch (Exception e) {
			log.info("[{}] Error al agrupar registros: {}", key, e.getMessage(), e);
			throw e;
		}

		/* ---------- 3. Construir respuesta gráfica -------------- */
		GraphicsRequest gr = graphicsUseUcase.buildGraphicsRequest(conteo, ejeX.getEtiquetaVisible(), request);
		log.info("[{}] GraphicsRequest armado correctamente", key);

		GraphicsResponseDTO response = generatorService.generate(gr);
		log.info("[{}] Base64: {}", key, response.getBase64());

		// Respuesta temporal, luego cambiar el tipo de respuesta
		return new GenericEntityResponse<>(OK, "Datos obtenidos correctamente", response);
	}

}
