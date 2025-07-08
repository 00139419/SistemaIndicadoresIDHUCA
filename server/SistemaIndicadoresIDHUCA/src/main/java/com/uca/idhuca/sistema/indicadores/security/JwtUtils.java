package com.uca.idhuca.sistema.indicadores.security;

import io.jsonwebtoken.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.ParametroSistema;
import com.uca.idhuca.sistema.indicadores.services.IParametrosSistema;
import com.uca.idhuca.sistema.indicadores.utils.ProjectProperties;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
	private final ProjectProperties projectProperties;
	
	@Autowired
	IParametrosSistema pSistema;

    public JwtUtils(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    @SuppressWarnings("deprecation")
    public String generateJwtToken(String username) throws ValidationException {
        double timeInHours = Double.parseDouble(projectProperties.getJwtExpirationTime()); // default en horas

        try {
            ParametroSistema tiempoToken = pSistema.getOne("tiempo_de_vida_de_sesion").getEntity();
            double valorHoras = Double.parseDouble(tiempoToken.getValor());
            
            
            System.out.println("tiempoToken " + tiempoToken);
            timeInHours = valorHoras;
        } catch (Exception e) {
            e.printStackTrace();
        }

        long expirationMillis = (long) (timeInHours * 3600_000); // horas a milisegundos

        System.out.println("expirationMillis " + expirationMillis);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
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
