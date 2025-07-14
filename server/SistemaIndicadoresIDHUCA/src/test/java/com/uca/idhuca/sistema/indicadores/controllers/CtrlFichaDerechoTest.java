package com.uca.idhuca.sistema.indicadores.controllers;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UsuarioSimple;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.FichaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.NotaDerechoDTO;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.repositories.NotaDerechoRepository;
import com.uca.idhuca.sistema.indicadores.services.IFichaDerecho;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

@ExtendWith(MockitoExtension.class)
class CtrlFichaDerechoTest {

    @Mock
    private Utilidades utils;

    @Mock
    private IFichaDerecho fichaDerechoService;

    @Mock
    private NotaDerechoRepository notaRepository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private Resource resource;

    @InjectMocks
    private CtrlFichaDerecho controller;

    private UserDto usuarioMock;
    private NotaDerechoRequest notaRequest;
    private FichaDerechoRequest fichaRequest;
    private SuperGenericResponse successResponse;
    private GenericEntityResponse<List<NotaDerechoDTO>> entityResponse;
    private MultipartFile[] archivos;

    @BeforeEach
    void setUp() throws ParseException {
        usuarioMock = new UserDto("Test", "test@example.com");
        usuarioMock.setEmail("test@example.com");

        notaRequest = new NotaDerechoRequest();
        notaRequest.setId(1L);
        notaRequest.setTitulo("Test Nota");

        fichaRequest = new FichaDerechoRequest();
        fichaRequest.setCodigoDerecho("1");

        successResponse = new SuperGenericResponse();
        successResponse.setCodigo(200);
        successResponse.setMensaje("Operación exitosa");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = dateFormat.parse("07-12-2025");

        List<NotaDerechoDTO> notas = Arrays.asList(
                new NotaDerechoDTO(
                        1L,
                        "Nota 1",
                        date,
                        "Descripción 1",
                        "Activo",
                        List.of(), // Empty list for archivos
                        new UsuarioSimple(1L, "Usuario 1", "usuario1@example.com"),
                        date,
                        new UsuarioSimple(1L, "Usuario 1", "usuario1@example.com"),
                        date
                ),
                new NotaDerechoDTO(
                        2L,
                        "Nota 2",
                        date,
                        "Descripción 2",
                        "Activo",
                        List.of(), // Empty list for archivos
                        new UsuarioSimple(2L, "Usuario 1", "usuario2@example.com"),
                        date,
                        new UsuarioSimple(2L, "Usuario 2", "usuario2@example.com"),
                        date
                )
        );

        entityResponse = new GenericEntityResponse<>();
        entityResponse.setCodigo(200);
        entityResponse.setMensaje("Datos obtenidos exitosamente");
        entityResponse.setEntity(notas);

        MockMultipartFile archivo1 = new MockMultipartFile("archivo1", "test1.pdf",
                MediaType.APPLICATION_PDF_VALUE, "contenido test 1".getBytes());
        MockMultipartFile archivo2 = new MockMultipartFile("archivo2", "test2.pdf",
                MediaType.APPLICATION_PDF_VALUE, "contenido test 2".getBytes());

        archivos = new MultipartFile[]{archivo1, archivo2};
    }

    // ========== TESTS PARA MÉTODO SAVE ==========

    @Test
    void save_DeberiaRetornarOK_CuandoOperacionEsExitosa() throws Exception {
        // Arrange
        String notaJson = "{\"id\":1,\"titulo\":\"Test\"}";
        // Mock de Usuario en lugar de UserDto
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.readValue(notaJson, NotaDerechoRequest.class)).thenReturn(notaRequest);
        when(mapper.writeValueAsString(notaRequest)).thenReturn(notaJson);
        when(fichaDerechoService.save(notaRequest, archivos)).thenReturn(successResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.save(notaJson, archivos);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successResponse, response.getBody());
        verify(fichaDerechoService).save(notaRequest, archivos);
    }

    @Test
    void save_DeberiaRetornarBadRequest_CuandoOcurreValidationException() throws Exception {
        // Arrange
        String notaJson = "{\"id\":1,\"titulo\":\"Test\"}";
        ValidationException validationException = new ValidationException(-1, "Datos inválidos");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.readValue(notaJson, NotaDerechoRequest.class)).thenReturn(notaRequest);
        when(mapper.writeValueAsString(notaRequest)).thenReturn(notaJson);
        when(fichaDerechoService.save(notaRequest, archivos)).thenThrow(validationException);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.save(notaJson, archivos);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Datos inválidos", response.getBody().getMensaje());
    }

    @Test
    void save_DeberiaRetornarInternalServerError_CuandoOcurreExcepcionGenerica() throws Exception {
        // Arrange
        String notaJson = "{\"id\":1,\"titulo\":\"Test\"}";
        RuntimeException runtimeException = new RuntimeException("Error interno");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.readValue(notaJson, NotaDerechoRequest.class)).thenReturn(notaRequest);
        when(mapper.writeValueAsString(notaRequest)).thenReturn(notaJson);
        when(fichaDerechoService.save(notaRequest, archivos)).thenThrow(runtimeException);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.save(notaJson, archivos);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Error interno", response.getBody().getMensaje());
    }

    // ========== TESTS PARA MÉTODO DELETE POST ==========

    @Test
    void deletePost_DeberiaRetornarOK_CuandoOperacionEsExitosa() throws Exception {
        // Arrange
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(notaRequest)).thenReturn("{}");
        when(fichaDerechoService.deletePost(notaRequest)).thenReturn(successResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.deletePost(notaRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successResponse, response.getBody());
        verify(fichaDerechoService).deletePost(notaRequest);
    }

    @Test
    void deletePost_DeberiaRetornarBadRequest_CuandoOcurreValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(-1, "No se puede eliminar");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(notaRequest)).thenReturn("{}");
        when(fichaDerechoService.deletePost(notaRequest)).thenThrow(validationException);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.deletePost(notaRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("No se puede eliminar", response.getBody().getMensaje());
    }

    @Test
    void deletePost_DeberiaRetornarInternalServerError_CuandoOcurreExcepcionGenerica() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error al eliminar");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(notaRequest)).thenReturn("{}");
        when(fichaDerechoService.deletePost(notaRequest)).thenThrow(runtimeException);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.deletePost(notaRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Error al eliminar", response.getBody().getMensaje());
    }

    // ========== TESTS PARA MÉTODO UPDATE NOTE ==========

    @Test
    void updateNote_DeberiaRetornarOK_CuandoOperacionEsExitosa() throws Exception {
        // Arrange
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(notaRequest)).thenReturn("{}");
        when(fichaDerechoService.updateNotePost(notaRequest)).thenReturn(successResponse);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.updateNote(notaRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successResponse, response.getBody());
        verify(fichaDerechoService).updateNotePost(notaRequest);
    }

    @Test
    void updateNote_DeberiaRetornarBadRequest_CuandoOcurreValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(-1, "Datos inválidos para actualizar");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(notaRequest)).thenReturn("{}");
        when(fichaDerechoService.updateNotePost(notaRequest)).thenThrow(validationException);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.updateNote(notaRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Datos inválidos para actualizar", response.getBody().getMensaje());
    }

    @Test
    void updateNote_DeberiaRetornarInternalServerError_CuandoOcurreExcepcionGenerica() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error al actualizar");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(mapper.writeValueAsString(notaRequest)).thenReturn("{}");
        when(fichaDerechoService.updateNotePost(notaRequest)).thenThrow(runtimeException);

        // Act
        ResponseEntity<SuperGenericResponse> response = controller.updateNote(notaRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Error al actualizar", response.getBody().getMensaje());
    }

    // ========== TESTS PARA MÉTODO GET ALL POST ==========

    @Test
    void getAllPost_DeberiaRetornarOK_CuandoOperacionEsExitosa() throws Exception {
        // Arrange
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(fichaDerechoService.getAllPost(fichaRequest)).thenReturn(entityResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<NotaDerechoDTO>>> response = controller.getAllPost(fichaRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(entityResponse, response.getBody());
        assertEquals(2, response.getBody().getEntity().size());
        verify(fichaDerechoService).getAllPost(fichaRequest);
    }

    @Test
    void getAllPost_DeberiaRetornarBadRequest_CuandoOcurreValidationException() throws Exception {
        // Arrange
        ValidationException validationException = new ValidationException(-1, "Parámetros inválidos");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(fichaDerechoService.getAllPost(fichaRequest)).thenThrow(validationException);

        // Act
        ResponseEntity<GenericEntityResponse<List<NotaDerechoDTO>>> response = controller.getAllPost(fichaRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(-1, response.getBody().getCodigo());
        assertEquals("Parámetros inválidos", response.getBody().getMensaje());
    }

    @Test
    void getAllPost_DeberiaRetornarInternalServerError_CuandoOcurreExcepcionGenerica() throws Exception {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Error al obtener datos");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(fichaDerechoService.getAllPost(fichaRequest)).thenThrow(runtimeException);

        // Act
        ResponseEntity<GenericEntityResponse<List<NotaDerechoDTO>>> response = controller.getAllPost(fichaRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ERROR, response.getBody().getCodigo());
        assertEquals("Error al obtener datos", response.getBody().getMensaje());
    }

    // ========== TESTS PARA MÉTODO GET FILE ==========

    @Test
    void getFile_DeberiaRetornarArchivo_CuandoOperacionEsExitosa() throws Exception {
        // Arrange
        String nombreArchivo = "archivo_test.pdf";
        String nombreOriginal = "documento_original.pdf";

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(notaRepository.findNombreOriginalByArchivoUrl(nombreArchivo)).thenReturn(nombreOriginal);
        when(fichaDerechoService.getFile(nombreArchivo)).thenReturn(resource);

        // Act
        ResponseEntity<Resource> response = controller.getFile(nombreArchivo);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resource, response.getBody());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains(nombreOriginal));
        verify(fichaDerechoService).getFile(nombreArchivo);
        verify(notaRepository).findNombreOriginalByArchivoUrl(nombreArchivo);
    }

    @Test
    void getFile_DeberiaRetornarBadRequest_CuandoOcurreValidationException() throws Exception {
        // Arrange
        String nombreArchivo = "archivo_invalido.pdf";
        ValidationException validationException = new ValidationException(-1, "Archivo inválido");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(notaRepository.findNombreOriginalByArchivoUrl(nombreArchivo)).thenReturn("original.pdf");
        when(fichaDerechoService.getFile(nombreArchivo)).thenThrow(validationException);

        // Act
        ResponseEntity<Resource> response = controller.getFile(nombreArchivo);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getFile_DeberiaRetornarNoContent_CuandoOcurreNotFoundException() throws Exception {
        // Arrange
        String nombreArchivo = "archivo_inexistente.pdf";
        NotFoundException notFoundException = new NotFoundException(-1, "Archivo no encontrado");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(notaRepository.findNombreOriginalByArchivoUrl(nombreArchivo)).thenReturn("original.pdf");
        when(fichaDerechoService.getFile(nombreArchivo)).thenThrow(notFoundException);

        // Act
        ResponseEntity<Resource> response = controller.getFile(nombreArchivo);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    @Test
    void getFile_DeberiaRetornarInternalServerError_CuandoOcurreExcepcionGenerica() throws Exception {
        // Arrange
        String nombreArchivo = "archivo_error.pdf";
        RuntimeException runtimeException = new RuntimeException("Error interno del servidor");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(notaRepository.findNombreOriginalByArchivoUrl(nombreArchivo)).thenReturn("original.pdf");
        when(fichaDerechoService.getFile(nombreArchivo)).thenThrow(runtimeException);

        // Act
        ResponseEntity<Resource> response = controller.getFile(nombreArchivo);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ========== TESTS PARA ESCENARIOS EDGE CASES ==========


    @Test
    void getAllPost_DeberiaRetornarListaVacia_CuandoNoHayDatos() throws Exception {
        // Arrange
        GenericEntityResponse<List<NotaDerechoDTO>> emptyResponse = new GenericEntityResponse<>();
        emptyResponse.setCodigo(200);
        emptyResponse.setMensaje("No hay datos");
        emptyResponse.setEntity(List.of());

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(fichaDerechoService.getAllPost(fichaRequest)).thenReturn(emptyResponse);

        // Act
        ResponseEntity<GenericEntityResponse<List<NotaDerechoDTO>>> response = controller.getAllPost(fichaRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getEntity().size());
        assertEquals("No hay datos", response.getBody().getMensaje());
    }

    @Test
    void getFile_DeberiaRetornarArchivo_CuandoNombreOriginalEsNull() throws Exception {
        // Arrange
        String nombreArchivo = "archivo_test.pdf";

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@example.com");

        // Configuración del mock
        when(utils.obtenerUsuarioAutenticado()).thenReturn(usuarioMock);
        when(notaRepository.findNombreOriginalByArchivoUrl(nombreArchivo)).thenReturn(null);
        when(fichaDerechoService.getFile(nombreArchivo)).thenReturn(resource);

        // Act
        ResponseEntity<Resource> response = controller.getFile(nombreArchivo);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resource, response.getBody());
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("null"));
    }
}