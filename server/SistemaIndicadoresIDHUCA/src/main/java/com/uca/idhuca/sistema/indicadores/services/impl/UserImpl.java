package com.uca.idhuca.sistema.indicadores.services.impl;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarAddUser;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarIdGiven;
import static com.uca.idhuca.sistema.indicadores.utils.RequestValidations.validarUpdateUser;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.controllers.dto.AddUserDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.IRepoUsuario;
import com.uca.idhuca.sistema.indicadores.services.IUser;
import com.uca.idhuca.sistema.indicadores.useCase.UserUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserImpl implements IUser {

	@Autowired
	IRepoUsuario userRepo;
	
	@Autowired
	private UserUseCase useCase;
	
	@Override
	public SuperGenericResponse add(AddUserDto request) throws ValidationException {
		List<String> errorsList = validarAddUser(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}

		String key = request.getEmail();
		log.info("[{}] Request válido", key);
		
		
		Usuario usuario = null;
		try {
			usuario = userRepo
					 .findByEmail(request.getEmail()).get();
			
			if(usuario != null) {
				System.out.println("Usuario ya existe.");
				throw new ValidationException(ERROR, "Usuario ya existe.");
			}
		} catch (NoSuchElementException e) {
			System.out.println("Usuario no existe.");
		}
		 log.info("[{}] Usuario válido", key);
		 
		 Usuario newUser = useCase.darFormatoInsert(request);
		 log.info("[{}] Creando usuario...",key);
		 
		 userRepo.save(newUser);
		 log.info("[{}] Usuario guardado correctamente.",key);
		 
		return new SuperGenericResponse(OK, "Usuario guardado correctamente.");
	}

	@Override
	public SuperGenericResponse delete(Integer id) throws ValidationException, NotFoundException {
		List<String> errorsList = validarIdGiven(id);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}
		
		Usuario usuario = null;
		try {
			usuario = userRepo
					 .findById(id.longValue()).get();
		} catch (NoSuchElementException e) {
			System.out.println("Usuario no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		log.info("[{}] Usuario encontrado correctamente.", "ADMIN");
		
		userRepo.delete(usuario);
		log.info("[{}] Usuario eliminado correctamente.", "ADMIN");
		
		return new SuperGenericResponse(OK, "Usuario elimindo correctamente.");
	}

	@Override
	public SuperGenericResponse update(AddUserDto request) throws ValidationException, NotFoundException {
		List<String> errorsList = validarUpdateUser(request);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}
		
		Usuario usuario = null;
		try {
			usuario = userRepo
					 .findById(request.getId()).get();
		} catch (NoSuchElementException e) {
			System.out.println("Usuario no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		log.info("[{}] Usuario encontrado correctamente.", "ADMIN");
		
		useCase.darFormatoUpdate(request, usuario);
		log.info("[{}] Actualizando usuario... [{}]", "ADMIN", usuario);
		
		userRepo.save(usuario);
		log.info("[{}] Usuario actualizado correctamente.","ADMNIN");
		 
		return new SuperGenericResponse(OK, "Usuario actualizado correctamente.");
	}

	@Override
	public GenericEntityResponse<Usuario> getOne(Integer id) throws ValidationException, NotFoundException {
		List<String> errorsList = validarIdGiven(id);
		if (!errorsList.isEmpty()) {
			throw new ValidationException(ERROR, errorsList.get(0));
		}
		
		Usuario usuario = null;
		try {
			usuario = userRepo
					 .findById(id.longValue()).get();
		} catch (NoSuchElementException e) {
			System.out.println("Usuario no existe.");
			throw new NotFoundException(ERROR, "Usuario no existe.");
		}
		
		log.info("[{}] Usuario encontrado correctamente.", "ADMIN");
		
		return new GenericEntityResponse<Usuario>(OK, "Usuario elimindo correctamente.", usuario);
	}

	@Override
	public GenericEntityResponse<List<Usuario>> getAll() throws ValidationException, NotFoundException {
		List<Usuario> users = userRepo.findAll();
		
		if(users == null || users.size() == 0) {
			System.out.println("No hay usuarios en la base de datos.");
			throw new NotFoundException(ERROR, "No hay usuarios en la base de datos.");
		}
		log.info("[{}] Usuarios obtenidos correctamente.", "ADMIN");
		
		return new GenericEntityResponse<List<Usuario>>(OK, "Usuario elimindo correctamente.", users);
	}

}
