package com.uca.idhuca.sistema.indicadores.graphics;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest.ChartType;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsResponseDTO;
import com.uca.idhuca.sistema.indicadores.graphics.dto.SeriesDTO;
import com.uca.idhuca.sistema.indicadores.graphics.dto.StyleDTO;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ChartFactory;
import org.jfree.util.Rotation;


import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GraphicsGeneratorService {

    /* ===========================================================
     *  API PÚBLICA
     * ========================================================= */
	public GraphicsResponseDTO generate(GraphicsRequest req) {
	    JFreeChart chart;

	    if (req.getChartType() == ChartType.PIE) {
	        chart = buildPieChart(req);
	    } else {
	        DefaultCategoryDataset ds = buildCategoryDataset(req.getSeries());
	        chart = buildCategoryChart(req, ds);
	    }

	    String base64 = chartToBase64(chart, req.getWidth(), req.getHeight());
	    return new GraphicsResponseDTO(base64);
	}


    /* ===========================================================
     *  DATASET
     * ========================================================= */
    private DefaultCategoryDataset buildCategoryDataset(List<SeriesDTO> seriesList) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (SeriesDTO serie : seriesList) {
            serie.getData().forEach((category, value) ->
                ds.addValue(value, serie.getName(), category)
            );
        }
        return ds;
    }
    
    private JFreeChart buildPieChart(GraphicsRequest req) {
    	NumberFormat valueFormat = NumberFormat.getNumberInstance();
    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
    	percentFormat.setMinimumFractionDigits(1);
    	percentFormat.setMaximumFractionDigits(1);

    	
        SeriesDTO serie = req.getSeries().get(0); // solo se usa una serie
        DefaultPieDataset ds = new DefaultPieDataset();
        serie.getData().forEach(ds::setValue);

        JFreeChart chart;
        if (req.isThreeD()) {
            chart = ChartFactory.createPieChart3D(
                req.getTitle(),
                ds,
                req.isShowLegend(),
                true,
                false
            );
            PiePlot3D plot = (PiePlot3D) chart.getPlot();
            plot.setStartAngle(290);
            plot.setDirection(Rotation.CLOCKWISE);
            plot.setForegroundAlpha(0.6f);
            plot.setLabelGenerator(
            	    new StandardPieSectionLabelGenerator(
            	        "{0}: {1} ({2})",
            	        new DecimalFormat("#,##0"),
            	        new DecimalFormat("0.00%")
            	    )
            	);
            plot.setBackgroundPaint(Color.decode(req.getStyle().getBackgroundColor()));
        } else {
            chart = ChartFactory.createPieChart(
                req.getTitle(),
                ds,
                req.isShowLegend(),
                true,
                false
            );
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.decode(req.getStyle().getBackgroundColor()));
        }

        // Subtítulo
        if (req.getSubtitle() != null && !req.getSubtitle().isBlank()) {
            Font subtitleFont = new Font("SansSerif", Font.PLAIN, req.getStyle().getSubtitleFontSize());
            chart.addSubtitle(new TextTitle(req.getSubtitle(), subtitleFont));
        }

        // Antialias y fuente del título
        chart.setAntiAlias(true);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, req.getStyle().getTitleFontSize()));

        return chart;
    }


    /* ===========================================================
     *  CHART
     * ========================================================= */
    private JFreeChart buildCategoryChart(GraphicsRequest req, DefaultCategoryDataset ds) {

        CategoryAxis domain = new CategoryAxis(req.getCategoryAxisLabel());
        NumberAxis   range  = new NumberAxis(req.getValueAxisLabel());
        CategoryItemRenderer renderer;

        /* ---------- Selección del renderer ---------- */
        switch (req.getChartType()) {

            case BAR:
                renderer = req.isThreeD()
                        ? new BarRenderer3D()             
                        : new BarRenderer();
                break;

            case STACKED_BAR:
                renderer = req.isThreeD()
                        ? new StackedBarRenderer3D()      
                        : new StackedBarRenderer();
                break;

            case LINE:
                renderer = new LineAndShapeRenderer();
                break;

            case AREA:
                renderer = new AreaRenderer();
                break;

            case STACKED_AREA:
                renderer = new StackedAreaRenderer();
                break;

            default:
                throw new IllegalArgumentException("Tipo de gráfico no soportado: " + req.getChartType());
        }

        /* ---------- Plot ---------- */
        CategoryPlot plot = new CategoryPlot(ds, domain, range, renderer);

        /* ---------- Estilo ---------- */
        StyleDTO s = Optional.ofNullable(req.getStyle()).orElse(new StyleDTO());

        // fondo y rejilla
        plot.setBackgroundPaint(Color.decode(s.getBackgroundColor()));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(true);

//        // paleta por serie
//        for (int i = 0; i < s.getPalette().length && i < ds.getRowCount(); i++) {
//            renderer.setSeriesPaint(i, Color.decode(s.getPalette()[i]));
//        }

        /* ---------- Creación final ---------- */
        Font titleFont    = new Font("SansSerif", Font.BOLD,   s.getTitleFontSize());
        Font subtitleFont = new Font("SansSerif", Font.PLAIN,  s.getSubtitleFontSize());

        JFreeChart chart = new JFreeChart(req.getTitle(), titleFont, plot, req.isShowLegend());

        // sub-título
        if (req.getSubtitle() != null && !req.getSubtitle().isBlank()) {
            chart.addSubtitle(new TextTitle(req.getSubtitle(), subtitleFont));
        }

        // antialias global
        chart.setAntiAlias(true);

        return chart;
    }

    /* ===========================================================
     *  PNG → Base64
     * ========================================================= */
    private String chartToBase64(JFreeChart chart, int w, int h) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage img = chart.createBufferedImage(w, h);
            ImageIO.write(img, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException("Error al generar gráfico", ex);
        }
    }
}