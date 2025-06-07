package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarCreateGraphics;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CreateGraphicsDto;
import com.uca.idhuca.sistema.indicadores.dto.BusquedaRegistroEventoResultado;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.graphics.CampoSeleccionado;
import com.uca.idhuca.sistema.indicadores.graphics.GraphicsGeneratorService;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsResponseDTO;
import com.uca.idhuca.sistema.indicadores.graphics.dto.SeriesDTO;
import com.uca.idhuca.sistema.indicadores.graphics.dto.StyleDTO;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Violencia;
import com.uca.idhuca.sistema.indicadores.repositories.custom.RegistroEventoRepositoryCustom;
import com.uca.idhuca.sistema.indicadores.services.IGraphics;
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

	@Override
	public GenericEntityResponse<List<RegistroEvento>> generate(CreateGraphicsDto request)
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
	        ejeX = detectarCampo(request.getCategoriaEjeX());
	        log.info("[{}] Campo eje X detectado: subFiltro={}, nombreCampo={}", key, ejeX.getSubFiltro(),
	                ejeX.getNombreCampo());
	    } catch (Exception e) {
	        log.info("[{}] Error al detectar campo eje X: {}", key, e.getMessage(), e);
	        throw e;
	    }

	    /* ---------- 2. Agrupar registros -------------- */
	    Map<String, Long> conteo = null;
	    try {
	        conteo = agruparPorCampo(ejeX, registros);
	        log.info("[{}] Agrupación completada, {} grupos encontrados", key, conteo.size());
	        log.info("[{}] Conteo agrupado: {}", key, conteo);
	    } catch (Exception e) {
	        log.info("[{}] Error al agrupar registros: {}", key, e.getMessage(), e);
	        throw e;
	    }

	    /* ---------- 3. Construir respuesta gráfica -------------- */
	    GraphicsRequest gr = buildGraphicsRequest(conteo, ejeX.getEtiquetaVisible());
	    log.info("[{}] GraphicsRequest armado correctamente", key);
	    
	    GraphicsResponseDTO  response =  generatorService.generate(gr);
	    log.info("[{}] Base64: {}", key, response.getBase64());
	    
	    // Respuesta temporal, luego cambiar el tipo de respuesta
	    return new GenericEntityResponse<>(OK, "Datos obtenidos correctamente", registros);
	}


	public CampoSeleccionado detectarCampo(Filtros catEjeX) throws Exception {
		for (Field sf : Filtros.class.getDeclaredFields()) {
			sf.setAccessible(true);
			Object sub = sf.get(catEjeX);
			if (sub == null)
				continue;

			for (Field campo : sub.getClass().getDeclaredFields()) {
				campo.setAccessible(true);
				Object valor = campo.get(sub);

				boolean tieneContenido = false;

				if (valor != null) {
					if (valor instanceof Collection) {
						tieneContenido = !((Collection<?>) valor).isEmpty();
					} else {
						tieneContenido = true;
					}
				}

				if (tieneContenido) {
					String pretty = campo.getName();
					return new CampoSeleccionado(sf.getName(), campo.getName(), pretty);
				}
			}
		}
		throw new IllegalStateException("No se pudo determinar eje X");
	}

	private GraphicsRequest buildGraphicsRequest(Map<String, Long> data, String axisLabel) {

		SeriesDTO serie = new SeriesDTO();
		serie.setName("Total");
		serie.setData(new LinkedHashMap<>(data)); // mantener orden natural

		StyleDTO style = new StyleDTO();
		style.setBackgroundColor("#ffffff");
		String[] paleta = { "#3366cc", "#dc3912", "#ff9900" };
		style.setPalette(paleta);
		style.setTitleFontSize(20);
		style.setSubtitleFontSize(13);

		GraphicsRequest gr = new GraphicsRequest();
		gr.setChartType(GraphicsRequest.ChartType.BAR); // por defecto
		gr.setTitle("Distribución por " + axisLabel);
		gr.setSubtitle("Derecho " + axisLabel);
		gr.setCategoryAxisLabel(axisLabel);
		gr.setValueAxisLabel("Cantidad");
		gr.setWidth(900);
		gr.setHeight(550);
		gr.setStyle(style);
		gr.setThreeD(true);
		gr.setSeries(List.of(serie));
		return gr;
	}

	private Map<String, Long> agruparPorCampo(CampoSeleccionado eje, List<RegistroEvento> registros) {

		Function<RegistroEvento, List<String>> extractor;

		String subFiltro = eje.getSubFiltro();
		String nombreCampo = eje.getNombreCampo();

		if ("eventoFiltro".equals(subFiltro)) {

			if ("departamentos".equals(nombreCampo)) {
				extractor = re -> {
					if (re.getUbicacion() == null)
						return List.of("Sin dato");
					var depto = re.getUbicacion().getDepartamento();
					if (depto == null || depto.getDescripcion() == null)
						return List.of("Sin dato");
					return List.of(depto.getDescripcion());
				};

			} else if ("municipios".equals(nombreCampo)) {
				extractor = re -> {
					if (re.getUbicacion() == null)
						return List.of("Sin dato");
					var municipio = re.getUbicacion().getMunicipio();
					if (municipio == null || municipio.getDescripcion() == null)
						return List.of("Sin dato");
					return List.of(municipio.getDescripcion());
				};

			} else if ("flagViolencia".equals(nombreCampo)) {
				extractor = re -> {
					String label = booleanLabel(re.getFlagViolencia());
					return label != null ? List.of(label) : List.of("Sin dato");
				};

			} else {
				extractor = re -> List.of("Sin dato");
			}

		} else if ("afectadaFiltro".equals(subFiltro)) {

			if ("generos".equals(nombreCampo)) {
				extractor = re -> {
					if (re.getPersonasAfectadas() == null || re.getPersonasAfectadas().isEmpty())
						return List.of("Sin dato");
					return re.getPersonasAfectadas().stream().map(PersonaAfectada::getGenero).filter(Objects::nonNull)
							.map(Catalogo::getDescripcion).filter(Objects::nonNull).toList();
				};

			} else {
				extractor = re -> List.of("Sin dato");
			}

		} else if ("violenciaFiltro".equals(subFiltro)) {

			if ("esAsesinato".equals(nombreCampo)) {
				extractor = re -> {
					if (re.getPersonasAfectadas() == null || re.getPersonasAfectadas().isEmpty())
						return List.of("Sin dato");
					return re.getPersonasAfectadas().stream().map(PersonaAfectada::getViolencia)
							.filter(Objects::nonNull).map(Violencia::getEsAsesinato).map(this::booleanLabel)
							.filter(Objects::nonNull).toList();
				};

			} else {
				extractor = re -> List.of("Sin dato");
			}

		} else {
			extractor = re -> List.of("Sin dato");
		}

		return registros.stream().flatMap(re -> extractor.apply(re).stream())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	private String booleanLabel(Boolean b) {
		return b == null ? "Sin dato" : (b ? "Sí" : "No");
	}
}
