package com.uca.idhuca.sistema.indicadores.repositories.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.BusquedaRegistroEventoResultado;
import com.uca.idhuca.sistema.indicadores.filtros.dto.EventoFiltro;
import com.uca.idhuca.sistema.indicadores.filtros.dto.PersonaAfectadaFiltro;
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


    /* ---------- HELPER 1: filtro por derecho ---------- */
    /** Devuelve la porción "FROM … WHERE …" con parámetro :der ya previsto */
    private String armarBaseQueryPorDerecho(CatalogoDto request, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(
            "FROM RegistroEvento re " +
            "JOIN re.personasAfectadas pa " +  
            "WHERE re.derechoAsociado = :der"
        );
        params.put("der", request.getDerecho());

        agregarFiltroPorFechas(request, sb, params);
        agregarFiltrosEvento(request, sb, params);
        agregarFiltrosPersonaAfectada(request, sb, params);  // ya usa alias pa

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
            return; // nada que agregar
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
}

