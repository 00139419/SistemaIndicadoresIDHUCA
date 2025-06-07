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
import com.uca.idhuca.sistema.indicadores.models.Ubicacion;
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
	    return new GenericEntityResponse<>(OK, "Datos obtenidos correctamente", response);
	}


	private CampoSeleccionado detectarCampo(Filtros ejeX) throws Exception {

	    for (Field sf : Filtros.class.getDeclaredFields()) {
	        sf.setAccessible(true);
	        Object sub = sf.get(ejeX);
	        if (sub == null) continue;          // sub-filtro no usado

	        for (Field campo : sub.getClass().getDeclaredFields()) {
	            campo.setAccessible(true);
	            Object v = campo.get(sub);
	            if (v == null) continue;

	            boolean conValor = (v instanceof Collection<?> col)
	                                ? !col.isEmpty()
	                                : true;

	            if (conValor) {
	                List<String> codigos = null;

	                // Si el campo es una lista de Catálogos, guarda sus códigos
	                if (v instanceof List<?> lista && !lista.isEmpty()
	                    && lista.get(0) instanceof Catalogo cat) {
	                    codigos = lista.stream()
	                                   .map(c -> ((Catalogo) c).getCodigo())
	                                   .toList();
	                }
	                return new CampoSeleccionado(
	                          sf.getName(),          // eventoFiltro / …
	                          campo.getName(),       // municipios / …
	                          campo.getName(),       // etiqueta “bonita”
	                          codigos                // ← puede ser null
	                       );
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

	/* =========================================================
	 * MÉTODO PRINCIPAL  ───────────── mantiene sólo el “router”
	 * ========================================================= */
	private Map<String,Long> agruparPorCampo(CampoSeleccionado eje,
            List<RegistroEvento> registros) {

		Function<RegistroEvento,List<String>> extractor = switch (eje.getSubFiltro()) {
		case "eventoFiltro"    -> extractorEvento(eje.getNombreCampo(), eje.getCodigosPermitidos());
		case "afectadaFiltro"  -> extractorPersona(eje.getNombreCampo());
		case "violenciaFiltro" -> extractorViolencia(eje.getNombreCampo());
		// …otros sub-filtros
		default                -> re -> List.of("Sin dato");
		};
		
		return registros.stream()
	            .flatMap(re -> extractor.apply(re).stream())
	            .filter(label -> !"Sin dato".equals(label))          // <─ NUEVO
	            .collect(Collectors.groupingBy(Function.identity(),
	                                           Collectors.counting()));
}

	/* =========================================================
	 * 1. Helpers por sub-filtro
	 * ========================================================= */
	private Function<RegistroEvento,List<String>> extractorEvento(String campo,
        List<String> permitidos) {

		return switch (campo) {
				/* ----------  Departamentos  ---------- */
				case "departamentos" -> re -> {
				Ubicacion u = re.getUbicacion();
				return (u == null || u.getDepartamento() == null)
				? List.of("Sin dato")
				: List.of(u.getDepartamento().getDescripcion());
				};
				
				/* ----------  Municipios  ---------- */
				case "municipios" -> re -> {
				Ubicacion u = re.getUbicacion();
				if (u == null || u.getMunicipio() == null) return List.of("Sin dato");
				
				String cod  = u.getMunicipio().getCodigo();
				String desc = u.getMunicipio().getDescripcion();
				
				// ▸ Si hay lista de códigos y este municipio NO está allí → se ignora
				if (permitidos != null && !permitidos.contains(cod)) return List.of("Sin dato");
				
				return desc == null ? List.of("Sin dato") : List.of(desc);
				};
				
				/* ----------  Flag violencia  ---------- */
				case "flagViolencia" -> re -> List.of(booleanLabel(re.getFlagViolencia()));
				
				default -> re -> List.of("Sin dato");
			};
		}


	private Function<RegistroEvento, List<String>> extractorPersona(String campo) {

	    return switch (campo) {
	        case "generos" -> re -> re.getPersonasAfectadas() == null
	                ? List.of("Sin dato")
	                : re.getPersonasAfectadas().stream()
	                      .map(PersonaAfectada::getGenero)
	                      .filter(Objects::nonNull)
	                      .map(Catalogo::getDescripcion)
	                      .filter(Objects::nonNull)
	                      .toList();

	        // …otros campos: nacionalidades, tiposPersona, etc.
	        default -> re -> List.of("Sin dato");
	    };
	}

	private Function<RegistroEvento, List<String>> extractorViolencia(String campo) {

	    return switch (campo) {
	        case "esAsesinato" -> re -> re.getPersonasAfectadas() == null
	                ? List.of("Sin dato")
	                : re.getPersonasAfectadas().stream()
	                      .map(PersonaAfectada::getViolencia)
	                      .filter(Objects::nonNull)
	                      .map(Violencia::getEsAsesinato)
	                      .map(this::booleanLabel)
	                      .filter(Objects::nonNull)
	                      .toList();

	        case "tiposViolencia" -> re -> re.getPersonasAfectadas() == null
	                ? List.of("Sin dato")
	                : re.getPersonasAfectadas().stream()
	                      .map(PersonaAfectada::getViolencia)
	                      .filter(Objects::nonNull)
	                      .map(Violencia::getTipoViolencia)
	                      .filter(Objects::nonNull)
	                      .map(Catalogo::getDescripcion)
	                      .toList();

	        // …otros campos de Violencia
	        default -> re -> List.of("Sin dato");
	    };
	}

	/* =========================================================
	 * Helper para Boolean a “Sí/No/Sin dato”
	 * ========================================================= */
	private String booleanLabel(Boolean b) {
	    return b == null ? "Sin dato" : (b ? "Sí" : "No");
	}

}
