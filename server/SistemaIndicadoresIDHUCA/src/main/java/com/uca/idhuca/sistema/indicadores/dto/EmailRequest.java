package com.uca.idhuca.sistema.indicadores.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    private String destinatario;
    private String asunto;
    private String mensaje;
}
