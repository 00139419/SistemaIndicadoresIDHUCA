package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarSaveFicha;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uca.idhuca.sistema.indicadores.controllers.dto.ArchivoAdjuntoRequest;
import com.uca.idhuca.sistema.indicadores.controllers.dto.NotaDerechoRequest;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.NotaDerecho;
import com.uca.idhuca.sistema.indicadores.models.NotaDerechoArchivo;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
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

	    NotaDerecho nota = new NotaDerecho();
	    nota.setDerecho(request.getDerecho());
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


}
