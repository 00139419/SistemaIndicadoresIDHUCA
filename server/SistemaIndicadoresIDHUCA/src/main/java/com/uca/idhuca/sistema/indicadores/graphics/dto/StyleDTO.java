package com.uca.idhuca.sistema.indicadores.graphics.dto;

import lombok.Data;

@Data
public class StyleDTO {
	private String   backgroundColor = "#FFFFFF";          // plot bg
    private String[] palette         = {                   // series colors
            "#4e79a7", "#f28e2c", "#e15759",
            "#76b7b2", "#59a14f", "#edc949"
    };
    private int  titleFontSize    = 18;
    private int  subtitleFontSize = 12;
}
