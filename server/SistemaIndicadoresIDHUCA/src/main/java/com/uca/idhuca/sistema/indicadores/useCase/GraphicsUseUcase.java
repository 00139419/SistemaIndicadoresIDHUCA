package com.uca.idhuca.sistema.indicadores.useCase;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CreateGraphicsDto;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.graphics.CampoSeleccionado;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest;
import com.uca.idhuca.sistema.indicadores.graphics.dto.SeriesDTO;
import com.uca.idhuca.sistema.indicadores.graphics.dto.StyleDTO;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Violencia;
import com.uca.idhuca.sistema.indicadores.repositories.CatalogoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GraphicsUseUcase {

	@Autowired
	private CatalogoRepository catalogoRepository;

	public CampoSeleccionado detectarCampo(Filtros ejeX) throws Exception {

		for (Field sf : Filtros.class.getDeclaredFields()) {
			sf.setAccessible(true);
			Object sub = sf.get(ejeX);
			if (sub == null)
				continue; // sub-filtro no usado

			for (Field campo : sub.getClass().getDeclaredFields()) {
				campo.setAccessible(true);
				Object v = campo.get(sub);
				if (v == null)
					continue;

				boolean conValor = (v instanceof Collection<?> col) ? !col.isEmpty() : true;

				if (conValor) {
					List<String> codigos = null;

					// Si el campo es una lista de Catálogos, guarda sus códigos
					if (v instanceof List<?> lista && !lista.isEmpty() && lista.get(0) instanceof Catalogo) {
						codigos = lista
									.stream()
									.map(c -> ((Catalogo) c).getCodigo())
									.toList();
					}
					return new CampoSeleccionado(sf.getName(), // eventoFiltro / …
							campo.getName(), // municipios / …
							campo.getName(), // etiqueta “bonita”
							codigos // ← puede ser null
					);
				}
			}
		}
		throw new IllegalStateException("No se pudo determinar eje X");
	}

	public GraphicsRequest buildGraphicsRequest(Map<String, Long> data, String axisLabel, CreateGraphicsDto request) {

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
		gr.setChartType(request.getGraphicsSettings().getChartType()); // por defecto
		gr.setTitle("Grafico de personas vulnerabas por " + axisLabel);
		gr.setSubtitle("Subtitutlo de ejempo modificable");
		gr.setCategoryAxisLabel(axisLabel);
		gr.setValueAxisLabel("Cantidad");
		gr.setWidth(900);
		gr.setHeight(550);
		gr.setStyle(style);
		gr.setThreeD(true);
		gr.setSeries(List.of(serie));
		return gr;
	}

	/*
	 * ========================================================= MÉTODO PRINCIPAL
	 * ───────────── mantiene sólo el “router”
	 * =========================================================
	 */
	public Map<String, Long> agruparPorCampo(CampoSeleccionado eje, List<RegistroEvento> registros) {

		Function<RegistroEvento, List<String>> extractor = 
				switch (eje.getSubFiltro()) {
					case "eventoFiltro" -> extractorEvento(eje.getNombreCampo(), eje.getCodigosPermitidos());
					case "afectadaFiltro" -> extractorPersona(eje.getNombreCampo());
					case "violenciaFiltro" -> extractorViolencia(eje.getNombreCampo());
					// …otros sub-filtros
					default -> re -> List.of();
		};

		// 1. Conteo real por persona
		Map<String, Long> conteo = registros
										.stream()
										.flatMap(re -> extractor.apply(re).stream())
										.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		// 2. Añadir elementos “permitidos” sin datos (valor 0)
		List<String> permitidos = eje.getCodigosPermitidos();
		if (permitidos != null && !permitidos.isEmpty()) {
			for (String cod : permitidos) {
				String desc = catalogoRepository.findByCodigo(cod).getDescripcion();
				if (desc != null && !conteo.containsKey(desc)) {
					conteo.put(desc, 0L);
				}
			}
		}
		
		// Aquí usamos la nueva función auxiliar
		if (esFlagBooleano(eje.getSubFiltro(), eje.getNombreCampo())) {
			conteo.putIfAbsent("Sí", 0L);
			conteo.putIfAbsent("No", 0L);
		}

		return conteo;
	}

	/*
	 * ========================================================= 1. Helpers por
	 * sub-filtro =========================================================
	 */
	public Function<RegistroEvento, List<String>> extractorEvento(String campo, List<String> permitidos) {

	    Function<RegistroEvento, List<String>> baseExtractor = switch (campo) {

	        /* ---------- Departamentos ---------- */
	        case "departamentos" -> re -> {
	            String desc = null;
	            if (re.getUbicacion() != null && re.getUbicacion().getDepartamento() != null)
	                desc = re.getUbicacion().getDepartamento().getDescripcion();

	            int personas = re.getPersonasAfectadas() == null ? 0 : re.getPersonasAfectadas().size();
	            if (personas == 0 || desc == null)
	                return List.of();

	            if (permitidos != null && !permitidos.isEmpty()) {
	                String cod = re.getUbicacion().getDepartamento().getCodigo();
	                if (!permitidos.contains(cod))
	                    return List.of();
	            }

	            return java.util.Collections.nCopies(personas, desc);
	        };

	        /* ---------- Municipios ---------- */
	        case "municipios" -> re -> {
	            String desc = null;
	            if (re.getUbicacion() != null && re.getUbicacion().getMunicipio() != null)
	                desc = re.getUbicacion().getMunicipio().getDescripcion();

	            int personas = re.getPersonasAfectadas() == null ? 0 : re.getPersonasAfectadas().size();
	            if (personas == 0 || desc == null)
	                return List.of();

	            if (permitidos != null && !permitidos.isEmpty()) {
	                String cod = re.getUbicacion().getMunicipio().getCodigo();
	                if (!permitidos.contains(cod))
	                    return List.of();
	            }
	            return java.util.Collections.nCopies(personas, desc);
	        };

	        /* ---------- Flags flagRegimenExcepcion ---------- */
	        case "flagRegimenExcepcion" -> re -> {
	            int personas = re.getPersonasAfectadas() == null ? 0 : re.getPersonasAfectadas().size();
	            if (personas == 0)
	                return List.of();

	            Boolean flag = re.getFlagRegimenExcepcion(); // nunca null
	            String label = booleanLabel(flag);    // "Sí" o "No"
	            return java.util.Collections.nCopies(personas, label);
	        };

	        
	        case "fuentes" -> re -> {
	            String desc = null;
	            if (re != null && re.getFuente().getDescripcion() != null)
	                desc = re.getFuente().getDescripcion();

	            int personas = re.getPersonasAfectadas() == null ? 0 : re.getPersonasAfectadas().size();
	            if (personas == 0 || desc == null)
	                return List.of();

	            if (permitidos != null && !permitidos.isEmpty()) {
	                String cod = re.getFuente().getCodigo();
	                if (!permitidos.contains(cod))
	                    return List.of();
	            }
	            return java.util.Collections.nCopies(personas, desc);
	        };
	        
	        case "estadosActuales" -> re -> {
	            String desc = null;
	            if (re != null && re.getEstadoActual().getDescripcion() != null)
	                desc = re.getEstadoActual().getDescripcion();

	            int personas = re.getPersonasAfectadas() == null ? 0 : re.getPersonasAfectadas().size();
	            if (personas == 0 || desc == null)
	                return List.of();

	            if (permitidos != null && !permitidos.isEmpty()) {
	                String cod = re.getEstadoActual().getCodigo();
	                if (!permitidos.contains(cod))
	                    return List.of();
	            }
	            return java.util.Collections.nCopies(personas, desc);
	        };
	        
	        case "lugaresExactos" -> re -> {
	            String desc = null;
	            if (re != null && re.getUbicacion().getLugarExacto().getDescripcion() != null)
	                desc = re.getUbicacion().getLugarExacto().getDescripcion();

	            int personas = re.getPersonasAfectadas() == null ? 0 : re.getPersonasAfectadas().size();
	            if (personas == 0 || desc == null)
	                return List.of();

	            if (permitidos != null && !permitidos.isEmpty()) {
	                String cod = re.getUbicacion().getLugarExacto().getCodigo();
	                if (!permitidos.contains(cod))
	                    return List.of();
	            }
	            return java.util.Collections.nCopies(personas, desc);
	        };
	        
	        // Fin
	        default -> re -> List.of();
	    };

	    // Envolvemos con logging
	    return re -> {
	        List<String> result = baseExtractor.apply(re);
	        log.info("[ExtractorEvento] Campo: " + campo + " -> Resultado: " + result);
	        return result;
	    };
	}


	public Function<RegistroEvento, List<String>> extractorPersona(String campo) {

		return switch (campo) {
		case "generos" -> re -> re.getPersonasAfectadas() == null ? List.of("Sin dato")
				: re.getPersonasAfectadas().stream().map(PersonaAfectada::getGenero).filter(Objects::nonNull)
						.map(Catalogo::getDescripcion).filter(Objects::nonNull).toList();

		// …otros campos: nacionalidades, tiposPersona, etc.
		default -> re -> List.of("Sin dato");
		};
	}

	public Function<RegistroEvento, List<String>> extractorViolencia(String campo) {

		return switch (campo) {
		case "esAsesinato" -> re -> re.getPersonasAfectadas() == null ? List.of("Sin dato")
				: re.getPersonasAfectadas().stream().map(PersonaAfectada::getViolencia).filter(Objects::nonNull)
						.map(Violencia::getEsAsesinato).map(this::booleanLabel).filter(Objects::nonNull).toList();

		case "tiposViolencia" -> re -> re.getPersonasAfectadas() == null ? List.of("Sin dato")
				: re.getPersonasAfectadas().stream().map(PersonaAfectada::getViolencia).filter(Objects::nonNull)
						.map(Violencia::getTipoViolencia).filter(Objects::nonNull).map(Catalogo::getDescripcion)
						.toList();

		// …otros campos de Violencia
		default -> re -> List.of("Sin dato");
		};
	}

	/*
	 * ========================================================= Helper para Boolean
	 * a “Sí/No/Sin dato” =========================================================
	 */
	public String booleanLabel(Boolean b) {
		return b == null ? "Sin dato" : (b ? "Sí" : "No");
	}
	
	public boolean esFlagBooleano(String subFiltro, String nombreCampo) {
		// Puedes ir agregando más campos booleanos aquí
		return switch (subFiltro) {
			case "eventoFiltro" -> List.of("flagViolencia", "flagDiscapacidad", "flagEmergencia").contains(nombreCampo);
			case "afectadaFiltro" -> List.of("flagLGBT", "flagIndigena").contains(nombreCampo);
			// Agrega más si necesitas
			default -> false;
		};
	}

}
