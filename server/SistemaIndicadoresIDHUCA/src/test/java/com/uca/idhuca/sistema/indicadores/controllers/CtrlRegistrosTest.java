package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.filtros.dto.Filtros;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.CatalogoDto;
import com.uca.idhuca.sistema.indicadores.controllers.dto.RegistroEventoDTO;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;
import com.uca.idhuca.sistema.indicadores.services.IRegistros;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CtrlRegistrosTest {

    @Mock
    private Utilidades utils;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private IRegistros registrosServices;

    @InjectMocks
    private CtrlRegistros ctrlRegistros;

    private CatalogoDto catalogoDto;
    private RegistroEventoDTO registroEventoDTO;
    private RegistroEvento registroEvento;
    private List<RegistroEvento> registroEventoList;
    private GenericEntityResponse<List<RegistroEvento>> genericListResponse;
    private GenericEntityResponse<RegistroEvento> genericResponse;
    private SuperGenericResponse superGenericResponse;

    @BeforeEach
    void setUp() {
        catalogoDto = new CatalogoDto(new Catalogo(), new Filtros());
        registroEventoDTO = new RegistroEventoDTO();
        registroEvento = new RegistroEvento();
        registroEventoList = new ArrayList<>();
        registroEventoList.add(registroEvento);

        genericListResponse = new GenericEntityResponse<>();
        genericListResponse.setCodigo(200);
        genericListResponse.setMensaje("SUCCESS");
        genericListResponse.setEntity(registroEventoList);

        genericResponse = new GenericEntityResponse<>();
        genericResponse.setCodigo(200);
        genericResponse.setMensaje("SUCCESS");
        genericResponse.setEntity(registroEvento);

        superGenericResponse = new SuperGenericResponse();
        superGenericResponse.setCodigo(200);
        superGenericResponse.setMensaje("Operación exitosa");
    }

    @Test
    void testGetAllByDerecho_Success() throws Exception {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);;

        when(registrosServices.getAllByDerecho(any(CatalogoDto.class))).thenReturn(genericListResponse);

        ResponseEntity<GenericEntityResponse<List<RegistroEvento>>> response = ctrlRegistros.getAll(catalogoDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals("SUCCESS", response.getBody().getMensaje());
        assertEquals(1, response.getBody().getEntity().size());

        verify(utils).obtenerUsuarioAutenticado();
        verify(registrosServices).getAllByDerecho(catalogoDto);
    }

    @Test
    void testGetAllByDerecho_ValidationException() throws Exception {
        String errorMessage = "Error de validación";
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);;

        when(registrosServices.getAllByDerecho(any(CatalogoDto.class))).thenThrow(new ValidationException(-1, errorMessage));

        ResponseEntity<GenericEntityResponse<List<RegistroEvento>>> response = ctrlRegistros.getAll(catalogoDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals(errorMessage, response.getBody().getMensaje());

        verify(registrosServices).getAllByDerecho(catalogoDto);
    }

    @Test
    void testGetAllByDerecho_GenericException() throws Exception {
        String errorMessage = "Error interno del servidor";
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);;

        when(registrosServices.getAllByDerecho(any(CatalogoDto.class))).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<GenericEntityResponse<List<RegistroEvento>>> response = ctrlRegistros.getAll(catalogoDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals(errorMessage, response.getBody().getMensaje());

        verify(registrosServices).getAllByDerecho(catalogoDto);
    }

    @Test
    void testAddEvento_Success() throws Exception {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);;

        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(registrosServices.addEvento(any(RegistroEventoDTO.class))).thenReturn(superGenericResponse);

        ResponseEntity<SuperGenericResponse> response = ctrlRegistros.addEvento(registroEventoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals("Operación exitosa", response.getBody().getMensaje());

        verify(utils).obtenerUsuarioAutenticado();
        verify(mapper).writeValueAsString(registroEventoDTO);
        verify(registrosServices).addEvento(registroEventoDTO);
    }

}