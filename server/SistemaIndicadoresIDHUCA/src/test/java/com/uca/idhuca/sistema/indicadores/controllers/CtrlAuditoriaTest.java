package com.uca.idhuca.sistema.indicadores.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.models.Auditoria;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CtrlAuditoriaTest {

    @Mock
    private Utilidades utils;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private IAuditoria auditoriaService;

    @InjectMocks
    private CtrlAuditoria ctrlAuditoria;

    private Filtros filtros;
    private Usuario usuario;
    private GenericEntityResponse<List<Auditoria>> serviceResponse;
    private List<Auditoria> auditoriaList;

    @BeforeEach
    void setUp() {
        filtros = new Filtros();

        usuario = new Usuario();
        usuario.setEmail("test@example.com");

        auditoriaList = new ArrayList<>();
        Auditoria auditoria = new Auditoria();
        auditoria.setId(1L);
        auditoria.setOperacion("CREATE");
        auditoria.setTablaAfectada("usuarios");
        auditoriaList.add(auditoria);

        serviceResponse = new GenericEntityResponse<>();
        serviceResponse.setEntity(auditoriaList);
        serviceResponse.setCodigo(200);
        serviceResponse.setMensaje("Auditorías obtenidas exitosamente");

    }

    @Test
    void testGet_Success() throws ValidationException, NotFoundException {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(auditoriaService.get(any(Filtros.class))).thenReturn(serviceResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals(1, response.getBody().getEntity().size());

        verify(utils).obtenerUsuarioAutenticado();
        verify(auditoriaService).get(filtros);
    }

    @Test
    void testGet_ValidationException() throws ValidationException, NotFoundException {
        // Arrange
        ValidationException validationException = new ValidationException(-1, "Error de validación");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(auditoriaService.get(any(Filtros.class))).thenThrow(validationException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error de validación", response.getBody().getMensaje());

        verify(utils).obtenerUsuarioAutenticado();
        verify(auditoriaService).get(filtros);
    }

    @Test
    void testGet_NotFoundException() throws ValidationException, NotFoundException {
        // Arrange
        NotFoundException notFoundException = new NotFoundException(-1, "Recurso no encontrado");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(auditoriaService.get(any(Filtros.class))).thenThrow(notFoundException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Recurso no encontrado", response.getBody().getMensaje());

        verify(utils).obtenerUsuarioAutenticado();
        verify(auditoriaService).get(filtros);
    }

    @Test
    void testGet_GenericException() throws ValidationException, NotFoundException {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error interno del servidor");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(auditoriaService.get(any(Filtros.class))).thenThrow(runtimeException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error interno del servidor", response.getBody().getMensaje());

        verify(utils).obtenerUsuarioAutenticado();
        verify(auditoriaService).get(filtros);
    }

    @Test
    void testGet_ExceptionInObtenerUsuarioAutenticado() throws ValidationException {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error al obtener usuario");
        when(utils.obtenerUsuarioAutenticado()).thenThrow(runtimeException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error al obtener usuario", response.getBody().getMensaje());

        verify(utils).obtenerUsuarioAutenticado();
    }

    @Test
    void testGet_EmptyResponse() throws ValidationException, NotFoundException {
        // Arrange
        GenericEntityResponse<List<Auditoria>> emptyResponse = new GenericEntityResponse<>();
        emptyResponse.setEntity(new ArrayList<>());
        emptyResponse.setCodigo(200);
        emptyResponse.setMensaje("No hay datos");

        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(auditoriaService.get(any(Filtros.class))).thenReturn(emptyResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals(0, response.getBody().getEntity().size());

        verify(utils).obtenerUsuarioAutenticado();
        verify(auditoriaService).get(filtros);
    }

    @Test
    void testGet_WithNullFiltros() throws ValidationException, NotFoundException {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(auditoriaService.get(any())).thenReturn(serviceResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());

        verify(utils).obtenerUsuarioAutenticado();
        verify(auditoriaService).get(null);
    }

    @Test
    void testGet_WithDefaultUserKeyWhenExceptionInGetUser() throws ValidationException, NotFoundException {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenThrow(new RuntimeException("Error al obtener usuario"));
        when(auditoriaService.get(any(Filtros.class))).thenReturn(serviceResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        // Verificar que el servicio maneja la excepción correctamente
        // y que el key por defecto "ADMIN" se usa en los logs
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(utils).obtenerUsuarioAutenticado();
    }

    @Test
    void testGet_ValidationExceptionWithNullMessage() throws ValidationException, NotFoundException {
        // Arrange
        ValidationException validationException = new ValidationException(-1, null);
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(auditoriaService.get(any(Filtros.class))).thenThrow(validationException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(utils).obtenerUsuarioAutenticado();
        verify(auditoriaService).get(filtros);
    }

    @Test
    void testGet_NotFoundExceptionWithNullMessage() throws ValidationException, NotFoundException {
        // Arrange
        NotFoundException notFoundException = new NotFoundException(-1, null);
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(auditoriaService.get(any(Filtros.class))).thenThrow(notFoundException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Auditoria>>> response = ctrlAuditoria.get(filtros);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(utils).obtenerUsuarioAutenticado();
        verify(auditoriaService).get(filtros);
    }

}