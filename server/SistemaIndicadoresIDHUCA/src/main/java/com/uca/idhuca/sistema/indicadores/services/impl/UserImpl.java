package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.*;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.*;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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
import com.uca.idhuca.sistema.indicadores.utils.RequestValidations;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserImpl implements IUser {

	@Autowired
	private PasswordEncoder pEncoder;

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

		if (request == null || request.getNombre() == null || request.getNombre().isEmpty()
				|| request.getEmail() == null || request.getEmail().isEmpty()
				|| request.getRol() == null || request.getRol().getCodigo() == null) {
			throw new ValidationException(ERROR, "Nombre, email y rol son campos obligatorios");
		}

		Optional<Usuario> existingUser = userRepository.findByEmail(request.getEmail());
		if (existingUser.isPresent()) {
			log.info("Usuario ya existe.");
			throw new ValidationException(ERROR, "El usuario con email " + request.getEmail() + " ya existe");
		}

		String provisionalPassword = UUID.randomUUID().toString().substring(0, 8);

		Usuario newUser = new Usuario();
		newUser.setNombre(request.getNombre());
		newUser.setEmail(request.getEmail());
		newUser.setRol(request.getRol());
		newUser.setContrasenaHash(pEncoder.encode(provisionalPassword));
		newUser.setActivo(true);
		newUser.setCreadoEn(new Date());
		newUser.setEsPasswordProvisional(true);
		log.info("[{}] Creando usuario... [{}]", key, newUser);

		Usuario savedUser = userRepository.save(newUser);

		String createdKey = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Usuario creado: {}", createdKey, savedUser.getEmail());


		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), CREAR, savedUser));
		return new SuperGenericResponse(
				OK,
				String.format("Usuario %s creado exitosamente. Contraseña provisional: %s",
						savedUser.getEmail(),
						provisionalPassword)
		);
	}

	@Override
	public SuperGenericResponse delete(Integer id) throws ValidationException, NotFoundException {
		List<String> errorsList = validarIdGiven(id);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		Usuario usuario = userRepository.findById(id.longValue())
				.orElseThrow(() -> new NotFoundException(ERROR, "Usuario no existe."));
		log.info("[{}] Usuario encontrado correctamente.", key);

		userRepository.delete(usuario);
		log.info("[{}] Usuario eliminado correctamente.", key);

		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), DELETE, usuario));
		log.info("[{}] Auditoria creada correctamente.", key);

		return new SuperGenericResponse(OK, "Usuario eliminado correctamente.");
	}

	@Override
	public SuperGenericResponse update(UserDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarUpdateUser(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		Usuario usuario = userRepository.findById(request.getId())
				.orElseThrow(() -> new NotFoundException(ERROR, "Usuario no existe."));

		RecoveryPassword recovery = recoveryPasswordRepository.findByUsuario(usuario)
				.orElseThrow(() -> new NotFoundException(ERROR, "Para acceder a esta función, primero debe iniciar sesión y configurar una pregunta de seguridad."));

		useCase.darFormatoUpdate(request, usuario);
		log.info("[{}] Actualizando usuario... [{}]", key, usuario);

		useCase.darFormatoUpdateRecovery(request, recovery);
		log.info("[{}] Actualizando recovery... [{}]", key, recovery);

		userRepository.save(usuario);
		recoveryPasswordRepository.save(recovery);
		log.info("[{}] Usuario actualizado correctamente.", key);

		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, usuario));
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, recovery));
		log.info("[{}] Auditoria creada correctamente.", key);

		return new SuperGenericResponse(OK, "Usuario actualizado correctamente.");
	}

	@Override
	public SuperGenericResponse updateNameCurrent(UserDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = RequestValidations.validarIdGiven(Math.toIntExact(request.getId()));
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		Usuario usuario = userRepository.findById(request.getId())
				.orElseThrow(() -> new NotFoundException(ERROR, "Usuario no existe."));

		usuario.setNombre(request.getNombre());
		log.info("[{}] Actualizando nombre de usuario... [{}]", key, usuario);

		userRepository.save(usuario);
		log.info("[{}] Nombre de usuario actualizado correctamente.", key);

		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, usuario));
		log.info("[{}] Auditoria creada correctamente.", key);

		return new SuperGenericResponse(OK, "Nombre de usuario actualizado correctamente.");
	}

	@Override
	public GenericEntityResponse<Usuario> getOne(Integer id) throws ValidationException, NotFoundException {
		List<String> errorsList = validarIdGiven(id);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		Usuario usuario = userRepository.findById(id.longValue())
				.orElseThrow(() -> new NotFoundException(ERROR, "Usuario no existe."));
		log.info("[{}] Usuario encontrado correctamente.", key);

		return new GenericEntityResponse<>(OK, "Usuario encontrado correctamente.", usuario);
	}

	@Override
	public GenericEntityResponse<List<Usuario>> getAll() throws ValidationException, NotFoundException {
		List<Usuario> users = userRepository.findAll();

		String key = utils.obtenerUsuarioAutenticado().getEmail();
		log.info("[{}] Request válido", key);

		if (users == null || users.isEmpty()) {
			log.info("No hay usuarios en la base de datos.");
			throw new NotFoundException(ERROR, "No hay usuarios en la base de datos.");
		}
		log.info("[{}] Usuarios obtenidos correctamente.", key);

		return new GenericEntityResponse<>(OK, "Usuarios obtenidos correctamente.", users);
	}

	@Override
	public SuperGenericResponse recoveryPassword(UserDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarRecoveryPassword(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key  = request.getEmail() != null ?  request.getEmail() : "SYSTEM";
		log.info("[{}] Request válido", key);


		RecoveryPassword recovery;

		Usuario usuario = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NotFoundException(ERROR, "Usuario no encontrado."));

		recovery = recoveryPasswordRepository.findByUsuario(usuario)
				.orElseThrow(() -> new ValidationException(ERROR, "Para acceder a esta opcion, por favor incie sesion por primera vez. " +
						"Si ha olvidado su contraseña provisional, por favor contacte al administrador del sistema."));


		if (recovery.getIntentosFallidos() >= utils.maximoIntentosPreguntaSeguridad()) {
			log.error("[{}] Usuario alcanzó el máximo de intentos permitidos", key);
			usuario.setActivo(false);
			userRepository.save(usuario);
			throw new ValidationException(ERROR, "Usuario bloqueado por exceder el máximo de intentos permitidos.");
		}

		if (!pEncoder.matches(request.getSecurityAnswer(), recovery.getRespuestaHash())) {
			recovery.setIntentosFallidos(recovery.getIntentosFallidos() + 1);
			recoveryPasswordRepository.save(recovery);

			if (recovery.getIntentosFallidos() >= utils.maximoIntentosPreguntaSeguridad()) {
				log.error("[{}] Usuario alcanzó el máximo de intentos permitidos", key);
				usuario.setActivo(false);
				userRepository.save(usuario);
				throw new ValidationException(ERROR, "Usuario bloqueado por exceder el máximo de intentos permitidos.");
			}

			log.error("[{}] Respuesta incorrecta. Intentos fallidos: {}", key, recovery.getIntentosFallidos());
			throw new ValidationException(ERROR, "Respuesta incorrecta.");
		}

		recovery.setIntentosFallidos(0);
		recoveryPasswordRepository.save(recovery);

		if (!validatePasswordPolicy(request.getNewPassword())) {
			throw new ValidationException(ERROR, "La nueva contraseña debe tener al menos 8 caracteres e incluir al menos una letra y un número.");
		}

		usuario.setContrasenaHash(pEncoder.encode(request.getNewPassword()));
		usuario.setEsPasswordProvisional(false);
		userRepository.save(usuario);

		log.info("[{}] Contraseña actualizada exitosamente", key);
		return new SuperGenericResponse(OK, "Contraseña actualizada exitosamente.");
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
			log.info("Para acceder a esta función, primero debe iniciar sesión y configurar una pregunta de seguridad.");
			throw new NotFoundException(ERROR, "Para acceder a esta función, primero debe iniciar sesión y configurar una pregunta de seguridad.");
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
		// Validate request
		List<String> validations = RequestValidations.validarChangePassword(request);
		if (!validations.isEmpty()) {
			throw new ValidationException(ERROR, validations.get(0));
		}

		Usuario usuario = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NotFoundException(ERROR, "Usuario no encontrado"));

		if (!pEncoder.matches(request.getPassword(), usuario.getContrasenaHash())) {
			throw new ValidationException(ERROR, "La contraseña actual es incorrecta");
		}
		log.info("[{}] Contraseña actual válida.", request.getPassword());

		if (!validatePasswordPolicy(request.getNewPassword())) {
			throw new ValidationException(ERROR, "La nueva contraseña debe tener al menos 8 caracteres e incluir al menos una letra y un número.");
		}
		log.info("[{}] Nueva contraseña válida.", request.getEmail());

		usuario.setContrasenaHash(pEncoder.encode(request.getNewPassword()));
		usuario.setEsPasswordProvisional(false);
		userRepository.save(usuario);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, usuario));

		RecoveryPassword recovery = null;

		boolean hasRecoveryConfig = hasRecoveryConfig(usuario);
		if (hasRecoveryConfig) {
			recovery = recoveryPasswordRepository.findByUsuario(usuario)
					.orElseThrow(() -> new NotFoundException(ERROR, "No existe configuración de recuperación para este usuario"));

			log.info("[{}] Configuración de recuperación obtenida correctamente.", request.getEmail());
		} else{
			recovery = new RecoveryPassword();
			log.info("[{}] Usuario no tiene configuración de recuperación, se creará una nueva.", request.getEmail());
		}

		recovery.setPreguntaCodigo(request.getSecurityQuestion().getCodigo());
		recovery.setRespuestaHash(pEncoder.encode(request.getSecurityAnswer()));
		recovery.setUsuario(usuario);
		
		recoveryPasswordRepository.save(recovery);
		log.info("[{}] Configuración de recuperación actualizada correctamente.", request.getEmail());

		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, recovery));
		log.info("[{}] Auditoria creada correctamente.", request.getEmail());
		
		return new SuperGenericResponse(OK, "Contraseña actualizada exitosamente");
	}

	/* * Cambia la contraseña del usuario autenticado sin necesidad de email y pregunta de seguridad.
	 * Utiliza el token de autorización para obtener el usuario autenticado.
	 */
	@Override
	public SuperGenericResponse changePasswordSimple(UserDto request) throws ValidationException, NotFoundException {
		// Validate the request
		List<String> validations = RequestValidations.validarChangePassword(request);
		if (!validations.isEmpty()) {
			throw new ValidationException(ERROR, validations.get(0));
		}

		// Get the authenticated user using the Authorization token
		Usuario usuario = utils.obtenerUsuarioAutenticado();

		// Validate the current password
		if (!pEncoder.matches(request.getPassword(), usuario.getContrasenaHash())) {
			throw new ValidationException(ERROR, "La contraseña actual es incorrecta");
		}
		log.info("[{}] Contraseña actual válida.", usuario.getEmail());

		// Validate the new password against the password policy
		if (!validatePasswordPolicy(request.getNewPassword())) {
			throw new ValidationException(ERROR, "La nueva contraseña debe tener al menos 8 caracteres e incluir al menos una letra y un número.");
		}
		log.info("[{}] Nueva contraseña válida.", usuario.getEmail());

		// Update the user's password
		usuario.setContrasenaHash(pEncoder.encode(request.getNewPassword()));
		usuario.setEsPasswordProvisional(false);
		userRepository.save(usuario);

		// Log the change and add an audit entry
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, usuario));
		log.info("[{}] Contraseña actualizada exitosamente.", usuario.getEmail());

		return new SuperGenericResponse(OK, "Contraseña actualizada exitosamente.");
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
			log.info("Para acceder a esta función, primero debe iniciar sesión y configurar una pregunta de seguridad.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		log.info("[{}] Usuario encontrado correctamente.", key);

		RecoveryPassword recovery;
		try {
			recovery = recoveryPasswordRepository.findByUsuario(userFound).get();
		} catch (NoSuchElementException e) {
			log.info("Para acceder a esta función, primero debe iniciar sesión y configurar una pregunta de seguridad.");
			throw new NotFoundException(ERROR, "Para acceder a esta función, primero debe iniciar sesión y configurar una pregunta de seguridad.");
		}
		log.info("[{}] Usuario encontrado correctamente.", key);

		recovery.setIntentosFallidos(0);
		log.info("[{}] Actualizando usuario... [{}]", key, usuario);

		recoveryPasswordRepository.save(recovery);
		log.info("[{}] Usuario actualizado correctamente.", key);

		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, recovery));
		log.info("[{}] Auditoria de recovery creada correctamente.",key);

		String provisionalPassword = UUID.randomUUID().toString().substring(0, 8);

		userFound.setContrasenaHash(pEncoder.encode(provisionalPassword));
		userFound.setEsPasswordProvisional(true);
		userFound.setActivo(true);
		
		userRepository.save(userFound);
		auditoriaService.add(utils.crearDto(utils.obtenerUsuarioAutenticado(), UPDATE, userFound));
		log.info("[{}] Contraseña provisional agregada correctamente.", key);
		
		return new SuperGenericResponse(OK, "Usuario desbloqueado correctamente. Contraseña provisional: " + provisionalPassword);
	}
	
	private boolean validatePasswordPolicy(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}

		boolean hasLetter = false;
		boolean hasNumber = false;

		for (char c : password.toCharArray()) {
			if (Character.isLetter(c)) {
				hasLetter = true;
			}
			if (Character.isDigit(c)) {
				hasNumber = true;
			}
			if (hasLetter && hasNumber) {
				return true;
			}
		}

		return false;
	}

	private boolean hasRecoveryConfig(Usuario user) throws NotFoundException {
		if (user.getEmail()== null) {
			throw new NotFoundException(ERROR, "Usuario no encontrado");
		}

		RecoveryPassword recovery = recoveryPasswordRepository.findByUsuario(user)
				.orElse(null);

		return recovery != null && recovery.getPreguntaCodigo() != null && !recovery.getPreguntaCodigo().isEmpty();
	}

}