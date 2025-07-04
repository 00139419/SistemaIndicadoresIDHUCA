package com.uca.idhuca.sistema.indicadores.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class Ctrl {

    @GetMapping("/")
    public String testConnection() {
        return "API OK";
    }
}