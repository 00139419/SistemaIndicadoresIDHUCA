package com.uca.idhuca.sistema.indicadores.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper(res);

        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String authHeader = request.getHeader("Authorization");

        log.info("=== [Petición Entrante] ===");
        log.info("Método: {}", method);
        log.info("Endpoint: {}", endpoint);
        log.info("IP: {}", ip);
        log.info("User-Agent: {}", userAgent);
        log.info("JWT: {}", authHeader != null ? authHeader : "No JWT");

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String param = parameterNames.nextElement();
            log.info("Parámetro: {} = {}", param, request.getParameter(param));
        }

        filterChain.doFilter(request, response);

        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (!requestBody.isBlank()) {
            log.info("Body de la petición: {}", requestBody);
        }

        log.info("=== [Respuesta] ===");
        log.info("Status: {}", response.getStatus());

        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (!responseBody.isBlank()) {
            log.info("Body de la respuesta: {}", responseBody);
        }

        response.copyBodyToResponse();
    }
}
