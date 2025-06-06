package com.uca.idhuca.sistema.indicadores.repositories.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.BusquedaRegistroEventoResultado;
import com.uca.idhuca.sistema.indicadores.filtros.dto.AccesoJusticiaFiltro;
import com.uca.idhuca.sistema.indicadores.filtros.dto.DetencionFiltro;
import com.uca.idhuca.sistema.indicadores.filtros.dto.EventoFiltro;
import com.uca.idhuca.sistema.indicadores.filtros.dto.PersonaAfectadaFiltro;
import com.uca.idhuca.sistema.indicadores.filtros.dto.RangoNumero;
import com.uca.idhuca.sistema.indicadores.filtros.dto.ViolenciaFiltro;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.repositories.custom.RegistroEventoRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RegistroEventoRepositoryImpl implements RegistroEventoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    /* ---------- MÉTODO PRINCIPAL ---------- */
    @Override
    public BusquedaRegistroEventoResultado buscarEventos(CatalogoDto request) {
        Map<String, Object> params = new HashMap<>();

        // Base query con derecho y más filtros
        String baseJpql = armarBaseQueryPorDerecho(request, params);

        // Conteo total
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(re) " + baseJpql, Long.class);
        params.forEach(countQuery::setParameter);
        Long total = countQuery.getSingleResult();

        // Query paginada
        TypedQuery<RegistroEvento> query = em.createQuery("SELECT re " + baseJpql, RegistroEvento.class);
        params.forEach(query::setParameter);
        aplicarPaginacion(query, request);

        return new BusquedaRegistroEventoResultado(query.getResultList(), total);
    }

    private String armarBaseQueryPorDerecho(CatalogoDto req,
            Map<String,Object> params) {

			boolean hayFiltroPersona   = req.getFiltros()!=null && req.getFiltros().getAfectadaFiltro()!=null;
			
			boolean hayFiltroDerechos  = req.getFiltros()!=null
			&& req.getFiltros().getDerechosVulneradosFiltro()!=null
			&& req.getFiltros().getDerechosVulneradosFiltro().getDerechosVulnerados()!=null
			&& !req.getFiltros().getDerechosVulneradosFiltro().getDerechosVulnerados().isEmpty();
			
			boolean hayFiltroViolencia = req.getFiltros()!=null && req.getFiltros().getViolenciaFiltro()!=null;
			boolean hayFiltroDetencion = req.getFiltros()!=null && req.getFiltros().getDetencionFiltro()!=null;
			boolean hayFiltroCensura   = req.getFiltros()!=null && req.getFiltros().getCensuraFiltro() !=null;
			boolean hayFiltroJusticia  = req.getFiltros()!=null && req.getFiltros().getAccesoJusticiaFiltro()!=null;
			
			StringBuilder sb = new StringBuilder("FROM RegistroEvento re ");
			
			/* JOIN a personas si algún filtro ligado a ‘pa’ está presente */
			if (hayFiltroPersona || hayFiltroDerechos || hayFiltroViolencia
			|| hayFiltroDetencion || hayFiltroCensura || hayFiltroJusticia) {
			sb.append("JOIN re.personasAfectadas pa ");
			}
			if (hayFiltroDerechos)  sb.append("JOIN pa.derechosVulnerados dv ");
			if (hayFiltroViolencia) sb.append("JOIN pa.violencia v ");
			if (hayFiltroDetencion) sb.append("JOIN pa.detencionIntegridad di ");
			if (hayFiltroCensura)   sb.append("JOIN pa.expresionCensura ec ");
			if (hayFiltroJusticia)  sb.append("JOIN pa.accesoJusticia aj ");
			
			sb.append("WHERE re.derechoAsociado = :der ");
			params.put("der", req.getDerecho());
			
			agregarFiltroPorFechas(req, sb, params);
			agregarFiltrosEvento(req, sb, params);
			agregarFiltrosPersonaAfectada(req, sb, params);
			agregarFiltrosDerechosVulnerados(req, sb, params);
			agregarFiltrosViolencia(req, sb, params);
			agregarFiltrosDetencion(req, sb, params);
			agregarFiltrosCensura(req, sb, params);
			agregarFiltrosJusticia(req, sb, params);  

			return sb.toString();
		}


    /* ---------- HELPER 2: paginación ---------- */
    private void aplicarPaginacion(TypedQuery<?> query, CatalogoDto request) {
        if (request.getFiltros() == null || request.getFiltros().getPaginacion() == null) {
            return; // no hay info de paginación → sin límite
        }

        var pag = request.getFiltros().getPaginacion();
        Integer size = pag.getRegistrosPorPagina();
        Integer page = pag.getPaginaActual();

        if (size != null && size > 0) {
            int pageIndex = (page != null && page >= 0) ? page : 0;
            query.setFirstResult(pageIndex * size);
            query.setMaxResults(size);
        }
    }
    
    /* ---------- HELPER 2: filtro por rango de fechas ---------- */
    private void agregarFiltroPorFechas(CatalogoDto request,
                                        StringBuilder where, Map<String, Object> params) {

        if (request.getFiltros() == null || request.getFiltros().getRangoFechas() == null) {
            return; 
        }

        var rango = request.getFiltros().getRangoFechas();
        
        java.sql.Timestamp fechaInicio = java.sql.Timestamp.valueOf(rango.getFechaInicio().atStartOfDay());
        java.sql.Timestamp fechaFin = java.sql.Timestamp.valueOf(rango.getFechaFin().atTime(23,59,59,999_000_000));

        if (rango.getFechaInicio() != null) {
            where.append(" AND re.fechaRegistro >= :fechaInicio");
            log.info("[SYSTEM] AND re.fechaRegistro >= :{}", fechaInicio);
            params.put("fechaInicio",fechaInicio);
        }

        if (rango.getFechaFin() != null) {
            where.append(" AND re.fechaRegistro <= :fechaFin");
            log.info("[SYSTEM] AND re.fechaRegistro <= :{}", fechaFin);
            params.put("fechaFin", fechaFin);
        }
    }
    
    /* ---------- HELPER 4: filtros del bloque EventoFiltro ---------- */
    private void agregarFiltrosEvento(CatalogoDto request,
                                      StringBuilder where, Map<String,Object> params) {

        var filtros = request.getFiltros();
        if (filtros == null || filtros.getEventoFiltro() == null) return;

        EventoFiltro ev = filtros.getEventoFiltro();

        if (ev.getFechaHechoRango() != null) {
            var fr = ev.getFechaHechoRango();
            if (fr.getFechaInicio() != null) {
                where.append(" AND re.fechaHecho >= :fhInicio");
                log.info("[SYSTEM] AND re.fechaHecho >= :{}", fr.getFechaInicio());
                params.put("fhInicio", fr.getFechaInicio());
            }
            if (fr.getFechaFin() != null) {
                where.append(" AND re.fechaHecho <= :fhFin");
                log.info("[SYSTEM] AND re.fechaHecho <= :{}", fr.getFechaFin());
                params.put("fhFin", fr.getFechaFin());
            }
        }

        if (ev.getFuentes() != null && !ev.getFuentes().isEmpty()) {
            List<String> codigosFuente = ev.getFuentes().stream().map(Catalogo::getCodigo).toList();
            where.append(" AND re.fuente.codigo IN :fuentes");
            log.info("[SYSTEM] AND re.fuente.codigo IN :{}", codigosFuente);
            params.put("fuentes", codigosFuente);
        }

        if (ev.getEstadosActuales() != null && !ev.getEstadosActuales().isEmpty()) {
            List<String> codigosEstados = ev.getEstadosActuales().stream().map(Catalogo::getCodigo).toList();
            where.append(" AND re.estadoActual.codigo IN :estadosAct");
            log.info("[SYSTEM] AND re.estadoActual.codigo IN :{}", codigosEstados);
            params.put("estadosAct", codigosEstados);
        }

        if (ev.getDepartamentos() != null && !ev.getDepartamentos().isEmpty()) {
            List<String> codigosDeps = ev.getDepartamentos().stream().map(Catalogo::getCodigo).toList();
            where.append(" AND re.ubicacion.departamento.codigo IN :deps");
            log.info("[SYSTEM] AND re.ubicacion.departamento.codigo IN :{}", codigosDeps);
            params.put("deps", codigosDeps);
        }

        if (ev.getMunicipios() != null && !ev.getMunicipios().isEmpty()) {
            List<String> codigosMuns = ev.getMunicipios().stream().map(Catalogo::getCodigo).toList();
            where.append(" AND re.ubicacion.municipio.codigo IN :muns");
            log.info("[SYSTEM] AND re.ubicacion.municipio.codigo IN :{}", codigosMuns);
            params.put("muns", codigosMuns);
        }

        if (ev.getLugaresExactos() != null && !ev.getLugaresExactos().isEmpty()) {
            List<String> codigosLugExac = ev.getLugaresExactos().stream().map(Catalogo::getCodigo).toList();
            where.append(" AND re.ubicacion.lugarExacto.codigo IN :lugExac");
            log.info("[SYSTEM] AND re.ubicacion.lugarExacto.codigo IN :{}", codigosLugExac);
            params.put("lugExac", codigosLugExac);
        }

        if (ev.getFlagViolencia() != null) {
            where.append(" AND re.flagViolencia = :flagViolencia");
            log.info("[SYSTEM] AND re.flagViolencia = :{}", ev.getFlagViolencia());
            params.put("flagViolencia", ev.getFlagViolencia());
        }
        if (ev.getFlagDetencion() != null) {
            where.append(" AND re.flagDetencion = :flagDetencion");
            log.info("[SYSTEM] AND re.flagDetencion = :{}", ev.getFlagDetencion());
            params.put("flagDetencion", ev.getFlagDetencion());
        }
        if (ev.getFlagExpresion() != null) {
            where.append(" AND re.flagExpresion = :flagExpresion");
            log.info("[SYSTEM] AND re.flagExpresion = :{}", ev.getFlagExpresion());
            params.put("flagExpresion", ev.getFlagExpresion());
        }
        if (ev.getFlagJusticia() != null) {
            where.append(" AND re.flagJusticia = :flagJusticia");
            log.info("[SYSTEM] AND re.flagJusticia = :{}", ev.getFlagJusticia());
            params.put("flagJusticia", ev.getFlagJusticia());
        }
        if (ev.getFlagCensura() != null) {
            where.append(" AND re.flagCensura = :flagCensura");
            log.info("[SYSTEM] AND re.flagCensura = :{}", ev.getFlagCensura());
            params.put("flagCensura", ev.getFlagCensura());
        }
        if (ev.getFlagRegimenExcepcion() != null) {
            where.append(" AND re.flagRegimenExcepcion = :flagRegExc");
            log.info("[SYSTEM] AND re.flagRegimenExcepcion = :{}", ev.getFlagRegimenExcepcion());
            params.put("flagRegExc", ev.getFlagRegimenExcepcion());
        }
    }
    
    private void agregarFiltrosPersonaAfectada(CatalogoDto request, StringBuilder sb, Map<String, Object> params) {
        var filtros = request.getFiltros();
        if (filtros == null || filtros.getAfectadaFiltro() == null) return;

        PersonaAfectadaFiltro filtro = filtros.getAfectadaFiltro();

        if (filtro.getNombres() != null && !filtro.getNombres().isEmpty()) {
            sb.append(" AND (");
            for (int i = 0; i < filtro.getNombres().size(); i++) {
                sb.append(" LOWER(pa.nombre) LIKE LOWER(CONCAT('%', :nombre").append(i).append(", '%')) ");
                if (i < filtro.getNombres().size() - 1) sb.append(" OR ");
                params.put("nombre" + i, filtro.getNombres().get(i));
                log.info("[SYSTEM] Filtro nombre: LIKE %{}%", filtro.getNombres().get(i));
            }
            sb.append(") ");
        }

        if (filtro.getGeneros() != null && !filtro.getGeneros().isEmpty()) {
            List<String> codigosGeneros = filtro.getGeneros().stream().map(Catalogo::getCodigo).toList();
            sb.append(" AND pa.genero.codigo IN :codigosGeneros ");
            params.put("codigosGeneros", codigosGeneros);
            log.info("[SYSTEM] Filtro genero IN {}", codigosGeneros);
        }

        if (filtro.getNacionalidades() != null && !filtro.getNacionalidades().isEmpty()) {
            List<String> codigosNac = filtro.getNacionalidades().stream().map(Catalogo::getCodigo).toList();
            sb.append(" AND pa.nacionalidad.codigo IN :codigosNac ");
            params.put("codigosNac", codigosNac);
            log.info("[SYSTEM] Filtro nacionalidad IN {}", codigosNac);
        }

        if (filtro.getDepartamentosResidencia() != null && !filtro.getDepartamentosResidencia().isEmpty()) {
            List<String> codigosDeps = filtro.getDepartamentosResidencia().stream().map(Catalogo::getCodigo).toList();
            sb.append(" AND pa.departamentoResidencia.codigo IN :codigosDeps ");
            params.put("codigosDeps", codigosDeps);
            log.info("[SYSTEM] Filtro departamento residencia IN {}", codigosDeps);
        }

        if (filtro.getMunicipiosResidencia() != null && !filtro.getMunicipiosResidencia().isEmpty()) {
            List<String> codigosMuns = filtro.getMunicipiosResidencia().stream().map(Catalogo::getCodigo).toList();
            sb.append(" AND pa.municipioResidencia.codigo IN :codigosMuns ");
            params.put("codigosMuns", codigosMuns);
            log.info("[SYSTEM] Filtro municipio residencia IN {}", codigosMuns);
        }

        if (filtro.getTiposPersona() != null && !filtro.getTiposPersona().isEmpty()) {
            List<String> codigosTipos = filtro.getTiposPersona().stream().map(Catalogo::getCodigo).toList();
            sb.append(" AND pa.tipoPersona.codigo IN :codigosTipos ");
            params.put("codigosTipos", codigosTipos);
            log.info("[SYSTEM] Filtro tipo persona IN {}", codigosTipos);
        }

        if (filtro.getEstadosSalud() != null && !filtro.getEstadosSalud().isEmpty()) {
            List<String> codigosEstados = filtro.getEstadosSalud().stream().map(Catalogo::getCodigo).toList();
            sb.append(" AND pa.estadoSalud.codigo IN :codigosEstados ");
            params.put("codigosEstados", codigosEstados);
            log.info("[SYSTEM] Filtro estado salud IN {}", codigosEstados);
        }

        if (filtro.getRangoEdad() != null) {
            if (filtro.getRangoEdad().getDesde() != null) {
                sb.append(" AND pa.edad >= :edadMin ");
                params.put("edadMin", filtro.getRangoEdad().getDesde());
                log.info("[SYSTEM] Filtro edad Min >= {}", filtro.getRangoEdad().getDesde());
            }
            if (filtro.getRangoEdad().getHasta() != null) {
                sb.append(" AND pa.edad <= :edadMax ");
                params.put("edadMax", filtro.getRangoEdad().getHasta());
                log.info("[SYSTEM] Filtro edad Max <= {}", filtro.getRangoEdad().getHasta());
            }
        }
    }
    
    /* ---------- HELPER: filtros de derechos vulnerados (alias dv) ---------- */
    private void agregarFiltrosDerechosVulnerados(CatalogoDto req,
                                                  StringBuilder sb,
                                                  Map<String,Object> params) {

        if (req.getFiltros()==null) return;

        var dvFiltro = req.getFiltros().getDerechosVulneradosFiltro();
        if (dvFiltro==null || dvFiltro.getDerechosVulnerados()==null
            || dvFiltro.getDerechosVulnerados().isEmpty()) return;

        List<String> codigos = dvFiltro.getDerechosVulnerados()
                                       .stream()
                                       .map(Catalogo::getCodigo)
                                       .toList();

        sb.append(" AND dv.derecho.codigo IN :codDVs ");
        params.put("codDVs", codigos);

        log.info("[SYSTEM] Filtro derechos vulnerados IN {}", codigos);
    }
    
    private void agregarFiltrosViolencia(CatalogoDto req, StringBuilder sb, Map<String, Object> params) {
        if (req.getFiltros() == null || req.getFiltros().getViolenciaFiltro() == null) return;

        ViolenciaFiltro filtro = req.getFiltros().getViolenciaFiltro();

        boolean seRequiereJoinViolencia =
            filtro.getEsAsesinato() != null ||
            filtro.getTiposViolencia() != null && !filtro.getTiposViolencia().isEmpty() ||
            filtro.getArtefactosUtilizados() != null && !filtro.getArtefactosUtilizados().isEmpty() ||
            filtro.getContextos() != null && !filtro.getContextos().isEmpty() ||
            filtro.getActoresResponsables() != null && !filtro.getActoresResponsables().isEmpty() ||
            filtro.getEstadosSaludActorResponsable() != null && !filtro.getEstadosSaludActorResponsable().isEmpty() ||
            filtro.getHuboProteccion() != null ||
            filtro.getInvestigacionAbierta() != null;

        if (!seRequiereJoinViolencia) return;

        if (filtro.getEsAsesinato() != null) {
            sb.append(" AND v.esAsesinato = :esAsesinato ");
            params.put("esAsesinato", filtro.getEsAsesinato());
            log.info("[SYSTEM] Filtro violencia: esAsesinato = {}", filtro.getEsAsesinato());
        }

        if (filtro.getTiposViolencia() != null && !filtro.getTiposViolencia().isEmpty()) {
            List<String> codigos = filtro.getTiposViolencia().stream()
                .map(Catalogo::getCodigo).toList();
            sb.append(" AND v.tipoViolencia.codigo IN :tiposViolencia ");
            params.put("tiposViolencia", codigos);
            log.info("[SYSTEM] Filtro violencia: tiposViolencia IN {}", codigos);
        }

        if (filtro.getArtefactosUtilizados() != null && !filtro.getArtefactosUtilizados().isEmpty()) {
            List<String> codigos = filtro.getArtefactosUtilizados().stream()
                .map(Catalogo::getCodigo).toList();
            sb.append(" AND v.artefactoUtilizado.codigo IN :artefactosUtilizados ");
            params.put("artefactosUtilizados", codigos);
            log.info("[SYSTEM] Filtro violencia: artefactosUtilizados IN {}", codigos);
        }

        if (filtro.getContextos() != null && !filtro.getContextos().isEmpty()) {
            List<String> codigos = filtro.getContextos().stream()
                .map(Catalogo::getCodigo).toList();
            sb.append(" AND v.contexto.codigo IN :contextos ");
            params.put("contextos", codigos);
            log.info("[SYSTEM] Filtro violencia: contextos IN {}", codigos);
        }

        if (filtro.getActoresResponsables() != null && !filtro.getActoresResponsables().isEmpty()) {
            List<String> codigos = filtro.getActoresResponsables().stream()
                .map(Catalogo::getCodigo).toList();
            sb.append(" AND v.actorResponsable.codigo IN :actoresResponsables ");
            params.put("actoresResponsables", codigos);
            log.info("[SYSTEM] Filtro violencia: actoresResponsables IN {}", codigos);
        }

        if (filtro.getEstadosSaludActorResponsable() != null && !filtro.getEstadosSaludActorResponsable().isEmpty()) {
            List<String> codigos = filtro.getEstadosSaludActorResponsable().stream()
                .map(Catalogo::getCodigo).toList();
            sb.append(" AND v.estadoSaludActorResponsable.codigo IN :estadosSaludActorResponsable ");
            params.put("estadosSaludActorResponsable", codigos);
            log.info("[SYSTEM] Filtro violencia: estadosSaludActorResponsable IN {}", codigos);
        }

        if (filtro.getHuboProteccion() != null) {
            sb.append(" AND v.huboProteccion = :huboProteccion ");
            params.put("huboProteccion", filtro.getHuboProteccion());
            log.info("[SYSTEM] Filtro violencia: huboProteccion = {}", filtro.getHuboProteccion());
        }

        if (filtro.getInvestigacionAbierta() != null) {
            sb.append(" AND v.investigacionAbierta = :investigacionAbierta ");
            params.put("investigacionAbierta", filtro.getInvestigacionAbierta());
            log.info("[SYSTEM] Filtro violencia: investigacionAbierta = {}", filtro.getInvestigacionAbierta());
        }
    }

    /** Aplica filtros sobre la entidad DetencionIntegridad (alias di). */
    private void agregarFiltrosDetencion(CatalogoDto req,
                                         StringBuilder sb,
                                         Map<String,Object> params) {

        if (req.getFiltros()==null || req.getFiltros().getDetencionFiltro()==null) return;

        DetencionFiltro f = req.getFiltros().getDetencionFiltro();

        /* 1. Tipo de detención */
        if (f.getTiposDetencion()!=null && !f.getTiposDetencion().isEmpty()) {
            List<String> codigos = f.getTiposDetencion().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND di.tipoDetencion.codigo IN :tiposDet ");
            params.put("tiposDet", codigos);
            log.info("[SYSTEM] Filtro detención: tiposDetencion IN {}", codigos);
        }

        /* 2. Orden judicial */
        if (f.getOrdenJudicial()!=null) {
            sb.append(" AND di.ordenJudicial = :ordenJud ");
            params.put("ordenJud", f.getOrdenJudicial());
            log.info("[SYSTEM] Filtro detención: ordenJudicial = {}", f.getOrdenJudicial());
        }

        /* 3. Autoridades involucradas */
        if (f.getAutoridadesInvolucradas()!=null && !f.getAutoridadesInvolucradas().isEmpty()) {
            List<String> codigos = f.getAutoridadesInvolucradas().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND di.autoridadInvolucrada.codigo IN :autoridadesInv ");
            params.put("autoridadesInv", codigos);
            log.info("[SYSTEM] Filtro detención: autoridadesInvolucradas IN {}", codigos);
        }

        /* 4. Hubo tortura */
        if (f.getHuboTortura()!=null) {
            sb.append(" AND di.huboTortura = :huboTortura ");
            params.put("huboTortura", f.getHuboTortura());
            log.info("[SYSTEM] Filtro detención: huboTortura = {}", f.getHuboTortura());
        }

        /* 5. Motivos de detención */
        if (f.getMotivosDetencion()!=null && !f.getMotivosDetencion().isEmpty()) {
            List<String> codigos = f.getMotivosDetencion().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND di.motivoDetencion.codigo IN :motivosDet ");
            params.put("motivosDet", codigos);
            log.info("[SYSTEM] Filtro detención: motivosDetencion IN {}", codigos);
        }

        /* 6. Duración en días (rango) */
        if (f.getDuracionDiasExactos() != null) {
            RangoNumero rango = f.getDuracionDiasExactos();

            if (rango.getDesde() != null && rango.getHasta() != null) {
                sb.append(" AND di.duracionDias BETWEEN :durDesde AND :durHasta ");
                params.put("durDesde", rango.getDesde());
                params.put("durHasta", rango.getHasta());
                log.info("[SYSTEM] Filtro detención: duracionDias BETWEEN {} AND {}", rango.getDesde(), rango.getHasta());

            } else if (rango.getDesde() != null) {
                sb.append(" AND di.duracionDias >= :durDesde ");
                params.put("durDesde", rango.getDesde());
                log.info("[SYSTEM] Filtro detención: duracionDias >= {}", rango.getDesde());

            } else if (rango.getHasta() != null) {
                sb.append(" AND di.duracionDias <= :durHasta ");
                params.put("durHasta", rango.getHasta());
                log.info("[SYSTEM] Filtro detención: duracionDias <= {}", rango.getHasta());
            }
        }

        /* 8. Acceso a abogado */
        if (f.getAccesoAbogado()!=null) {
            sb.append(" AND di.accesoAbogado = :accAbog ");
            params.put("accAbog", f.getAccesoAbogado());
            log.info("[SYSTEM] Filtro detención: accesoAbogado = {}", f.getAccesoAbogado());
        }

        /* 9. Resultados (texto libre) */
        if (f.getResultados()!=null && !f.getResultados().isEmpty()) {
            sb.append(" AND (");
            for (int i=0;i<f.getResultados().size();i++) {
                sb.append(" LOWER(di.resultado) LIKE LOWER(CONCAT('%', :res").append(i).append(", '%')) ");
                if (i < f.getResultados().size()-1) sb.append(" OR ");
                params.put("res"+i, f.getResultados().get(i));
                log.info("[SYSTEM] Filtro detención: resultado LIKE %{}%", f.getResultados().get(i));
            }
            sb.append(") ");
        }
    }

    private void agregarFiltrosCensura(CatalogoDto req, StringBuilder sb, Map<String, Object> params) {
        var f = req.getFiltros().getCensuraFiltro();
        if (f == null) return;

        /* 1. Medios de expresión */
        if (f.getMediosExpresion() != null && !f.getMediosExpresion().isEmpty()) {
            List<String> codigos = f.getMediosExpresion().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND ec.medioExpresion.codigo IN :mediosExpr ");
            params.put("mediosExpr", codigos);
            log.info("[SYSTEM] Filtro censura: mediosExpresion IN {}", codigos);
        }

        /* 2. Tipos de represión */
        if (f.getTiposRepresion() != null && !f.getTiposRepresion().isEmpty()) {
            List<String> codigos = f.getTiposRepresion().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND ec.tipoRepresion.codigo IN :tiposRep ");
            params.put("tiposRep", codigos);
            log.info("[SYSTEM] Filtro censura: tiposRepresion IN {}", codigos);
        }

        /* 3. Represalias legales */
        if (f.getRepresaliasLegales() != null) {
            sb.append(" AND ec.represaliasLegales = :repLegal ");
            params.put("repLegal", f.getRepresaliasLegales());
            log.info("[SYSTEM] Filtro censura: represaliasLegales = {}", f.getRepresaliasLegales());
        }

        /* 4. Represalias físicas */
        if (f.getRepresaliasFisicas() != null) {
            sb.append(" AND ec.represaliasFisicas = :repFisica ");
            params.put("repFisica", f.getRepresaliasFisicas());
            log.info("[SYSTEM] Filtro censura: represaliasFisicas = {}", f.getRepresaliasFisicas());
        }

        /* 5. Actores censores */
        if (f.getActoresCensores() != null && !f.getActoresCensores().isEmpty()) {
            List<String> codigos = f.getActoresCensores().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND ec.actorCensor.codigo IN :actoresCens ");
            params.put("actoresCens", codigos);
            log.info("[SYSTEM] Filtro censura: actoresCensores IN {}", codigos);
        }

        /* 6. Consecuencias (strings) */
        if (f.getConsecuencias() != null && !f.getConsecuencias().isEmpty()) {
            sb.append(" AND ec.consecuencia IN :consecuencias ");
            params.put("consecuencias", f.getConsecuencias());
            log.info("[SYSTEM] Filtro censura: consecuencias IN {}", f.getConsecuencias());
        }
    }

    /**  Filtros sobre la entidad AccesoJusticia (alias aj). */
    private void agregarFiltrosJusticia(CatalogoDto req,
                                        StringBuilder sb,
                                        Map<String,Object> params) {

        AccesoJusticiaFiltro f = req.getFiltros()!=null ? req.getFiltros().getAccesoJusticiaFiltro() : null;
        if (f == null) return;

        /* 1. Tipos de proceso */
        if (f.getTiposProceso()!=null && !f.getTiposProceso().isEmpty()) {
            List<String> codigos = f.getTiposProceso().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND aj.tipoProceso.codigo IN :tiposProc ");
            params.put("tiposProc", codigos);
            log.info("[SYSTEM] Filtro justicia: tiposProceso IN {}", codigos);
        }

        /* 2. Fecha de denuncia (rango) */
        if (f.getFechaDenunciaRango()!=null) {
            var r = f.getFechaDenunciaRango();
            if (r.getFechaInicio()!=null) {
                java.sql.Timestamp ini = java.sql.Timestamp.valueOf(r.getFechaInicio().atStartOfDay());
                sb.append(" AND aj.fechaDenuncia >= :fdIni ");
                params.put("fdIni", ini);
                log.info("[SYSTEM] Filtro justicia: fechaDenuncia >= {}", ini);
            }
            if (r.getFechaFin()!=null) {
                java.sql.Timestamp fin = java.sql.Timestamp.valueOf(r.getFechaFin().atTime(23,59,59,999_000_000));
                sb.append(" AND aj.fechaDenuncia <= :fdFin ");
                params.put("fdFin", fin);
                log.info("[SYSTEM] Filtro justicia: fechaDenuncia <= {}", fin);
            }
        }

        /* 3. Tipo de denunciante */
        if (f.getTiposDenunciante()!=null && !f.getTiposDenunciante().isEmpty()) {
            List<String> codigos = f.getTiposDenunciante().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND aj.tipoDenunciante.codigo IN :tipDen ");
            params.put("tipDen", codigos);
            log.info("[SYSTEM] Filtro justicia: tiposDenunciante IN {}", codigos);
        }

        /* 4. Duración del proceso */
        if (f.getDuracionesProceso()!=null && !f.getDuracionesProceso().isEmpty()) {
            List<String> codigos = f.getDuracionesProceso().stream()
                                    .map(Catalogo::getCodigo).toList();
            sb.append(" AND aj.duracionProceso.codigo IN :durProc ");
            params.put("durProc", codigos);
            log.info("[SYSTEM] Filtro justicia: duracionesProceso IN {}", codigos);
        }

        /* 5. Acceso a abogado */
        if (f.getAccesoAbogado()!=null) {
            sb.append(" AND aj.accesoAbogado = :accAbog ");
            params.put("accAbog", f.getAccesoAbogado());
            log.info("[SYSTEM] Filtro justicia: accesoAbogado = {}", f.getAccesoAbogado());
        }

        /* 6. Hubo parcialidad */
        if (f.getHuboParcialidad()!=null) {
            sb.append(" AND aj.huboParcialidad = :parcial ");
            params.put("parcial", f.getHuboParcialidad());
            log.info("[SYSTEM] Filtro justicia: huboParcialidad = {}", f.getHuboParcialidad());
        }

        /* 7. Resultados del proceso */
        if (f.getResultadosProceso()!=null && !f.getResultadosProceso().isEmpty()) {
            sb.append(" AND aj.resultadoProceso IN :resProc ");
            params.put("resProc", f.getResultadosProceso());
            log.info("[SYSTEM] Filtro justicia: resultadosProceso IN {}", f.getResultadosProceso());
        }

        /* 8. Instancias */
        if (f.getInstancias()!=null && !f.getInstancias().isEmpty()) {
            sb.append(" AND aj.instancia IN :insts ");
            params.put("insts", f.getInstancias());
            log.info("[SYSTEM] Filtro justicia: instancias IN {}", f.getInstancias());
        }
    }

}

