package com.uca.idhuca.sistema.indicadores.services.impl;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.models.AccesoJusticia;
import com.uca.idhuca.sistema.indicadores.models.DetencionIntegridad;
import com.uca.idhuca.sistema.indicadores.models.ExpresionCensura;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.models.Violencia;
import com.uca.idhuca.sistema.indicadores.services.IExcel;

import org.apache.poi.ss.usermodel.*;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelServiceImpl implements IExcel {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
 // ===== MAPA DE ANCHOS POR HOJA =====
    private static final Map<String, Map<Integer, Integer>> MAPA_ANCHOS_COLUMNAS = new HashMap<>();

    static {
        // Hoja: Personas Afectadas
        Map<Integer, Integer> anchosPersonas = new HashMap<>();
        anchosPersonas.put(0, 5000);
        anchosPersonas.put(1, 5000);
        anchosPersonas.put(2, 7000);
        anchosPersonas.put(3, 5000);
        anchosPersonas.put(4, 4000);
        anchosPersonas.put(5, 7000);
        anchosPersonas.put(6, 7000);
        anchosPersonas.put(7, 7000);
        anchosPersonas.put(8, 7000);
        anchosPersonas.put(9, 7000);
        anchosPersonas.put(10, 20000);
        MAPA_ANCHOS_COLUMNAS.put("Personas Afectadas", anchosPersonas);

        // Hoja: Violencia
        Map<Integer, Integer> anchosViolencia = new HashMap<>();
        anchosViolencia.put(0, 5000);
        anchosViolencia.put(5, 20000);
        MAPA_ANCHOS_COLUMNAS.put("Violencia", anchosViolencia);

        // Hoja: Detención e Integridad
        Map<Integer, Integer> anchosDetencion = new HashMap<>();
        anchosDetencion.put(0, 5000);
        anchosDetencion.put(9, 20000);
        MAPA_ANCHOS_COLUMNAS.put("Detencion e Integridad", anchosDetencion);

        // Hoja: Expresión y Censura
        Map<Integer, Integer> anchosExpresion = new HashMap<>();
        anchosExpresion.put(0, 5000);
        anchosExpresion.put(7, 15000);
        MAPA_ANCHOS_COLUMNAS.put("Expresion y Censura", anchosExpresion);

        // Hoja: Acceso a Justicia
        Map<Integer, Integer> anchosJusticia = new HashMap<>();
        anchosJusticia.put(0, 5000);
        anchosJusticia.put(8, 15000);
        MAPA_ANCHOS_COLUMNAS.put("Acceso a Justicia", anchosJusticia);
    }
    
    private CellStyle crearEstiloHeader(Workbook wb) {
        CellStyle style = wb.createCellStyle();

        // Fuente
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14); // tamaño más grande
        font.setColor(IndexedColors.WHITE.getIndex()); // letras blancas
        style.setFont(font);

        // Alineación y wrap
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        // Fondo negro
        style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }
    
    private CellStyle crearEstiloWrap(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle crearEstiloCentrado(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }
    
    

    
 //  Método mejorado para ajustar columnas y guardar sus anchos
    private Map<Integer, Integer> ajustarColumnas(SXSSFSheet sheet, int numCols, int anchoDefault, int anchoEspecial, int... colsWrap) {
        Map<Integer, Integer> mapaAnchos = new HashMap<>();

        for (int i = 0; i < numCols; i++) {
            boolean esColumnaEspecial = false;
            for (int colWrap : colsWrap) {
                if (i == colWrap) {
                    esColumnaEspecial = true;
                    break;
                }
            }
            int ancho = esColumnaEspecial ? anchoEspecial : anchoDefault;
            sheet.setColumnWidth(i, ancho);
            mapaAnchos.put(i, ancho);
        }

        return mapaAnchos;
    }

    // ✅ Método para centrar texto y permitir wrap
    private void agregarEstiloCentrado(CellStyle style) {
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
    }
    
    private void ajustarColumnas(SXSSFSheet sheet, String hoja) {
        Map<Integer, Integer> anchos = MAPA_ANCHOS_COLUMNAS.getOrDefault(hoja, Map.of());
        for (Map.Entry<Integer, Integer> entry : anchos.entrySet()) {
            sheet.setColumnWidth(entry.getKey(), entry.getValue());
        }
    }

    private void ajustarAlturaDinamica(Row row, SXSSFSheet sheet) {
        float maxLines = 1f;
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue();
                int colWidth = sheet.getColumnWidth(cell.getColumnIndex()) / 256; // caracteres aprox
                if (colWidth > 0 && value != null && !value.isEmpty()) {
                    int estimatedLines = (int) Math.ceil((double) value.length() / colWidth);
                    maxLines = Math.max(maxLines, estimatedLines);
                }
            }
        }
        row.setHeightInPoints(maxLines * sheet.getDefaultRowHeightInPoints());
    }


    
    @Override
    public void generarExcel(List<RegistroEvento> datos, OutputStream outputStream, String key) throws Exception {
        log.info("[{}] Inicio de creación de Excel.", key);

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {

            crearHojaRegistroDeEventos(workbook, datos);
            crearHojaPersonasAfectadas(workbook, datos);
            crearHojaDetalle(workbook, datos, "Violencia", this::llenarFilaViolencia);
            crearHojaDetalle(workbook, datos, "Detencion e Integridad", this::llenarFilaDetencion);
            crearHojaDetalle(workbook, datos, "Expresion y Censura", this::llenarFilaExpresion);
            crearHojaDetalle(workbook, datos, "Acceso a Justicia", this::llenarFilaJusticia);

            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            log.error("[{}] Error generando Excel: {}", key, e.getMessage(), e);
            throw e;
        } finally {
            log.info("[{}] Fin de creación de Excel.", key);
        }
    }

    private void crearHojaRegistroDeEventos(SXSSFWorkbook workbook, List<RegistroEvento> registros) {
        SXSSFSheet sheet = workbook.createSheet("Registro de Eventos");

        CellStyle headerStyle = crearEstiloHeader(workbook);
        CellStyle centerText = workbook.createCellStyle();
        agregarEstiloCentrado(centerText);

        Row header = sheet.createRow(0);
        header.setHeightInPoints(30);
        String[] cols = {
            "ID", "Fecha Registro", "Fecha Hecho", "Departamento Hecho", "Municipio Hecho",
            "Lugar Exacto", "Fuente", "Estado Actual", "Derecho Asociado", "Régimen Excepción",
            "Registro creado Por", "Observaciones"
        };

        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        // 🔹 Guardamos los anchos de columna para el cálculo de altura
        Map<Integer, Integer> anchos = ajustarColumnas(sheet, cols.length, 6000, 10000, 11);

        int rowIdx = 1;
        for (RegistroEvento r : registros) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getId());
            row.createCell(1).setCellValue(r.getFechaRegistro() != null ? sdf.format(r.getFechaRegistro()) : "");
            row.createCell(2).setCellValue(r.getFechaHecho() != null ? r.getFechaHecho().format(dtf) : "");
            row.createCell(3).setCellValue(r.getUbicacion() != null && r.getUbicacion().getDepartamento() != null
                    ? r.getUbicacion().getDepartamento().getDescripcion() : "");
            row.createCell(4).setCellValue(r.getUbicacion() != null && r.getUbicacion().getMunicipio() != null
                    ? r.getUbicacion().getMunicipio().getDescripcion() : "");
            row.createCell(5).setCellValue(r.getUbicacion() != null && r.getUbicacion().getLugarExacto() != null
                    ? r.getUbicacion().getLugarExacto().getDescripcion() : "");
            row.createCell(6).setCellValue(r.getFuente() != null ? r.getFuente().getDescripcion() : "");
            row.createCell(7).setCellValue(r.getEstadoActual() != null ? r.getEstadoActual().getDescripcion() : "");
            row.createCell(8).setCellValue(r.getDerechoAsociado() != null ? r.getDerechoAsociado().getDescripcion() : "");
            row.createCell(9).setCellValue(r.getFlagRegimenExcepcion() != null && r.getFlagRegimenExcepcion() ? "SI" : "NO");
            row.createCell(10).setCellValue(r.getCreadoPor() != null ? r.getCreadoPor().getEmail() : "");
            row.createCell(11).setCellValue(r.getObservaciones() != null ? r.getObservaciones() : "");

            // Aplicar estilo centrado
            for (int i = 0; i < cols.length; i++) {
                row.getCell(i).setCellStyle(centerText);
            }

            // 🔹 Ajustar altura según el texto y el ancho real
            ajustarAlturaDinamica(row, sheet);
        }
    }


    public void crearHojaPersonasAfectadas(SXSSFWorkbook workbook, List<RegistroEvento> registros) {
        SXSSFSheet sheet = workbook.createSheet("Personas Afectadas");

        CellStyle headerStyle = crearEstiloHeader(workbook);
        CellStyle wrapStyle = crearEstiloWrap(workbook);
        CellStyle centerStyle = crearEstiloCentrado(workbook);

        Row header = sheet.createRow(0);
        header.setHeightInPoints(40);

        String[] cols = {
                "Evento ID", "Persona ID", "Nombre", "Género", "Edad", "Nacionalidad",
                "Departamento Residencia", "Municipio Residencia", "Tipo Persona",
                "Estado de Salud", "Derechos Vulnerados"
        };

        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        ajustarColumnas(sheet, "Personas Afectadas");

        int rowIdx = 1;
        for (RegistroEvento evento : registros) {
            if (evento.getPersonasAfectadas() == null) continue;

            for (PersonaAfectada p : evento.getPersonasAfectadas()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(evento.getId());
                row.createCell(1).setCellValue(p.getId());
                row.createCell(2).setCellValue(p.getNombre() != null ? p.getNombre() : "");
                row.createCell(3).setCellValue(p.getGenero() != null ? p.getGenero().getDescripcion() : "");
                row.createCell(4).setCellValue(p.getEdad() != null ? p.getEdad() : 0);
                row.createCell(5).setCellValue(p.getNacionalidad() != null ? p.getNacionalidad().getDescripcion() : "");
                row.createCell(6).setCellValue(p.getDepartamentoResidencia() != null ? p.getDepartamentoResidencia().getDescripcion() : "");
                row.createCell(7).setCellValue(p.getMunicipioResidencia() != null ? p.getMunicipioResidencia().getDescripcion() : "");
                row.createCell(8).setCellValue(p.getTipoPersona() != null ? p.getTipoPersona().getDescripcion() : "");
                row.createCell(9).setCellValue(p.getEstadoSalud() != null ? p.getEstadoSalud().getDescripcion() : "");

                // Derechos vulnerados con wrap text
                Cell derechosCell = row.createCell(10);
                String derechos = p.getDerechosVulnerados() != null ?
                        p.getDerechosVulnerados().stream()
                                .map(d -> d.getDerecho() != null ? d.getDerecho().getDescripcion() : "")
                                .filter(s -> !s.isEmpty())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("")
                        : "";
                derechosCell.setCellValue(derechos);
                derechosCell.setCellStyle(wrapStyle);

                // Aplicar centrado a todas las celdas
                for (int i = 0; i < cols.length; i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null) cell = row.createCell(i);
                    cell.setCellStyle(centerStyle);
                }

                // Ajuste dinámico de altura
                ajustarAlturaDinamica(row, sheet);
            }
        }
    }

 // Método genérico para hojas de detalle
    public <T> void crearHojaDetalle(
            SXSSFWorkbook workbook,
            List<RegistroEvento> registros,
            String nombreHoja,
            BiConsumer<Row, PersonaAfectada> rellenarFila) {

        SXSSFSheet sheet = workbook.createSheet(nombreHoja);

        CellStyle headerStyle = crearEstiloHeader(workbook);
        CellStyle centerStyle = crearEstiloCentrado(workbook);

        Row header = sheet.createRow(0);
        header.setHeightInPoints(40);

        String[] cols;
        switch (nombreHoja) {
            case "Violencia":
                cols = new String[]{
                        "Evento ID", "Persona ID", "Es Asesinato", "Tipo Violencia",
                        "Artefacto Utilizado", "Contexto", "Actor Responsable",
                        "Estado Salud Actor Responsable", "Hubo Protección",
                        "Investigación Abierta", "Respuesta Estado"
                };
                break;

            case "Detencion e Integridad":
                cols = new String[]{
                        "Evento ID", "Persona ID", "Tipo Detención", "Orden Judicial",
                        "Autoridad Involucrada", "Hubo Tortura", "Duración (días)",
                        "Acceso a Abogado", "Resultado", "Motivo Detención"
                };
                break;

            case "Expresion y Censura":
                cols = new String[]{
                        "Evento ID", "Persona ID", "Medio Expresión", "Tipo Represión",
                        "Represalias Legales", "Represalias Físicas", "Actor Censor", "Consecuencia"
                };
                break;

            case "Acceso a Justicia":
                cols = new String[]{
                        "Evento ID", "Persona ID", "Tipo Proceso", "Fecha Denuncia",
                        "Tipo Denunciante", "Duración Proceso", "Acceso a Abogado",
                        "Hubo Parcialidad", "Resultado Proceso", "Instancia"
                };
                break;

            default:
                cols = new String[]{};
        }

        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        ajustarColumnas(sheet, nombreHoja);

        int rowIdx = 1;
        for (RegistroEvento evento : registros) {
            if (evento.getPersonasAfectadas() == null) continue;

            for (PersonaAfectada p : evento.getPersonasAfectadas()) {
                Row row = sheet.createRow(rowIdx++);
                rellenarFila.accept(row, p);

                row.createCell(0).setCellValue(evento.getId());
                row.createCell(1).setCellValue(p.getId());

                for (int i = 0; i < cols.length; i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null) cell = row.createCell(i);
                    cell.setCellStyle(centerStyle);
                }

                ajustarAlturaDinamica(row, sheet);
            }
        }
    }

    // Métodos específicos de detalle
    private void llenarFilaViolencia(Row row, PersonaAfectada p) {
        Violencia v = p.getViolencia();
        if (v == null) return;
        row.createCell(2).setCellValue(v.getEsAsesinato() != null && v.getEsAsesinato() ? "SI" : "NO");
        row.createCell(3).setCellValue(v.getTipoViolencia() != null ? v.getTipoViolencia().getDescripcion() : "");
        row.createCell(4).setCellValue(v.getArtefactoUtilizado() != null ? v.getArtefactoUtilizado().getDescripcion() : "");
        row.createCell(5).setCellValue(v.getContexto() != null ? v.getContexto().getDescripcion() : "");
        row.createCell(6).setCellValue(v.getActorResponsable() != null ? v.getActorResponsable().getDescripcion() : "");
        row.createCell(7).setCellValue(v.getEstadoSaludActorResponsable() != null ? v.getEstadoSaludActorResponsable().getDescripcion() : "");
        row.createCell(8).setCellValue(v.getHuboProteccion() != null && v.getHuboProteccion() ? "SI" : "NO");
        row.createCell(9).setCellValue(v.getInvestigacionAbierta() != null && v.getInvestigacionAbierta() ? "SI" : "NO");
        row.createCell(10).setCellValue(v.getRespuestaEstado() != null ? v.getRespuestaEstado() : "");
    }

    private void llenarFilaDetencion(Row row, PersonaAfectada p) {
        DetencionIntegridad d = p.getDetencionIntegridad();
        if (d == null) return;
        row.createCell(2).setCellValue(d.getTipoDetencion() != null ? d.getTipoDetencion().getDescripcion() : "");
        row.createCell(3).setCellValue(d.getOrdenJudicial() != null && d.getOrdenJudicial() ? "SI" : "NO");
        row.createCell(4).setCellValue(d.getAutoridadInvolucrada() != null ? d.getAutoridadInvolucrada().getDescripcion() : "");
        row.createCell(5).setCellValue(d.getHuboTortura() != null && d.getHuboTortura() ? "SI" : "NO");
        row.createCell(6).setCellValue(d.getDuracionDias() != null ? d.getDuracionDias() : 0);
        row.createCell(7).setCellValue(d.getAccesoAbogado() != null && d.getAccesoAbogado() ? "SI" : "NO");
        row.createCell(8).setCellValue(d.getResultado() != null ? d.getResultado() : "");
        row.createCell(9).setCellValue(d.getMotivoDetencion() != null ? d.getMotivoDetencion().getDescripcion() : "");
    }

    private void llenarFilaExpresion(Row row, PersonaAfectada p) {
        ExpresionCensura e = p.getExpresionCensura();
        if (e == null) return;
        row.createCell(2).setCellValue(e.getMedioExpresion() != null ? e.getMedioExpresion().getDescripcion() : "");
        row.createCell(3).setCellValue(e.getTipoRepresion() != null ? e.getTipoRepresion().getDescripcion() : "");
        row.createCell(4).setCellValue(e.getRepresaliasLegales() != null && e.getRepresaliasLegales() ? "SI" : "NO");
        row.createCell(5).setCellValue(e.getRepresaliasFisicas() != null && e.getRepresaliasFisicas() ? "SI" : "NO");
        row.createCell(6).setCellValue(e.getActorCensor() != null ? e.getActorCensor().getDescripcion() : "");
        row.createCell(7).setCellValue(e.getConsecuencia() != null ? e.getConsecuencia() : "");
    }

    private void llenarFilaJusticia(Row row, PersonaAfectada p) {
        AccesoJusticia a = p.getAccesoJusticia();
        if (a == null) return;
        row.createCell(2).setCellValue(a.getTipoProceso() != null ? a.getTipoProceso().getDescripcion() : "");
        row.createCell(3).setCellValue(a.getFechaDenuncia() != null ? sdf.format(a.getFechaDenuncia()) : "");
        row.createCell(4).setCellValue(a.getTipoDenunciante() != null ? a.getTipoDenunciante().getDescripcion() : "");
        row.createCell(5).setCellValue(a.getDuracionProceso() != null ? a.getDuracionProceso().getDescripcion() : "");
        row.createCell(6).setCellValue(a.getAccesoAbogado() != null && a.getAccesoAbogado() ? "SI" : "NO");
        row.createCell(7).setCellValue(a.getHuboParcialidad() != null && a.getHuboParcialidad() ? "SI" : "NO");
        row.createCell(8).setCellValue(a.getResultadoProceso() != null ? a.getResultadoProceso() : "");
        row.createCell(9).setCellValue(a.getInstancia() != null ? a.getInstancia() : "");
    }
}
