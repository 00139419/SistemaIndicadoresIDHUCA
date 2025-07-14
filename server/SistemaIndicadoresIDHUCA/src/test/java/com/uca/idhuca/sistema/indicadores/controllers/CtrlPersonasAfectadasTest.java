package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
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
import com.uca.idhuca.sistema.indicadores.controllers.dto.PersonaAfectadaDTO;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.PersonaAfectada;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.services.IPersonasAfectadas;
import com.uca.idhuca.sistema.indicadores.services.IRegistros;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CtrlPersonasAfectadasTest {

    @Mock
    private Utilidades utils;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private IRegistros registrosServices;

    @Mock
    private IPersonasAfectadas personasaAfectadasServices;

    @InjectMocks
    private CtrlPersonasAfectadas controller;

    private Usuario usuarioMock;
    private CatalogoDto catalogoDto;
    private RegistroEventoDTO registroEventoDto;
    private PersonaAfectadaDTO personaAfectadaDto;
    private GenericEntityResponse<List<PersonaAfectada>> responseListMock;
    private GenericEntityResponse<PersonaAfectada> responseEntityMock;
    private SuperGenericResponse superResponseMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        catalogoDto = new CatalogoDto(new Catalogo(), new Filtros());
        registroEventoDto = new RegistroEventoDTO();
        personaAfectadaDto = new PersonaAfectadaDTO();

        responseListMock = new GenericEntityResponse<>();
        responseListMock.setCodigo(200);
        responseListMock.setMensaje("Success");
        responseListMock.setEntity(List.of(new PersonaAfectada()));

        responseEntityMock = new GenericEntityResponse<>();
        responseEntityMock.setCodigo(200);
        responseEntityMock.setMensaje("Success");
        responseEntityMock.setEntity(new PersonaAfectada());

        superResponseMock = new SuperGenericResponse();
        superResponseMock.setCodigo(200);
        superResponseMock.setMensaje("Success");
    }

    @Test
    void testGetAllByDerecho_Success() throws ValidationException, JsonProcessingException {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(personasaAfectadasServices.getAllByDerecho(any(CatalogoDto.class))).thenReturn(responseListMock);

        // Act
        ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>> response = controller.getAllByDerecho(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals("Success", response.getBody().getMensaje());
    }

    @Test
    void testGetAllByDerecho_ValidationException() throws ValidationException, JsonProcessingException {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(personasaAfectadasServices.getAllByDerecho(any(CatalogoDto.class)))
                .thenThrow(new ValidationException(-1, "Validation error"));

        // Act
        ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>> response = controller.getAllByDerecho(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Validation error", response.getBody().getMensaje());
    }

    @Test
    void testGetAllByDerecho_GenericException() throws ValidationException, JsonProcessingException {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(personasaAfectadasServices.getAllByDerecho(any(CatalogoDto.class)))
                .thenThrow(new RuntimeException("Internal error"));

        // Act
        ResponseEntity<GenericEntityResponse<List<PersonaAfectada>>> response = controller.getAllByDerecho(catalogoDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Internal error", response.getBody().getMensaje());
    }

    @Test
    void testUpdate_Success() throws ValidationException, JsonProcessingException {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(personasaAfectadasServices.update(any(PersonaAfectadaDTO.class))).thenReturn(superResponseMock);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.update(personaAfectadaDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals("Success", response.getBody().getMensaje());
    }

    @Test
    void testUpdate_GenericException() throws ValidationException, JsonProcessingException {
        // Arrange
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(personasaAfectadasServices.update(any(PersonaAfectadaDTO.class)))
                .thenThrow(new RuntimeException("Internal error"));

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.update(personaAfectadaDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Internal error", response.getBody().getMensaje());
    }
}