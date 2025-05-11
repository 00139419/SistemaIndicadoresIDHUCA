package com.uca.idhuca.sistema.indicadores.services;

import java.util.List;

import com.uca.idhuca.sistema.indicadores.controllers.dto.AddUserDto;
import com.uca.idhuca.sistema.indicadores.dto.GenericEntityResponse;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.exceptions.NotFoundException;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Usuario;

public interface IUser {
	/**
     * Agrega un nuevo usuario al sistema.
     *
     * @param request objeto con los datos del usuario a agregar.
     * @return respuesta genérica indicando éxito o error.
     * @throws ValidationException si los datos de entrada no son válidos o el usuario ya existe.
     */
    SuperGenericResponse add(AddUserDto request) throws ValidationException;

    /**
     * Elimina un usuario existente del sistema por su ID.
     *
     * @param id identificador del usuario a eliminar.
     * @return respuesta genérica indicando éxito o error.
     * @throws ValidationException si el ID no es válido.
     * @throws NotFoundException si el usuario no existe.
     */
    SuperGenericResponse delete(Integer id) throws ValidationException, NotFoundException;

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param request objeto con los nuevos datos del usuario.
     * @return respuesta genérica indicando éxito o error.
     * @throws ValidationException si los datos no son válidos.
     * @throws NotFoundException si el usuario no existe.
     */
    SuperGenericResponse update(AddUserDto request) throws ValidationException, NotFoundException;

    /**
     * Obtiene los datos de un usuario por su ID.
     *
     * @param id identificador del usuario a consultar.
     * @return respuesta con el usuario encontrado.
     * @throws ValidationException si el ID no es válido.
     * @throws NotFoundException si el usuario no existe.
     */
    GenericEntityResponse<Usuario> getOne(Integer id) throws ValidationException, NotFoundException;

    /**
     * Obtiene la lista de todos los usuarios del sistema.
     *
     * @return respuesta con la lista de usuarios.
     * @throws ValidationException si ocurre una validación inesperada (aunque actualmente no se valida nada aquí).
     * @throws NotFoundException si no hay usuarios registrados.
     */
    GenericEntityResponse<List<Usuario>> getAll() throws ValidationException, NotFoundException;
}
