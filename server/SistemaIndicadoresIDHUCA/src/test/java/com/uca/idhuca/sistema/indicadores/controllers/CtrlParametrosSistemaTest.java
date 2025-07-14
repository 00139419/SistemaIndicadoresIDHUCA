package com.uca.idhuca.sistema.indicadores.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.ParametrosSistemaDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.models.ParametroSistema;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.services.IParametrosSistema;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CtrlParametrosSistemaTest {

    @Mock
    private Utilidades utils;

    @Mock
    private IParametrosSistema sistemaService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CtrlParametrosSistema controller;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setEmail("test@email.com");
    }

    @Test
    void testGetAllEndpoint() throws Exception {
        // Arrange
        ParametroSistema parametro = new ParametroSistema();
        parametro.setClave("TEST_PARAM");
        parametro.setValor("TEST_VALUE");

        GenericEntityResponse<List<ParametroSistema>> response = new GenericEntityResponse<>();
        response.setCodigo(200);
        response.setEntity(List.of(parametro));

        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(sistemaService.getAll()).thenReturn(response);

        // Act
        var result = controller.get();

        // Assert
        assertEquals(200, result.getBody().getCodigo());
        assertNotNull(result.getBody().getEntity());
        assertEquals("TEST_PARAM", result.getBody().getEntity().get(0).getClave());
        verify(utils).obtenerUsuarioAutenticado();
        verify(sistemaService).getAll();
    }

    @Test
    void testGetOneEndpoint() throws Exception {
        // Arrange
        ParametroSistema parametro = new ParametroSistema();
        parametro.setClave("TEST_PARAM");
        parametro.setValor("TEST_VALUE");

        GenericEntityResponse<ParametroSistema> response = new GenericEntityResponse<>();
        response.setCodigo(200);
        response.setEntity(parametro);

        ParametrosSistemaDto request = new ParametrosSistemaDto();
        request.setClave("TEST_PARAM");

        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(sistemaService.getOne("TEST_PARAM")).thenReturn(response);

        // Act
        var result = controller.getOne(request);

        // Assert
        assertEquals(200, result.getBody().getCodigo());
        assertNotNull(result.getBody().getEntity());
        assertEquals("TEST_PARAM", result.getBody().getEntity().getClave());
        verify(utils).obtenerUsuarioAutenticado();
        verify(sistemaService).getOne("TEST_PARAM");
    }

    @Test
    void testUpdateEndpoint() throws Exception {
        // Arrange
        SuperGenericResponse response = new SuperGenericResponse();
        response.setCodigo(200);

        ParametrosSistemaDto request = new ParametrosSistemaDto();
        request.setClave("TEST_PARAM");
        request.setValor("NEW_VALUE");

        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuario);
        when(sistemaService.update(any(ParametrosSistemaDto.class))).thenReturn(response);

        // Act
        var result = controller.update(request);

        // Assert
        assertEquals(200, result.getBody().getCodigo());
        verify(utils).obtenerUsuarioAutenticado();
        verify(sistemaService).update(any(ParametrosSistemaDto.class));
    }
}