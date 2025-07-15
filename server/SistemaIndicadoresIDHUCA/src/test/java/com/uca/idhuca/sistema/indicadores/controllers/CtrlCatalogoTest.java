package com.uca.idhuca.sistema.indicadores.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.services.ICatalogo;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CtrlCatalogoTest {

    @Mock
    private Utilidades utils;

    @Mock
    private ICatalogo catalogoService;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private CtrlCatalogo ctrlCatalogo;

    private CatalogoDto catalogoDto;
    private Usuario usuario;
    private GenericEntityResponse<List<Catalogo>> getResponse;
    private SuperGenericResponse superGenericResponse;
    private List<Catalogo> catalogoList;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        catalogoDto = new CatalogoDto(
                new Catalogo("001", "Descripción de prueba"),
                new Filtros()
        );
        catalogoDto.setCatalogo(new Catalogo("001", "Descripción de prueba"));
        catalogoDto.setDerecho(new Catalogo("002", "Derecho de prueba"));
        catalogoDto.setDepartamentos(true);

        usuario = new Usuario();
        usuario.setEmail("test@example.com");

        // Preparar lista de catálogos
        catalogoList = new ArrayList<>();
        Catalogo catalogo = new Catalogo("001", "Categoría Test");
        catalogoList.add(catalogo);

        // Preparar respuesta de get
        getResponse = new GenericEntityResponse<>();
        getResponse.setCodigo(200);
        getResponse.setMensaje("Catálogos obtenidos exitosamente");
        getResponse.setEntity(catalogoList);

        // Preparar respuesta genérica
        superGenericResponse = new SuperGenericResponse();
        superGenericResponse.setCodigo(200);
        superGenericResponse.setMensaje("Operación exitosa");
    }
    // ========== PRUEBAS PARA GET ==========

    @Test
    void testGet_Success() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any(CatalogoDto.class))).thenReturn("{\"codigo\":\"001\",\"descripcion\":\"Categoría Test\"}");
        when(catalogoService.get(any(CatalogoDto.class))).thenReturn(getResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<Catalogo>>> response = ctrlCatalogo.get(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getResponse, response.getBody());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals(1, response.getBody().getEntity().size());

        verify(utils).obtenerUsuarioAutenticado();
        verify(catalogoService).get(catalogoDto);
        verify(mapper).writeValueAsString(catalogoDto);
    }

    @Test
    void testGet_ValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(400, "Datos inválidos");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        when(catalogoService.get(any(CatalogoDto.class))).thenThrow(validationException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Catalogo>>> response = ctrlCatalogo.get(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getCodigo());
        assertEquals("Datos inválidos", response.getBody().getMensaje());

        verify(catalogoService).get(catalogoDto);
    }

    @Test
    void testGet_NotFoundException() throws Exception {
        // Arrange
        NotFoundException notFoundException = new NotFoundException(404, "Catálogo no encontrado");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        when(catalogoService.get(any(CatalogoDto.class))).thenThrow(notFoundException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Catalogo>>> response = ctrlCatalogo.get(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getCodigo());
        assertEquals("Catálogo no encontrado", response.getBody().getMensaje());

        verify(catalogoService).get(catalogoDto);
    }

    @Test
    void testGet_GenericException() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error interno del servidor");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        when(catalogoService.get(any(CatalogoDto.class))).thenThrow(runtimeException);

        // Act
        ResponseEntity<GenericEntityResponse<List<Catalogo>>> response = ctrlCatalogo.get(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error interno del servidor", response.getBody().getMensaje());

        verify(catalogoService).get(catalogoDto);
    }

    @Test
    void testGet_ExceptionInObtenerUsuario() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenThrow(new RuntimeException("Error al obtener usuario"));
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":1}");

        // Act
        ResponseEntity<GenericEntityResponse<List<Catalogo>>> response = ctrlCatalogo.get(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(utils).obtenerUsuarioAutenticado();
    }

    // ========== PRUEBAS PARA ADD ==========

    @Test
    void testAdd_Success() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"nombre\":\"Nuevo Catálogo\"}");
        when(catalogoService.add(any(CatalogoDto.class))).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.add(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superGenericResponse, response.getBody());
        assertEquals(200, response.getBody().getCodigo());

        verify(utils).obtenerUsuarioAutenticado();
        verify(catalogoService).add(catalogoDto);
        verify(mapper).writeValueAsString(catalogoDto);
    }

    @Test
    void testAdd_ValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(400, "Nombre requerido");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"nombre\":\"\"}");
        when(catalogoService.add(any(CatalogoDto.class))).thenThrow(validationException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.add(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getCodigo());
        assertEquals("Nombre requerido", response.getBody().getMensaje());

        verify(catalogoService).add(catalogoDto);
    }

    @Test
    void testAdd_NotFoundException() throws Exception {
        // Arrange
        NotFoundException notFoundException = new NotFoundException(404, "Recurso no encontrado");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"nombre\":\"Test\"}");
        when(catalogoService.add(any(CatalogoDto.class))).thenThrow(notFoundException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.add(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getCodigo());
        assertEquals("Recurso no encontrado", response.getBody().getMensaje());

        verify(catalogoService).add(catalogoDto);
    }

    @Test
    void testAdd_GenericException() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error interno");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"nombre\":\"Test\"}");
        when(catalogoService.add(any(CatalogoDto.class))).thenThrow(runtimeException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.add(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error interno", response.getBody().getMensaje());

        verify(catalogoService).add(catalogoDto);
    }

    // ========== PRUEBAS PARA UPDATE ==========

    @Test
    void testUpdate_Success() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":1,\"nombre\":\"Catálogo Actualizado\"}");
        when(catalogoService.update(any(CatalogoDto.class))).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.update(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superGenericResponse, response.getBody());
        assertEquals(200, response.getBody().getCodigo());

        verify(utils).obtenerUsuarioAutenticado();
        verify(catalogoService).update(catalogoDto);
        verify(mapper).writeValueAsString(catalogoDto);
    }

    @Test
    void testUpdate_ValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(400, "ID requerido");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":null}");
        when(catalogoService.update(any(CatalogoDto.class))).thenThrow(validationException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.update(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getCodigo());
        assertEquals("ID requerido", response.getBody().getMensaje());

        verify(catalogoService).update(catalogoDto);
    }

    @Test
    void testUpdate_NotFoundException() throws Exception {
        // Arrange
        NotFoundException notFoundException = new NotFoundException(404, "Catálogo no encontrado");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":999}");
        when(catalogoService.update(any(CatalogoDto.class))).thenThrow(notFoundException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.update(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getCodigo());
        assertEquals("Catálogo no encontrado", response.getBody().getMensaje());

        verify(catalogoService).update(catalogoDto);
    }

    @Test
    void testUpdate_GenericException() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error de base de datos");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        when(catalogoService.update(any(CatalogoDto.class))).thenThrow(runtimeException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.update(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error de base de datos", response.getBody().getMensaje());

        verify(catalogoService).update(catalogoDto);
    }

    // ========== PRUEBAS PARA DELETE ==========

    @Test
    void testDelete_Success() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        when(catalogoService.delete(any(CatalogoDto.class))).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.delete(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superGenericResponse, response.getBody());
        assertEquals(200, response.getBody().getCodigo());

        verify(utils).obtenerUsuarioAutenticado();
        verify(catalogoService).delete(catalogoDto);
        verify(mapper).writeValueAsString(catalogoDto);
    }

    @Test
    void testDelete_ValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(400, "ID requerido para eliminar");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":null}");
        when(catalogoService.delete(any(CatalogoDto.class))).thenThrow(validationException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.delete(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getCodigo());
        assertEquals("ID requerido para eliminar", response.getBody().getMensaje());

        verify(catalogoService).delete(catalogoDto);
    }

    @Test
    void testDelete_NotFoundException() throws Exception {
        // Arrange
        NotFoundException notFoundException = new NotFoundException(404, "Catálogo no encontrado");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":999}");
        when(catalogoService.delete(any(CatalogoDto.class))).thenThrow(notFoundException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.delete(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getCodigo());
        assertEquals("Catálogo no encontrado", response.getBody().getMensaje());

        verify(catalogoService).delete(catalogoDto);
    }

    @Test
    void testDelete_GenericException() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error al eliminar");
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        when(catalogoService.delete(any(CatalogoDto.class))).thenThrow(runtimeException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.delete(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error al eliminar", response.getBody().getMensaje());

        verify(catalogoService).delete(catalogoDto);
    }

    // ========== PRUEBAS PARA CASOS EDGE ==========

    @Test
    void testGet_WithNullRequest() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("null");
        when(catalogoService.get(any())).thenReturn(getResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<Catalogo>>> response = ctrlCatalogo.get(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getResponse, response.getBody());

        verify(catalogoService).get(null);
    }

    @Test
    void testAdd_WithNullRequest() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("null");
        when(catalogoService.add(any())).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.add(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superGenericResponse, response.getBody());

        verify(catalogoService).add(null);
    }

    @Test
    void testUpdate_WithNullRequest() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("null");
        when(catalogoService.update(any())).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.update(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superGenericResponse, response.getBody());

        verify(catalogoService).update(null);
    }

    @Test
    void testDelete_WithNullRequest() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenReturn("null");
        when(catalogoService.delete(any())).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.delete(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superGenericResponse, response.getBody());

        verify(catalogoService).delete(null);
    }

    @Test
    void testGet_JsonProcessingException() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});
        when(catalogoService.get(any())).thenReturn(getResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<Catalogo>>> response = ctrlCatalogo.get(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(mapper).writeValueAsString(catalogoDto);
    }

    @Test
    void testAdd_JsonProcessingException() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});
        when(catalogoService.add(any())).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.add(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(mapper).writeValueAsString(catalogoDto);
    }

    @Test
    void testUpdate_JsonProcessingException() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});
        when(catalogoService.update(any())).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.update(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(mapper).writeValueAsString(catalogoDto);
    }

    @Test
    void testDelete_JsonProcessingException() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});
        when(catalogoService.delete(any())).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlCatalogo.delete(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(mapper).writeValueAsString(catalogoDto);
    }
}