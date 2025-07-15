package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.services.IUser;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CtrlUsersTest {

    @Mock
    private Utilidades utils;

    @Mock
    private IUser userServices;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private CtrlUsers ctrlUsers;

    private Usuario mockUser;
    private UserDto userDto;
    private List<Usuario> userList;
    private GenericEntityResponse<Usuario> genericUserResponse;
    private GenericEntityResponse<List<Usuario>> genericListResponse;
    private SuperGenericResponse superGenericResponse;

    @BeforeEach
    void setUp() {
        mockUser = new Usuario();
        mockUser.setEmail("test@example.com");

        userDto = new UserDto("Test User", "test@example.com");

        userList = new ArrayList<>();
        userList.add(mockUser);

        genericUserResponse = new GenericEntityResponse<>();
        genericUserResponse.setCodigo(OK);
        genericUserResponse.setMensaje("Success");
        genericUserResponse.setEntity(mockUser);

        genericListResponse = new GenericEntityResponse<>();
        genericListResponse.setCodigo(OK);
        genericListResponse.setMensaje("Success");
        genericListResponse.setEntity(userList);

        superGenericResponse = new SuperGenericResponse();
        superGenericResponse.setCodigo(OK);
        superGenericResponse.setMensaje("Operation successful");
    }

    @Test
    void testGetCurrent_Success() throws ValidationException {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);;

        ResponseEntity<GenericEntityResponse<Usuario>> response = ctrlUsers.getCurrent();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OK, response.getBody().getCodigo());
        assertEquals(mockUser, response.getBody().getEntity());

        verify(utils).obtenerUsuarioAutenticado();
    }

    @Test
    void testGetCurrent_Exception() throws ValidationException {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);;

        ResponseEntity<GenericEntityResponse<Usuario>> response = ctrlUsers.getCurrent();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCodigo());
        assertEquals("Data obtenida correctamente", response.getBody().getMensaje());

        verify(utils).obtenerUsuarioAutenticado();
    }

    @Test
    void testGetAll_Success() throws Exception {
        when(utils.obtenerUsuarioAutenticado()).thenReturn(mockUser);
        when(userServices.getAll()).thenReturn(genericListResponse);

        ResponseEntity<GenericEntityResponse<List<Usuario>>> response = ctrlUsers.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OK, response.getBody().getCodigo());
        assertEquals(userList, response.getBody().getEntity());

        verify(utils).obtenerUsuarioAutenticado();
        verify(userServices).getAll();
    }

    @Test
    void testGetAll_ValidationException() throws Exception {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);;
        when(userServices.getAll()).thenThrow(new ValidationException(-1, "Validation failed"));

        ResponseEntity<GenericEntityResponse<List<Usuario>>> response = ctrlUsers.getAll();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Validation failed", response.getBody().getMensaje());

        verify(userServices).getAll();
    }

    @Test
    void testGetAll_GenericException() throws Exception {
        when(utils.obtenerUsuarioAutenticado()).thenReturn(mockUser);
        when(userServices.getAll()).thenThrow(new RuntimeException("Internal error"));

        ResponseEntity<GenericEntityResponse<List<Usuario>>> response = ctrlUsers.getAll();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Internal error", response.getBody().getMensaje());

        verify(userServices).getAll();
    }

    @Test
    void testAddUser_Success() throws Exception {
        when(utils.obtenerUsuarioAutenticado()).thenReturn(mockUser);
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(userServices.add(any(UserDto.class))).thenReturn(superGenericResponse);

        ResponseEntity<SuperGenericResponse> response = ctrlUsers.addUser(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OK, response.getBody().getCodigo());

        verify(utils).obtenerUsuarioAutenticado();
        verify(mapper).writeValueAsString(userDto);
        verify(userServices).add(userDto);
    }

    @Test
    void testAddUser_ValidationException() throws Exception {
        when(utils.obtenerUsuarioAutenticado()).thenReturn(mockUser);
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(userServices.add(any(UserDto.class))).thenThrow(new ValidationException(-1, "Invalid data"));

        ResponseEntity<SuperGenericResponse> response = ctrlUsers.addUser(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Invalid data", response.getBody().getMensaje());

        verify(userServices).add(userDto);
    }

    @Test
    void testAddUser_JsonProcessingException() throws Exception {
        when(utils.obtenerUsuarioAutenticado()).thenReturn(mockUser);
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON Error") {});

        ResponseEntity<SuperGenericResponse> response = ctrlUsers.addUser(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ERROR, response.getBody().getCodigo());

        verify(mapper).writeValueAsString(userDto);
        verify(userServices, never()).add(any());
    }
}