package com.uca.idhuca.sistema.indicadores.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.Jwt;
import com.uca.idhuca.sistema.indicadores.dto.LoginDto;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.services.IAuth;
import com.uca.idhuca.sistema.indicadores.services.IUser;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CtrlAuthTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private IUser userService;

    @Mock
    private IAuth authService;

    @InjectMocks
    private CtrlAuth ctrlAuth;

    private LoginDto loginDto;
    private UserDto userDto;
    private Jwt jwt;
    private GenericEntityResponse<Jwt> loginResponse;
    private SuperGenericResponse superGenericResponse;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123");

        userDto = new UserDto("Test User", "test@example.com");
        userDto.setEmail("test@example.com");
        userDto.setNombre("Test User");

        jwt = new Jwt("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        jwt.setJwt("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        loginResponse = new GenericEntityResponse<>();
        loginResponse.setCodigo(200);
        loginResponse.setMensaje("Login exitoso");
        loginResponse.setEntity(jwt);

        superGenericResponse = new SuperGenericResponse();
        superGenericResponse.setCodigo(200);
        superGenericResponse.setMensaje("Operación exitosa");
    }

    // ========== PRUEBAS PARA LOGIN ==========

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":\"test@example.com\"}");
        when(authService.login(any(LoginDto.class))).thenReturn(loginResponse);

        // Act
        ResponseEntity<GenericEntityResponse<Jwt>> response = ctrlAuth.login(loginDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
        assertEquals(200, response.getBody().getCodigo());
        assertEquals(jwt, response.getBody().getEntity());

        verify(authService).login(loginDto);
        verify(mapper).writeValueAsString(loginDto);
    }


    @Test
    void testLogin_GenericException() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error interno del servidor");
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":\"test@example.com\"}");
        when(authService.login(any(LoginDto.class))).thenThrow(runtimeException);

        // Act
        ResponseEntity<GenericEntityResponse<Jwt>> response = ctrlAuth.login(loginDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error interno del servidor", response.getBody().getMensaje());

        verify(authService).login(loginDto);
    }

    @Test
    void testLogin_WithNullEmail() throws Exception {
        // Arrange
        loginDto.setEmail(null);
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":null}");
        when(authService.login(any(LoginDto.class))).thenReturn(loginResponse);

        // Act
        ResponseEntity<GenericEntityResponse<Jwt>> response = ctrlAuth.login(loginDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());

        verify(authService).login(loginDto);
    }

    @Test
    void testLogin_JsonProcessingException() throws Exception {
        // Arrange
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});
        when(authService.login(any(LoginDto.class))).thenReturn(loginResponse);

        // Act
        ResponseEntity<GenericEntityResponse<Jwt>> response = ctrlAuth.login(loginDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(mapper).writeValueAsString(loginDto);
    }

    // ========== PRUEBAS PARA RECOVERY PASSWORD ==========

    @Test
    void testRecoveryPassword_Success() throws Exception {
        // Arrange
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":\"test@example.com\"}");
        when(userService.recoveryPassword(any(UserDto.class))).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlAuth.recoverypassword(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superGenericResponse, response.getBody());
        assertEquals(200, response.getBody().getCodigo());

        verify(userService).recoveryPassword(userDto);
        verify(mapper).writeValueAsString(userDto);
    }

    @Test
    void testRecoveryPassword_ValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(400, "Email no válido");
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":\"test@example.com\"}");
        when(userService.recoveryPassword(any(UserDto.class))).thenThrow(validationException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlAuth.recoverypassword(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getCodigo());
        assertEquals("Email no válido", response.getBody().getMensaje());

        verify(userService).recoveryPassword(userDto);
    }

    @Test
    void testRecoveryPassword_GenericException() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error interno");
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":\"test@example.com\"}");
        when(userService.recoveryPassword(any(UserDto.class))).thenThrow(runtimeException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlAuth.recoverypassword(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Error interno", response.getBody().getMensaje());

        verify(userService).recoveryPassword(userDto);
    }

    @Test
    void testRecoveryPassword_WithNullEmail() throws Exception {
        // Arrange
        userDto.setEmail(null);
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":null}");
        when(userService.recoveryPassword(any(UserDto.class))).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlAuth.recoverypassword(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superGenericResponse, response.getBody());

        verify(userService).recoveryPassword(userDto);
    }

    @Test
    void testRecoveryPassword_JsonProcessingException() throws Exception {
        // Arrange
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});
        when(userService.recoveryPassword(any(UserDto.class))).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlAuth.recoverypassword(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());

        verify(mapper).writeValueAsString(userDto);
    }

    // ========== PRUEBAS PARA GET SECURITY QUESTION ==========

    @Test
    void testGetSecurityQuestion_NotFoundException() throws Exception {
        // Arrange
        NotFoundException notFoundException = new NotFoundException(404, "Usuario no encontrado");
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":\"test@example.com\"}");
        when(userService.getSecurityQuestio(any(UserDto.class))).thenThrow(notFoundException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlAuth.getSecurityQuestion(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(404, response.getBody().getCodigo());
        assertEquals("Usuario no encontrado", response.getBody().getMensaje());

        verify(userService).getSecurityQuestio(userDto);
    }

    @Test
    void testGetSecurityQuestion_ValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(400, "Email requerido");
        when(mapper.writeValueAsString(any())).thenReturn("{\"email\":\"test@example.com\"}");
        when(userService.getSecurityQuestio(any(UserDto.class))).thenThrow(validationException);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlAuth.getSecurityQuestion(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getCodigo());
        assertEquals("Email requerido", response.getBody().getMensaje());

        verify(userService).getSecurityQuestio(userDto);
    }


    @Test
    void testRecoveryPassword_NullRequest() throws Exception {
        // Arrange
        when(mapper.writeValueAsString(any())).thenReturn("null");
        when(userService.recoveryPassword(any())).thenReturn(superGenericResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = ctrlAuth.recoverypassword(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
    }


}
