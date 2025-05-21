package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.DELETE;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.UPDATE;

import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarAddUser;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarEmailGiven;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarIdGiven;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarRecoveryPassword;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdateUser;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarChangePassword;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUnlockUser;



import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.controllers.dto.UserDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.RecoveryPassword;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.CatalogoRepository;
import com.uca.idhuca.sistema.indicadores.repositories.RecoveryPasswordRepository;
import com.uca.idhuca.sistema.indicadores.repositories.UsuarioRepository;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.services.IUser;
import com.uca.idhuca.sistema.indicadores.useCase.UserUseCase;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserImpl implements IUser {
	
	@Autowired
	PasswordEncoder pEncoder;

	@Autowired
	private UsuarioRepository userRepository;

	@Autowired
	private CatalogoRepository catalogoRepository;
	
	@Autowired
	private RecoveryPasswordRepository recoveryPasswordRepository;
	
	@Autowired
	private IAuditoria auditoriaService;
	
	@Autowired
	private Utilidades utils;
	
	@Autowired
	private UserUseCase useCase;
	
	@Override
	public SuperGenericResponse add(UserDto request) throws ValidationException {
		List<String> errorsList = validarAddUser(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);
		
		
		Usuario usuario = null;
		try {
			usuario = userRepository
					 .findByEmail(request.getEmail()).get();
			
			if(usuario != null) {
				log.info("Usuario ya existe.");
				throw new ValidationException(ERROR, "Usuario ya existe.");
			}
		} catch (NoSuchElementException e) {
			log.info("Usuario no existe.");
		}
		 log.info("[{}] Usuario válido", key);
		 
		 Usuario newUser = useCase.darFormatoInsert(request);
		 log.info("[{}] Creando usuario...",key);
		 
		 Usuario usuarioSaved = userRepository.save(newUser);
		 
		 RecoveryPassword recovery = useCase.darFormatoRecovery(request, usuarioSaved);
		 recoveryPasswordRepository.save(recovery);
		 log.info("[{}] Usuario guardado correctamente.",key);
		 
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), CREAR, newUser));
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), CREAR, recovery));
		 log.info("[{}] Auditoria creada correctamente.",key);
		 
		return new SuperGenericResponse(OK, "Usuario guardado correctamente.");
	}

	@Override
	public SuperGenericResponse delete(Integer id) throws ValidationException, NotFoundException {
		List<String> errorsList = validarIdGiven(id);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}
		
		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);
		
		Usuario usuario = null;
		try {
			usuario = userRepository
					 .findById(id.longValue()).get();
		} catch (NoSuchElementException e) {
			log.info("Usuario no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		log.info("[{}] Usuario encontrado correctamente.", key);
		
		userRepository.delete(usuario);
		log.info("[{}] Usuario eliminado correctamente.", key);
		
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), DELETE, usuario));
		 log.info("[{}] Auditoria creada correctamente.",key);
		
		return new SuperGenericResponse(OK, "Usuario elimindo correctamente.");
	}

	@Override
	public SuperGenericResponse update(UserDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarUpdateUser(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}
		
		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);
		
		Usuario usuario = null;
		try {
			usuario = userRepository
					 .findById(request.getId()).get();
		} catch (NoSuchElementException e) {
			log.info("Usuario no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		
		RecoveryPassword recovery;
		try {
			recovery = recoveryPasswordRepository.findByUsuario(usuario).get();
		} catch (NoSuchElementException e) {
			log.info("Recovery no existe.");
			throw new NotFoundException(ERROR, "Recovery no existe.");
		}
		log.info("[{}] Usuario encontrado correctamente.", key);
		
		useCase.darFormatoUpdate(request, usuario);
		log.info("[{}] Actualizando usuario... [{}]", key, usuario);
		
		useCase.darFormatoUpdateRecovery(request, recovery);
		log.info("[{}] Actualizando recovery... [{}]", key, usuario);
		
		userRepository.save(usuario);
		recoveryPasswordRepository.save(recovery);
		log.info("[{}] Usuario actualizado correctamente.", key);
		
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, usuario));
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, recovery));
		 log.info("[{}] Auditoria creada correctamente.",key);
		 
		return new SuperGenericResponse(OK, "Usuario actualizado correctamente.");
	}

	@Override
	public GenericEntityResponse<Usuario> getOne(Integer id) throws ValidationException, NotFoundException {
		List<String> errorsList = validarIdGiven(id);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}
		
		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);
		
		Usuario usuario = null;
		try {
			usuario = userRepository
					 .findById(id.longValue()).get();
		} catch (NoSuchElementException e) {
			log.info("Usuario no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		
		log.info("[{}] Usuario encontrado correctamente.", key);
		
		return new GenericEntityResponse<Usuario>(OK, "Usuario elimindo correctamente.", usuario);
	}

	@Override
	public GenericEntityResponse<List<Usuario>> getAll() throws ValidationException, NotFoundException {
		List<Usuario> users = userRepository.findAll();
		
		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);
		
		if(users == null || users.size() == 0) {
			log.info("No hay usuarios en la base de datos.");
			throw new NotFoundException(ERROR, "No hay usuarios en la base de datos.");
		}
		log.info("[{}] Usuarios obtenidos correctamente.", key);
		
		return new GenericEntityResponse<List<Usuario>>(OK, "Usuario elimindo correctamente.", users);
	}

	@Override
	public SuperGenericResponse recoveryPassword(UserDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarRecoveryPassword(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}
		
		String key  = request.getEmail() != null ?  request.getEmail() : "SYSTEM";
		log.info("[{}] Request válido", key);
		
		Usuario usuario = null;
		try {
			usuario = userRepository
					 .findByEmail(request.getEmail()).get();
		} catch (NoSuchElementException e) {
			log.info("Usuario no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		
		RecoveryPassword recovery;
		try {
			recovery = recoveryPasswordRepository.findByUsuario(usuario).get();
		} catch (NoSuchElementException e) {
			log.info("Recovery no existe.");
			throw new NotFoundException(ERROR, "Recovery no existe.");
		}
		
		if(recovery.getIntentosFallidos() >= utils.maximoIntentosPreguntaSeguridad()) {
			throw new ValidationException(ERROR, "Usuario alcanzo el maximo de intentos permitidos.");
		}
		log.error("[{}] Usuario puede intentar validar pregunta de seguridad.", key);
		
		if(!pEncoder.matches(request.getSecurityAnswer(), recovery.getRespuestaHash())) {
			
			log.error("[{}] Password incorrecta.", key);
			recovery.setIntentosFallidos(recovery.getIntentosFallidos() + 1);
			recoveryPasswordRepository.save(recovery);
			
			throw new ValidationException(ERROR, "Respuesta incorrecta.");
		}
		
		recovery.setIntentosFallidos(0);
		usuario.setContrasenaHash(pEncoder.encode(request.getNewPassword()));
		userRepository.save(usuario);
		recoveryPasswordRepository.save(recovery);
		log.error("[{}] Contraseña actualizada.", key);
		
		return new SuperGenericResponse(OK, "Cambio de contraseña existoso.");
	}

	@Override
	public GenericEntityResponse<Catalogo> getSecurityQuestio(UserDto request)
			throws ValidationException, NotFoundException {
		List<String> errorsList = validarEmailGiven(request.getEmail());
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}
		
		String key  = request.getEmail() != null ?  request.getEmail() : "SYSTEM";
		log.info("[{}] Request válido", key);
		
		Usuario usuario = null;
		try {
			usuario = userRepository
					 .findByEmail(request.getEmail()).get();
		} catch (NoSuchElementException e) {
			log.info("Usuario no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		
		RecoveryPassword recovery;
		try {
			recovery = recoveryPasswordRepository.findByUsuario(usuario).get();
		} catch (NoSuchElementException e) {
			log.info("Recovery no existe.");
			throw new NotFoundException(ERROR, "Recovery no existe.");
		}
		
		Catalogo securityQuestion;
		try {
			securityQuestion = catalogoRepository.findByCodigo(recovery.getPreguntaCodigo());
		} catch (Exception e) {
			log.info("Pregunta no existe.");
			throw new NotFoundException(ERROR, "Pregunta no existe.");
		}
		
		log.error("[{}] Pregunta obtenida correctamente.", key);
		
		return new GenericEntityResponse<Catalogo>(OK, "Pregunta obtenida correctamente.", securityQuestion);
	}

	@Override
	public SuperGenericResponse changePassword(UserDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarChangePassword(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		Usuario usuario = utils.obtenerUsuarioAutenticado();
		
		String key = usuario.getEmail();
		log.info("[{}] Request válido", key);
		
		RecoveryPassword recovery;
		try {
			recovery = recoveryPasswordRepository.findByUsuario(usuario).get();
		} catch (NoSuchElementException e) {
			log.info("Recovery no existe.");
			throw new NotFoundException(ERROR, "Recovery no existe.");
		}
		log.info("[{}] Usuario encontrado correctamente.", key);
		
		if(recovery.getIntentosFallidos() >= utils.maximoIntentosPreguntaSeguridad()) {
			throw new ValidationException(ERROR, "Usuario alcanzo el maximo de intentos permitidos.");
		}
		
		if(!pEncoder.matches(request.getPassword(), usuario.getContrasenaHash())) {
	        log.error("[{}] Password incorrecta.", key);
	        recovery.setIntentosFallidos(recovery.getIntentosFallidos() + 1);
	        recoveryPasswordRepository.save(recovery);
	        throw new ValidationException(ERROR, "Error: usuario o contraseña no coinciden.");
	    }
	    log.info("[{}] Autenticación correcta.", key);
		
	    usuario.setContrasenaHash(pEncoder.encode(request.getNewPassword()));
		recovery.setIntentosFallidos(0);
		log.info("[{}] Actualizando usuario... [{}]", key, usuario);
		 
		userRepository.save(usuario);
		recoveryPasswordRepository.save(recovery);
		log.info("[{}] Usuario actualizado correctamente.", key);
		
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, usuario));
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, recovery));
		 log.info("[{}] Auditoria creada correctamente.",key);
		 
		return new SuperGenericResponse(OK, "Usuario actualizado correctamente.");
	}
	
	@Override
	public SuperGenericResponse unlockUser(UserDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarUnlockUser(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		Usuario usuario = utils.obtenerUsuarioAutenticado();
		
		String key = usuario.getEmail();
		log.info("[{}] Request válido", key);
		
		Usuario userFound;
		try {
			userFound = userRepository.findById(request.getId()).get();
		} catch (NoSuchElementException e) {
			log.info("Recovery no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		log.info("[{}] Usuario encontrado correctamente.", key);
		
		RecoveryPassword recovery;
		try {
			recovery = recoveryPasswordRepository.findByUsuario(userFound).get();
		} catch (NoSuchElementException e) {
			log.info("Recovery no existe.");
			throw new NotFoundException(ERROR, "Recovery no existe.");
		}
		log.info("[{}] Usuario encontrado correctamente.", key);
		
		recovery.setIntentosFallidos(0);
		log.info("[{}] Actualizando usuario... [{}]", key, usuario);
		 
		recoveryPasswordRepository.save(recovery);
		log.info("[{}] Usuario actualizado correctamente.", key);
		
		 auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, recovery));
		 log.info("[{}] Auditoria creada correctamente.",key);
		 
		return new SuperGenericResponse(OK, "Usuario desbloqueado correctamente.");
	}

}
