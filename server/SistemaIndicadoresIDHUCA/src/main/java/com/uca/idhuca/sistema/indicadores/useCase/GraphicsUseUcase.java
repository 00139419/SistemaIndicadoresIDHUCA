package com.uca.idhuca.sistema.indicadores.useCase;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
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
import com.uca.idhuca.sistema.indicadores.models.AccesoJusticia;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.DerechoVulnerado;
import com.uca.idhuca.sistema.indicadores.models.DetencionIntegridad;
import com.uca.idhuca.sistema.indicadores.models.ExpresionCensura;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Violencia;
import com.uca.idhuca.sistema.indicadores.repositories.CatalogoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GraphicsUseUcase {
	
	HashMap<String , String> CAMPOS_NUMERICOS = new HashMap<>();
	
	{
		CAMPOS_NUMERICOS.put("edades", "edades");
		CAMPOS_NUMERICOS.put("diasExactos", "diasExactos");
	}
	
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
					} else if (v instanceof List<?> lista && !lista.isEmpty() && lista.get(0) instanceof Integer) {
						codigos = lista
									.stream()
									.map(c -> ((Integer) c).toString())
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

		log.info("Campos permitidos " + eje.getCodigosPermitidos());
		
		Function<RegistroEvento, List<String>> extractor = 
				switch (eje.getSubFiltro()) {
					case "eventoFiltro" -> extractorEvento(eje.getNombreCampo(), eje.getCodigosPermitidos());
					case "afectadaFiltro" -> extractorPersona(eje.getNombreCampo(), eje.getCodigosPermitidos());
					case "derechosVulneradosFiltro" -> extractorDerechosVulnerados(eje.getNombreCampo(), eje.getCodigosPermitidos());
					case "violenciaFiltro" -> extractorViolencia(eje.getNombreCampo(), eje.getCodigosPermitidos()); 
					case "detencionFiltro" -> extractorDetencion(eje.getNombreCampo(), eje.getCodigosPermitidos());
					case "censuraFiltro"  -> extractorCensura(eje.getNombreCampo(), eje.getCodigosPermitidos());
					case "accesoJusticiaFiltro" -> extractorJusticia(eje.getNombreCampo(), eje.getCodigosPermitidos());
 					default -> re -> List.of();
		};

		// 1. Conteo real por persona
		Map<String, Long> conteo = registros
										.stream()
										.flatMap(re -> extractor.apply(re).stream())
										.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		log.info("Conteo actual sin filltrar " + conteo.toString());

		// 2. Añadir elementos “permitidos” sin datos (valor 0)
		List<String> permitidos = eje.getCodigosPermitidos();
		if (permitidos != null && !permitidos.isEmpty()) {
			for (String cod : permitidos) {
				
				if(CAMPOS_NUMERICOS.containsKey(eje.getNombreCampo())) {
					String desc = cod;
					if (desc != null && !conteo.containsKey(desc)) {
						conteo.put(desc, 0L);
					}
				} else {
					String desc = catalogoRepository.findByCodigo(cod).getDescripcion();
					if (desc != null && !conteo.containsKey(desc)) {
						conteo.put(desc, 0L);
					}
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


	/* =========================================================
	 *  Personas afectadas  (género, nacionalidad, …)
	 * ========================================================= */
	private Function<RegistroEvento, List<String>> extractorPersona(String campo,
	                                                                List<String> permitidos) {

	    return switch (campo) {
	        /* ----------  Géneros  ---------- */
	        case "generos" -> re -> {
	            if (re.getPersonasAfectadas()==null || re.getPersonasAfectadas().isEmpty())
	                return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getGenero)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))   // ⬅️ filtrar
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Nacionalidades  ---------- */
	        case "nacionalidades" -> re -> {
	            if (re.getPersonasAfectadas()==null || re.getPersonasAfectadas().isEmpty())
	                return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getNacionalidad)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))   // ⬅️ filtrar
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };
	        
	        /* ----------  departamentosResidencia  ---------- */
	        case "departamentosResidencia" -> re -> {
	            if (re.getPersonasAfectadas()==null || re.getPersonasAfectadas().isEmpty())
	                return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDepartamentoResidencia)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))   // ⬅️ filtrar
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };
	        
	        /* ----------  departamentosResidencia  ---------- */
	        case "municipiosResidencia" -> re -> {
	            if (re.getPersonasAfectadas()==null || re.getPersonasAfectadas().isEmpty())
	                return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getMunicipioResidencia)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))   // ⬅️ filtrar
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };
	        
	        /* ----------  departamentosResidencia  ---------- */
	        case "tiposPersona" -> re -> {
	            if (re.getPersonasAfectadas()==null || re.getPersonasAfectadas().isEmpty())
	                return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getTipoPersona)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))   // ⬅️ filtrar
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };
	        
	        /* ----------  departamentosResidencia  ---------- */
	        case "estadosSalud" -> re -> {
	            if (re.getPersonasAfectadas()==null || re.getPersonasAfectadas().isEmpty())
	                return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getEstadoSalud)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))   // ⬅️ filtrar
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };
	        
	        case "edades" -> re -> {
	            if (re.getPersonasAfectadas() == null || re.getPersonasAfectadas().isEmpty())
	                return List.of();

	            // lista de edades permitidas (puede estar vacía)
	            List<String> permitidosSet = permitidos == null ? List.of() : permitidos;

	            return re.getPersonasAfectadas().stream()
	                    .map(PersonaAfectada::getEdad)          // Integer
	                    .filter(Objects::nonNull)
	                    .map(String::valueOf)                   // "30"
	                    .filter(ed -> permitidosSet.isEmpty()   // ⬅️  SOLO filtra si hay permitidos
	                                  || permitidosSet.contains(ed))
	                    .toList();
	        };



	        default -> re -> List.of();
	    };
	}

	private Function<RegistroEvento, List<String>> extractorDerechosVulnerados(String nombreCampo, List<String> permitidos) {
	    return re -> {
	        if (re.getPersonasAfectadas() == null || re.getPersonasAfectadas().isEmpty())
	            return List.of();

	        return re.getPersonasAfectadas().stream()
	                 .filter(p -> p.getDerechosVulnerados() != null && !p.getDerechosVulnerados().isEmpty())
	                 .flatMap(p -> p.getDerechosVulnerados().stream())
	                 .map(DerechoVulnerado::getDerecho)
	                 .filter(Objects::nonNull)
	                 .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                 .map(Catalogo::getDescripcion)
	                 .filter(Objects::nonNull)
	                 .toList();
	    };
	}
	
	public Function<RegistroEvento, List<String>> extractorViolencia(String campo, List<String> permitidos) {
	    return switch (campo) {

	        case "esAsesinato" -> re -> {
	            if (re.getPersonasAfectadas() == null)
	                return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                .map(PersonaAfectada::getViolencia)
	                .filter(Objects::nonNull)
	                .map(Violencia::getEsAsesinato)
	                .map(this::booleanLabel)
	                .filter(Objects::nonNull)
	                .toList();
	        };

	        case "tiposViolencia" -> re -> {
	            if (re.getPersonasAfectadas() == null)
	                return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                .map(PersonaAfectada::getViolencia)
	                .filter(Objects::nonNull)
	                .map(Violencia::getTipoViolencia)
	                .filter(Objects::nonNull)
	                .filter(cat -> permitido(cat.getCodigo(), permitidos)) 
	                .map(Catalogo::getDescripcion)
	                .filter(Objects::nonNull)
	                .toList();
	        };

	        case "artefactosUtilizados" -> re -> {
	            if (re.getPersonasAfectadas() == null)
	                return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                .map(PersonaAfectada::getViolencia)
	                .filter(Objects::nonNull)
	                .map(Violencia::getArtefactoUtilizado)
	                .filter(Objects::nonNull)
	                .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                .map(Catalogo::getDescripcion)
	                .filter(Objects::nonNull)
	                .toList();
	        };

	        case "contextos" -> re -> {
	            if (re.getPersonasAfectadas() == null)
	                return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                .map(PersonaAfectada::getViolencia)
	                .filter(Objects::nonNull)
	                .map(Violencia::getContexto)
	                .filter(Objects::nonNull)
	                .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                .map(Catalogo::getDescripcion)
	                .filter(Objects::nonNull)
	                .toList();
	        };

	        case "actoresResponsables" -> re -> {
	            if (re.getPersonasAfectadas() == null)
	                return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                .map(PersonaAfectada::getViolencia)
	                .filter(Objects::nonNull)
	                .map(Violencia::getActorResponsable)
	                .filter(Objects::nonNull)
	                .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                .map(Catalogo::getDescripcion)
	                .filter(Objects::nonNull)
	                .toList();
	        };

	        case "estadosSaludActorResponsable" -> re -> {
	            if (re.getPersonasAfectadas() == null)
	                return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                .map(PersonaAfectada::getViolencia)
	                .filter(Objects::nonNull)
	                .map(Violencia::getEstadoSaludActorResponsable)
	                .filter(Objects::nonNull)
	                .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                .map(Catalogo::getDescripcion)
	                .filter(Objects::nonNull)
	                .toList();
	        };

	        case "huboProteccion" -> re -> {
	            if (re.getPersonasAfectadas() == null)
	                return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                .map(PersonaAfectada::getViolencia)
	                .filter(Objects::nonNull)
	                .map(Violencia::getHuboProteccion)
	                .map(this::booleanLabel)
	                .filter(Objects::nonNull)
	                .toList();
	        };

	        case "investigacionAbierta" -> re -> {
	            if (re.getPersonasAfectadas() == null)
	                return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                .map(PersonaAfectada::getViolencia)
	                .filter(Objects::nonNull)
	                .map(Violencia::getInvestigacionAbierta)
	                .map(this::booleanLabel)
	                .filter(Objects::nonNull)
	                .toList();
	        };

	        default -> re -> List.of("Sin dato");
	    };
	}
	
	/* =========================================================
	 *  Detención / Integridad   (evento.persona.detencionIntegridad)
	 * ========================================================= */
	private Function<RegistroEvento, List<String>> extractorDetencion(String campo,
	                                                                  List<String> permitidos) {

	    return switch (campo) {
	    
	        /* ----------  Catálogo: tipoDetencion  ---------- */
	        case "tiposDetencion" -> re -> {
	            if (re.getPersonasAfectadas() == null)  
	            	return List.of("Sin dato");

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDetencionIntegridad)
	                     .filter(Objects::nonNull)
	                     .map(DetencionIntegridad::getTipoDetencion)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Booleano: ordenJudicial  ---------- */
	        case "ordenJudicial" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDetencionIntegridad)
	                     .filter(Objects::nonNull)
	                     .map(DetencionIntegridad::getOrdenJudicial)
	                     .map(this::booleanLabel)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Catálogo: autoridadInvolucrada  ---------- */
	        case "autoridadesInvolucradas" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDetencionIntegridad)
	                     .filter(Objects::nonNull)
	                     .map(DetencionIntegridad::getAutoridadInvolucrada)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Booleano: huboTortura  ---------- */
	        case "huboTortura" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDetencionIntegridad)
	                     .filter(Objects::nonNull)
	                     .map(DetencionIntegridad::getHuboTortura)
	                     .map(this::booleanLabel)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Catálogo: motivoDetencion  ---------- */
	        case "motivosDetencion" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDetencionIntegridad)
	                     .filter(Objects::nonNull)
	                     .map(DetencionIntegridad::getMotivoDetencion)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Número: duracionDiasExactos  ---------- */
	        case "diasExactos" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            List<String> permitidosStr = permitidos == null ? List.of() : permitidos;

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDetencionIntegridad)
	                     .filter(Objects::nonNull)
	                     .map(DetencionIntegridad::getDuracionDias)
	                     .filter(Objects::nonNull)
	                     .map(String::valueOf)
	                     .filter(s -> permitidosStr.isEmpty() || permitidosStr.contains(s))
	                     .toList();
	        };

	        /* ----------  Booleano: accesoAbogado  ---------- */
	        case "accesoAbogado" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDetencionIntegridad)
	                     .filter(Objects::nonNull)
	                     .map(DetencionIntegridad::getAccesoAbogado)
	                     .map(this::booleanLabel)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Texto libre: resultado  ---------- */
	        case "resultados" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getDetencionIntegridad)
	                     .filter(Objects::nonNull)
	                     .map(DetencionIntegridad::getResultado)
	                     .filter(Objects::nonNull)
	                     .filter(res -> permitido(res, permitidos))  // aquí comparamos literal
	                     .toList();
	        };

	        /* ----------  Por defecto ---------- */
	        default -> re -> List.of();
	    };
	}


	/* =========================================================
	 *  Expresión / Censura  (evento → persona → expresionCensura)
	 * ========================================================= */
	private Function<RegistroEvento, List<String>> extractorCensura(
	        String campo,
	        List<String> permitidos) {

	    return switch (campo) {

	        /* ----------  Catálogo: medioExpresion  ---------- */
	        case "mediosExpresion" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getExpresionCensura)
	                     .filter(Objects::nonNull)
	                     .map(ExpresionCensura::getMedioExpresion)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Catálogo: tipoRepresion  ---------- */
	        case "tiposRepresion" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getExpresionCensura)
	                     .filter(Objects::nonNull)
	                     .map(ExpresionCensura::getTipoRepresion)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Booleano: represaliasLegales  ---------- */
	        case "represaliasLegales" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getExpresionCensura)
	                     .filter(Objects::nonNull)
	                     .map(ExpresionCensura::getRepresaliasLegales)
	                     .map(this::booleanLabel)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Booleano: represaliasFisicas  ---------- */
	        case "represaliasFisicas" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getExpresionCensura)
	                     .filter(Objects::nonNull)
	                     .map(ExpresionCensura::getRepresaliasFisicas)
	                     .map(this::booleanLabel)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Catálogo: actorCensor  ---------- */
	        case "actoresCensores" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getExpresionCensura)
	                     .filter(Objects::nonNull)
	                     .map(ExpresionCensura::getActorCensor)
	                     .filter(Objects::nonNull)
	                     .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                     .map(Catalogo::getDescripcion)
	                     .filter(Objects::nonNull)
	                     .toList();
	        };

	        /* ----------  Texto libre: consecuencia  ---------- */
	        case "consecuencias" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                     .map(PersonaAfectada::getExpresionCensura)
	                     .filter(Objects::nonNull)
	                     .map(ExpresionCensura::getConsecuencia)
	                     .filter(Objects::nonNull)
	                     .filter(txt -> permitido(txt, permitidos))   // comparación literal
	                     .toList();
	        };

	        /* ----------  Por defecto  ---------- */
	        default -> re -> List.of();
	    };
	}
	
	/* =========================================================
	 *  Acceso a la Justicia  (evento → persona → accesoJusticia)
	 * ========================================================= */
	private Function<RegistroEvento, List<String>> extractorJusticia(
	        String campo,
	        List<String> permitidos) {

	    return switch (campo) {

	        /* ---------- Catálogo: tipoProceso ---------- */
	        case "tiposProceso" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                    .map(PersonaAfectada::getAccesoJusticia)
	                    .filter(Objects::nonNull)
	                    .map(AccesoJusticia::getTipoProceso)
	                    .filter(Objects::nonNull)
	                    .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                    .map(Catalogo::getDescripcion)
	                    .filter(Objects::nonNull)
	                    .toList();
	        };

	        /* ---------- Catálogo: tipoDenunciante ---------- */
	        case "tiposDenunciante" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                    .map(PersonaAfectada::getAccesoJusticia)
	                    .filter(Objects::nonNull)
	                    .map(AccesoJusticia::getTipoDenunciante)
	                    .filter(Objects::nonNull)
	                    .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                    .map(Catalogo::getDescripcion)
	                    .filter(Objects::nonNull)
	                    .toList();
	        };

	        /* ---------- Catálogo: duracionProceso ---------- */
	        case "duracionesProceso" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                    .map(PersonaAfectada::getAccesoJusticia)
	                    .filter(Objects::nonNull)
	                    .map(AccesoJusticia::getDuracionProceso)
	                    .filter(Objects::nonNull)
	                    .filter(cat -> permitido(cat.getCodigo(), permitidos))
	                    .map(Catalogo::getDescripcion)
	                    .filter(Objects::nonNull)
	                    .toList();
	        };

	        /* ---------- Booleano: accesoAbogado ---------- */
	        case "accesoAbogado" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                    .map(PersonaAfectada::getAccesoJusticia)
	                    .filter(Objects::nonNull)
	                    .map(AccesoJusticia::getAccesoAbogado)
	                    .map(this::booleanLabel)
	                    .filter(Objects::nonNull)
	                    .toList();
	        };

	        /* ---------- Booleano: huboParcialidad ---------- */
	        case "huboParcialidad" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                    .map(PersonaAfectada::getAccesoJusticia)
	                    .filter(Objects::nonNull)
	                    .map(AccesoJusticia::getHuboParcialidad)
	                    .map(this::booleanLabel)
	                    .filter(Objects::nonNull)
	                    .toList();
	        };

	        /* ---------- Texto libre: resultadoProceso ---------- */
	        case "resultadosProceso" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                    .map(PersonaAfectada::getAccesoJusticia)
	                    .filter(Objects::nonNull)
	                    .map(AccesoJusticia::getResultadoProceso)
	                    .filter(Objects::nonNull)
	                    .filter(txt -> permitido(txt, permitidos))   // comparación literal
	                    .toList();
	        };

	        /* ---------- Texto libre: instancia ---------- */
	        case "instancias" -> re -> {
	            if (re.getPersonasAfectadas() == null) return List.of();

	            return re.getPersonasAfectadas().stream()
	                    .map(PersonaAfectada::getAccesoJusticia)
	                    .filter(Objects::nonNull)
	                    .map(AccesoJusticia::getInstancia)
	                    .filter(Objects::nonNull)
	                    .filter(txt -> permitido(txt, permitidos))
	                    .toList();
	        };

	        /* ---------- Por defecto ---------- */
	        default -> re -> List.of();
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
	
	/* Helper reutilizable */
	public boolean permitido(String codigo, List<String> permitidos) {
	    return permitidos==null || permitidos.isEmpty() || permitidos.contains(codigo);
	}

}
