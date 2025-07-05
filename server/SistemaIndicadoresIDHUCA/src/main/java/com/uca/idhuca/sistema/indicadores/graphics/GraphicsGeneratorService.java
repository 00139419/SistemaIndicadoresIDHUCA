package com.uca.idhuca.sistema.indicadores.graphics;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
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
	    } else if (req.getChartType() == ChartType.AREA || req.getChartType() == ChartType.STACKED_AREA) {
	    	DefaultCategoryDataset ds = buildCategoryDataset(req.getSeries());
            chart = buildAreaChart(req, ds);
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
    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
    	percentFormat.setMinimumFractionDigits(1);
    	percentFormat.setMaximumFractionDigits(1);

    	
        SeriesDTO serie = req.getSeries().get(0);
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
    
    private void logDataset(DefaultCategoryDataset ds) {
        System.out.println("=== Dataset Info ===");

        List<?> rowKeys = ds.getRowKeys();
        List<?> columnKeys = ds.getColumnKeys();

        System.out.println("Series (row keys):");
        for (Object rowKey : rowKeys) {
            System.out.println("  " + rowKey);
        }

        System.out.println("Categories (column keys):");
        for (Object colKey : columnKeys) {
            System.out.println("  " + colKey);
        }

        System.out.println("Values:");
        for (Object rowKey : rowKeys) {
            for (Object colKey : columnKeys) {
                Number value = ds.getValue((Comparable<?>) rowKey, (Comparable<?>) colKey);
                System.out.println("  (" + rowKey + ", " + colKey + ") = " + value);
            }
        }
    }



    @SuppressWarnings("serial")
    private JFreeChart buildCategoryChart(GraphicsRequest req, DefaultCategoryDataset ds) {
        CategoryAxis domain = new CategoryAxis(req.getCategoryAxisLabel());

        logDataset(ds);

        // Rotar etiquetas si son largas
        @SuppressWarnings("unchecked")
        boolean shouldRotate = ds.getColumnKeys().stream()
            .anyMatch(key -> key != null && key.toString().length() > 10);
        if (shouldRotate) {
            domain.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        }

        NumberAxis range = new NumberAxis(req.getValueAxisLabel());
        CategoryItemRenderer renderer;

        String[] palette = {
            "#4e79a7", "#f28e2b", "#e15759", "#76b7b2",
            "#59a14f", "#edc948", "#b07aa1", "#ff9da7",
            "#9c755f", "#bab0ab"
        };

        switch (req.getChartType()) {
            case BAR:
                if (req.isThreeD()) {
                    renderer = new BarRenderer3D() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = column % palette.length; // color por categoría
                            return Color.decode(palette[index]);
                        }
                    };
                } else {
                    renderer = new BarRenderer() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = column % palette.length; // color por categoría
                            return Color.decode(palette[index]);
                        }
                    };
                }
                break;

            case STACKED_BAR:
                if (req.isThreeD()) {
                    renderer = new StackedBarRenderer3D() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = column % palette.length; // color por categoría
                            return Color.decode(palette[index]);
                        }
                    };
                } else {
                    renderer = new StackedBarRenderer() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = column % palette.length; // color por categoría
                            return Color.decode(palette[index]);
                        }
                    };
                }
                break;

            case LINE:
                if (req.isThreeD()) {
                    // No hay LineAndShapeRenderer3D, usamos normal
                    renderer = new LineAndShapeRenderer() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = row % palette.length; // color por serie
                            return Color.decode(palette[index]);
                        }
                    };
                } else {
                    renderer = new LineAndShapeRenderer() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = row % palette.length; // color por serie
                            return Color.decode(palette[index]);
                        }
                    };
                }
                break;

            default:
                throw new IllegalArgumentException("Tipo de gráfico no soportado en buildCategoryChart: " + req.getChartType());
        }

        StyleDTO s = Optional.ofNullable(req.getStyle()).orElse(new StyleDTO());
        String backgroundColor = s.getBackgroundColor() != null ? s.getBackgroundColor() : "#ffffff";

        CategoryPlot plot = new CategoryPlot(ds, domain, range, renderer);
        plot.setBackgroundPaint(Color.decode(backgroundColor));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(true);

        Font titleFont = new Font("SansSerif", Font.BOLD, s.getTitleFontSize());
        Font subtitleFont = new Font("SansSerif", Font.PLAIN, s.getSubtitleFontSize());

        boolean showLegend = req.isShowLegend() && ds.getRowCount() > 1;

        JFreeChart chart = new JFreeChart(req.getTitle(), titleFont, plot, showLegend);

        if (req.getSubtitle() != null && !req.getSubtitle().isBlank()) {
            chart.addSubtitle(new TextTitle(req.getSubtitle(), subtitleFont));
        }

        chart.setAntiAlias(true);

        return chart;
    }

    private JFreeChart buildAreaChart(GraphicsRequest req, DefaultCategoryDataset dsOriginal) {
        CategoryAxis domain = new CategoryAxis(req.getCategoryAxisLabel());

        @SuppressWarnings("unchecked")
        boolean shouldRotate = dsOriginal.getColumnKeys().stream()
            .anyMatch(key -> key != null && key.toString().length() > 10);
        if (shouldRotate) {
            domain.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        }

        NumberAxis range = new NumberAxis(req.getValueAxisLabel());

        // Usar el dataset original, NO modificar la estructura
        DefaultCategoryDataset ds = dsOriginal;

        // Paleta de colores
        String[] palette = {
            "#4e79a7", "#f28e2b", "#e15759", "#76b7b2",
            "#59a14f", "#edc948", "#b07aa1", "#ff9da7",
            "#9c755f", "#bab0ab"
        };

        CategoryItemRenderer renderer;

        if (req.getChartType() == ChartType.AREA) {
            renderer = new AreaRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    int index = column % palette.length;
                    return Color.decode(palette[index]);
                }
            };
        } else if (req.getChartType() == ChartType.STACKED_AREA) {
            renderer = new StackedAreaRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    int index = column % palette.length;
                    return Color.decode(palette[index]);
                }
            };
        } else {
            throw new IllegalArgumentException("Tipo de gráfico no soportado en buildAreaChart: " + req.getChartType());
        }

        StyleDTO s = Optional.ofNullable(req.getStyle()).orElse(new StyleDTO());
        String backgroundColor = s.getBackgroundColor() != null ? s.getBackgroundColor() : "#ffffff";

        CategoryPlot plot = new CategoryPlot(ds, domain, range, renderer);
        plot.setBackgroundPaint(Color.decode(backgroundColor));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(true);

        Font titleFont = new Font("SansSerif", Font.BOLD, s.getTitleFontSize());
        Font subtitleFont = new Font("SansSerif", Font.PLAIN, s.getSubtitleFontSize());

        boolean showLegend = req.isShowLegend() && ds.getRowCount() > 1;

        JFreeChart chart = new JFreeChart(req.getTitle(), titleFont, plot, showLegend);

        if (req.getSubtitle() != null && !req.getSubtitle().isBlank()) {
            chart.addSubtitle(new TextTitle(req.getSubtitle(), subtitleFont));
        }

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