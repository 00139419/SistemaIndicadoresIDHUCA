package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
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
import com.uca.idhuca.sistema.indicadores.controllers.dto.CreateGraphicsDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsResponseDTO;
import com.uca.idhuca.sistema.indicadores.services.IGraphics;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CtrlGraficosTest {

    @Mock
    private Utilidades utils;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private IGraphics graphicsServices;

    @InjectMocks
    private CtrlGraficos controller;

    private UserDto usuarioMock;
    private CreateGraphicsDto createGraphicsRequest;
    private GenericEntityResponse<GraphicsResponseDTO> successResponse;
    private GraphicsResponseDTO graphicsResponseDTO;

    @BeforeEach
    void setUp() {
        usuarioMock = new UserDto("Test", "test@example.com");

        createGraphicsRequest = new CreateGraphicsDto();
        createGraphicsRequest.setDerecho(new Catalogo()); // Asignación directa
        createGraphicsRequest.setGraphicsSettings(new GraphicsRequest());
        createGraphicsRequest.setFiltros(new Filtros());
        createGraphicsRequest.setCategoriaEjeX(new Filtros());

        graphicsResponseDTO = new GraphicsResponseDTO("base64EncodedString");

        successResponse = new GenericEntityResponse<>();
        successResponse.setCodigo(200);
        successResponse.setMensaje("Gráfico generado exitosamente");
        successResponse.setEntity(graphicsResponseDTO);
    }

    // ========== TESTS PARA MÉTODO GENERATE ==========



    @Test
    void generate_DeberiaRetornarInternalServerError_CuandoOcurreExcepcionGenerica() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error interno del servidor");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);;
        when(graphicsServices.generate(createGraphicsRequest)).thenThrow(runtimeException);

        // Act
        ResponseEntity<GenericEntityResponse<GraphicsResponseDTO>> response =
                controller.getAll(createGraphicsRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Error interno del servidor", response.getBody().getMensaje());
        assertNull(response.getBody().getEntity());

        verify(graphicsServices).generate(createGraphicsRequest);
        verify(utils).obtenerUsuarioAutenticado();
    }

    @Test
    void generate_DeberiaUsarUsuarioADMIN_CuandoNoHayUsuarioAutenticado() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenThrow(new RuntimeException("No hay usuario autenticado"));
        when(graphicsServices.generate(createGraphicsRequest)).thenReturn(successResponse);

        // Act
        ResponseEntity<GenericEntityResponse<GraphicsResponseDTO>> response =
                controller.getAll(createGraphicsRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("No hay usuario autenticado", response.getBody().getMensaje());

        verify(utils).obtenerUsuarioAutenticado();
        // No se debe llamar al servicio si falla obtener usuario
        verify(graphicsServices, never()).generate(any());
    }

    // ========== TESTS PARA DIFERENTES TIPOS DE GRÁFICOS ==========

    @Test
    void generate_DeberiaGenerarGraficoPie_CuandoTipoEsPIE() throws Exception {
        // Arrange
        createGraphicsRequest.setGraphicsSettings(new GraphicsRequest());

        GraphicsResponseDTO pieGraphResponse = new GraphicsResponseDTO("base64EncodedString");

        GenericEntityResponse<GraphicsResponseDTO> pieResponse = new GenericEntityResponse<>();
        pieResponse.setCodigo(200);
        pieResponse.setMensaje("Gráfico circular generado exitosamente");
        pieResponse.setEntity(pieGraphResponse);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(graphicsServices.generate(createGraphicsRequest)).thenReturn(pieResponse);

        // Act
        ResponseEntity<GenericEntityResponse<GraphicsResponseDTO>> response =
                controller.getAll(createGraphicsRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gráfico circular generado exitosamente", response.getBody().getMensaje());

        verify(graphicsServices).generate(createGraphicsRequest);
    }

    // ========== TESTS PARA VALIDACIONES ESPECÍFICAS ==========

    @Test
    void generate_DeberiaRetornarBadRequest_CuandoIndicadorNoExiste() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(-1, "El indicador especificado no existe");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(graphicsServices.generate(createGraphicsRequest)).thenThrow(validationException);

        // Act
        ResponseEntity<GenericEntityResponse<GraphicsResponseDTO>> response =
                controller.getAll(createGraphicsRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("El indicador especificado no existe", response.getBody().getMensaje());

        verify(graphicsServices).generate(createGraphicsRequest);
    }

    @Test
    void generate_DeberiaRetornarBadRequest_CuandoRangoFechasEsInvalido() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(-1, "Rango de fechas inválido");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(graphicsServices.generate(createGraphicsRequest)).thenThrow(validationException);

        // Act
        ResponseEntity<GenericEntityResponse<GraphicsResponseDTO>> response =
                controller.getAll(createGraphicsRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Rango de fechas inválido", response.getBody().getMensaje());

        verify(graphicsServices).generate(createGraphicsRequest);
    }


    // ========== TESTS PARA CASOS EDGE ==========

    @Test
    void generate_DeberiaRetornarOK_CuandoNoHayDatos() throws Exception {
        // Arrange
        GraphicsResponseDTO emptyGraphResponse = new GraphicsResponseDTO("base64EncodedString");

        GenericEntityResponse<GraphicsResponseDTO> emptyResponse = new GenericEntityResponse<>();
        emptyResponse.setCodigo(200);
        emptyResponse.setMensaje("No hay datos para el período especificado");
        emptyResponse.setEntity(emptyGraphResponse);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(graphicsServices.generate(createGraphicsRequest)).thenReturn(emptyResponse);

        // Act
        ResponseEntity<GenericEntityResponse<GraphicsResponseDTO>> response =
                controller.getAll(createGraphicsRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals("No hay datos para el período especificado", response.getBody().getMensaje());


        verify(graphicsServices).generate(createGraphicsRequest);
    }

    // ========== TESTS PARA VERIFICAR LOGGING ==========

    @Test
    void generate_DeberiaLoggearCorrectamente_CuandoOperacionEsExitosa() throws Exception {
        // Arrange
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(graphicsServices.generate(createGraphicsRequest)).thenReturn(successResponse);

        // Act
        ResponseEntity<GenericEntityResponse<GraphicsResponseDTO>> response =
                controller.getAll(createGraphicsRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verificar que se obtiene el usuario para logging
        verify(utils).obtenerUsuarioAutenticado();
        verify(graphicsServices).generate(createGraphicsRequest);
    }

    @Test
    void generate_DeberiaLoggearConUsuarioADMIN_CuandoFallaObtenerUsuario() throws Exception {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenThrow(new RuntimeException("Usuario no autenticado"));
        when(graphicsServices.generate(createGraphicsRequest)).thenReturn(successResponse);

        // Act
        ResponseEntity<GenericEntityResponse<GraphicsResponseDTO>> response =
                controller.getAll(createGraphicsRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Verificar que se intentó obtener el usuario
        verify(utils).obtenerUsuarioAutenticado();
        // No se debe llamar al servicio si falla obtener usuario
        verify(graphicsServices, never()).generate(any());
    }
}