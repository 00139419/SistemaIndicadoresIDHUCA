package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;

import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarSaveFicha;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarObtenerDetalleArchivos;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdateNote;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarDeleteNote;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.controllers.dto.ArchivoAdjuntoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UsuarioSimple;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.NotaDerechoArchivoDTO;
import com.uca.idhuca.sistema.indicadores.dto.NotaDerechoDTO;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.NotaDerecho;
import com.uca.idhuca.sistema.indicadores.models.NotaDerechoArchivo;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoCatalogo;
import com.uca.idhuca.sistema.indicadores.repositories.NotaDerechoArchivoRepository;
import com.uca.idhuca.sistema.indicadores.repositories.NotaDerechoRepository;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.IFichaDerecho;
import com.uca.idhuca.sistema.indicadores.utils.ProjectProperties;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FichaDerechoImpl implements IFichaDerecho{
	
	@Autowired
	private Utilidades utils;

	@Autowired
	private NotaDerechoRepository notaRepo;

	@Autowired
	private NotaDerechoArchivoRepository archivoRepo;

	@Autowired
	private ProjectProperties projectProperties;
	
	@Autowired
	private IAuditoria auditoriaService;
	
	@Autowired
	private IRepoCatalogo catalogoRepo;
	
	@Override
	public SuperGenericResponse save(NotaDerechoRequest request, MultipartFile[] archivos)
	        throws ValidationException {

	    List<String> errorsList = validarSaveFicha(request, archivos);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    Usuario usuarioAutenticado = utils.obtenerUsuarioAutenticado();
	    String key = usuarioAutenticado.getEmail();
	    log.info("[{}] Request válido", key);
	    
	    Catalogo catalogo = null;
		try {
			catalogo = catalogoRepo
					 .findByCodigo(request.getDerecho().getCodigo());
		} catch (NoSuchElementException e) {
			System.out.println("catalogo no existe.");
		}
		 log.info("[{}] catalogo válido", key);

	    NotaDerecho nota = new NotaDerecho();
	    nota.setDerecho(catalogo);
	    nota.setFecha(new Date(System.currentTimeMillis()));
	    nota.setTitulo(request.getTitulo());
	    nota.setDescripcion(request.getDescripcion());
	    nota.setCreadoPor(usuarioAutenticado);
	    nota.setModificadoPor(usuarioAutenticado);
	    nota.setModificadoEn(new Date(System.currentTimeMillis()));
	    nota.setCreadoEn(new Date(System.currentTimeMillis()));

	    NotaDerecho notaGuardada = notaRepo.save(nota);
	    auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), CREAR, notaGuardada));
	    log.info("[{}] Nota principal guardada correctamente con ID: {}", key, notaGuardada.getId());

	    for (int i = 0; i < archivos.length; i++) {
	        MultipartFile archivo = archivos[i];
	        ArchivoAdjuntoRequest archivoReq = request.getArchivos().get(i);

	        String hashNombre = UUID.randomUUID().toString();
	        String extension = utils.getExtension(archivo.getOriginalFilename());
	        String nombreFisico = hashNombre + extension;
	        Path destino = Paths.get(projectProperties.getRutaArchivosFisicos(), nombreFisico);

	        try {
	            Files.createDirectories(destino.getParent());
	            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
	        } catch (IOException e) {
	            log.error("[{}] Error al guardar archivo: {}", key, archivo.getOriginalFilename(), e);
	            throw new ValidationException(ERROR, "Error al guardar el archivo " + archivo.getOriginalFilename());
	        }

	        NotaDerechoArchivo meta = new NotaDerechoArchivo();
	        meta.setNota(notaGuardada);
	        meta.setNombreOriginal(archivo.getOriginalFilename());
	        meta.setArchivoUrl(nombreFisico);
	        meta.setTipo(archivoReq.getTipo());

	        NotaDerechoArchivo metaGuardada = archivoRepo.save(meta);
	        auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), CREAR, metaGuardada));
	    }

	    log.info("[{}] Archivos adjuntos guardados correctamente para nota ID: {}", key, notaGuardada.getId());

	    return new SuperGenericResponse(OK, "Nota guardada correctamente con ID: " + notaGuardada.getId());
	}
	
	@Override
	public SuperGenericResponse updateNotePost(NotaDerechoRequest request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarUpdateNote(request);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    Usuario usuarioAutenticado = utils.obtenerUsuarioAutenticado();
	    String key = usuarioAutenticado.getEmail();
	    
	    NotaDerecho nota = null;
		try {
			nota = notaRepo.findById(request.getId()).get();
		} catch (Exception e) {
			System.out.println("nota no existe.");
			throw new NotFoundException(ERROR, "No existe la nota");
		}
		log.info("[{}] Request válido", key);
		 
		nota.setDescripcion(request.getDescripcion());
		nota.setModificadoEn(new Date(System.currentTimeMillis()));
		nota.setModificadoPor(usuarioAutenticado);
		nota.setTitulo(request.getTitulo());
		 
		notaRepo.save(nota);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, nota));
		
		return new SuperGenericResponse(OK, "Post actualizado correctamente");
	}
	
	@Override
	public SuperGenericResponse deletePost(NotaDerechoRequest request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarDeleteNote(request);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    Usuario usuarioAutenticado = utils.obtenerUsuarioAutenticado();
	    String key = usuarioAutenticado.getEmail();
	    
	    NotaDerecho nota = null;
		try {
			nota = notaRepo.findById(request.getId()).get();
		} catch (Exception e) {
			System.out.println("nota no existe.");
			throw new NotFoundException(ERROR, "No existe la nota");
		}		
		log.info("[{}] Request válido", key);
		
	    for (NotaDerechoArchivo file : nota.getArchivos()) {
	        try {
	            Path ruta = Paths.get(projectProperties.getRutaArchivosFisicos(), file.getArchivoUrl());
	            if (Files.exists(ruta)) {
	                Files.delete(ruta);
	                log.info("[{}] Archivo eliminado del sistema: {}", key, ruta.getFileName());
	            } else {
	                log.warn("[{}] Archivo no encontrado en el sistema: {}", key, ruta.getFileName());
	            }
	        } catch (IOException e) {
	            log.error("[{}] Error al eliminar archivo físico: {}", key, file.getArchivoUrl(), e);
	        }
	    }
		
		notaRepo.delete(nota);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), DELETE, nota));
		
		log.info("[{}] Nota Eliminada correctamente.", key);
		
		return new SuperGenericResponse(OK, "Nota y sus archvios eliminados correctamente.");
	}
	
	@Override
	public GenericEntityResponse<List<NotaDerechoDTO>> getAllPost(String codigoDerecho) throws ValidationException {
	    List<String> errorsList = validarObtenerDetalleArchivos(codigoDerecho);
	    if (!errorsList.isEmpty()) {
	        throw new ValidationException(ERROR, errorsList.get(0));
	    }

	    String key = utils.obtenerUsuarioAutenticado().getEmail();
	    log.info("[{}] Request válido", key);

	    List<NotaDerecho> notas = notaRepo.findByDerechoCodigo(codigoDerecho);

	    List<NotaDerechoDTO> notaDTOs = notas.stream().map(nota -> {
	        List<NotaDerechoArchivoDTO> archivosDTO = nota.getArchivos().stream()
	            .filter(archivo -> {
	                String rutaCompleta = projectProperties.getRutaArchivosFisicos() + File.separator + archivo.getArchivoUrl();
	                return new File(rutaCompleta).exists();
	            })
	            .map(archivo -> new NotaDerechoArchivoDTO(
	                archivo.getNombreOriginal(),
	                archivo.getTipo(),
	                archivo.getArchivoUrl()
	            ))
	            .collect(Collectors.toList());

	        return new NotaDerechoDTO(
	            nota.getId(),
	            nota.getDerecho().getCodigo(),
	            nota.getFecha(),
	            nota.getTitulo(),
	            nota.getDescripcion(),
	            archivosDTO,
	            new UsuarioSimple(nota.getCreadoPor().getId(), nota.getCreadoPor().getEmail(), nota.getCreadoPor().getNombre()),
	            nota.getCreadoEn(),
	            new UsuarioSimple(nota.getModificadoPor().getId(), nota.getModificadoPor().getEmail(), nota.getCreadoPor().getNombre()),
	            nota.getModificadoEn());
	    }).collect(Collectors.toList());

	    log.info("[{}] Se encontraron {} notas para el derecho '{}'", key, notaDTOs.size(), codigoDerecho);
	    return new GenericEntityResponse<>(OK, "Datos obtenidos correctamente", notaDTOs);
	}

	@Override
	public Resource getFile(String nombreArchivo) throws ValidationException, NotFoundException, Exception {
		if (nombreArchivo == null || nombreArchivo.isBlank()) {
	        throw new NotFoundException(ERROR, "Nombre de archivo no puede ser vacío");
	    }

	    Usuario usuario = utils.obtenerUsuarioAutenticado();
	    String key = usuario.getEmail();
	    log.info("[{}] Buscando archivo: {}", key, nombreArchivo);
	    
        Path ruta = Paths.get(projectProperties.getRutaArchivosFisicos(), nombreArchivo);
        
        if (!Files.exists(ruta)) {
            throw new NotFoundException(ERROR, "El archivo no existe");
        }

        Resource recurso = new UrlResource(ruta.toUri());
        
        if (!recurso.exists() || !recurso.isReadable()) {
            throw new NotFoundException(ERROR, "No se puede leer el archivo solicitado");
        }

        log.info("[{}] Archivo encontrado y listo para descarga: {}", key, nombreArchivo);
        return recurso;
	}

}
