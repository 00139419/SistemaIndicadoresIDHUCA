package com.uca.idhuca.sistema.indicadores.security;

import io.jsonwebtoken.*;

import org.springframework.stereotype.Component;

import com.uca.idhuca.sistema.indicadores.utils.ProjectProperties;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
	private final ProjectProperties projectProperties;

    public JwtUtils(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    @SuppressWarnings("deprecation")
	public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Integer.parseInt(projectProperties.getJwtExpirationTime())))
                .signWith(SignatureAlgorithm.HS512, projectProperties.getJwtSecret())
                .compact();
    }

    @SuppressWarnings("deprecation")
	public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(projectProperties.getJwtSecret())
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("deprecation")
	public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(projectProperties.getJwtSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
	public List<String> getRolesFromJwtToken(String token) {
        return (List<String>) Jwts.parser()
                .setSigningKey(projectProperties.getJwtSecret())
                .parseClaimsJws(token)
                .getBody()
                .get("roles");
    }
}
