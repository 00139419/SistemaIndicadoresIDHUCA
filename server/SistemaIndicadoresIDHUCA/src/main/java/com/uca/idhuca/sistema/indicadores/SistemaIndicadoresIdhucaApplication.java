package com.uca.idhuca.sistema.indicadores;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaIndicadoresIdhucaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaIndicadoresIdhucaApplication.class, args);
	}

}
