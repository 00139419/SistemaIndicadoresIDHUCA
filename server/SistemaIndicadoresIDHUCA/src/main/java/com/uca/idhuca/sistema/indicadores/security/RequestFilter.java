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

@Slf4j
@Component
public class RequestFilter extends OncePerRequestFilter {

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

        log.info("===========================================================================");
        log.info("=========================== [Petición Entrante] ==========================");
        log.info("Método: {}", method);
        log.info("Endpoint: {}", endpoint);
        log.info("IP: {}", ip);
        log.info("User-Agent: {}", userAgent);
        log.info("JWT: {}", authHeader != null ? authHeader : "No JWT");

        filterChain.doFilter(request, response);

        log.info("============================== [Respuesta] ===============================");
        log.info("Status: {}", response.getStatus());

        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (!responseBody.isBlank()) {
            log.info("Body de la respuesta: {}", responseBody);
        }
        
        log.info("===========================================================================");

        response.copyBodyToResponse();
    }
}
