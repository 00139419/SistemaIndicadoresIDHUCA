package com.uca.idhuca.sistema.indicadores.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UsuarioSimple {
    private Long id;
    private String email; 
    private String nombre;
}