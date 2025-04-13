package com.uca.idhuca.sistema.indicadores.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ProjectProperties {

	@Value("${spring.datasource.url}")
	private String jdbcUrl;

	@Value("${spring.datasource.username}")
	private String jdbcUsername;

	@Value("${spring.datasource.password}")
	private String jdbcPassword;
	
	@Value("${security.jwt.secret}")
	private String jwtSecret;
	
	@Value("${security.jwt.expiration.time}")
	private String jwtExpirationTime;
}
