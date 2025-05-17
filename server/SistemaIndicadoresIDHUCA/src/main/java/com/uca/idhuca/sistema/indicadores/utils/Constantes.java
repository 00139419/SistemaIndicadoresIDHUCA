package com.uca.idhuca.sistema.indicadores.utils;

public class Constantes {

	/**
	 * Constantes para la raiz de conexto de los servicios en general
	 */
	public static final String ROOT_CONTEXT = "idhuca-indicadores/api/srv/";
	
	/**
	 * Contantes para el manejo de respuesta genericas
	 */
	public static final int OK = 0;
	public static final int ERROR = -1;
	
	/**
	 * Constantes para los roles
	 * 
	 */
	public static final String ROL_ADMINISTRADOR = "admin";
	public static final String ROL_USER = "user";
	public static final String ROL_AYUDANTE = "ayudante";
	
	/**
	 * Constantes para parametros del sistema
	 * 
	 */
	public static final String MAX_INTENTOS_PREGUNTA_SEGURIDAD = "max_intentos_pregunta_seguridad";
	
	/**
	 * Prefijo de catalogos
	 */
	public static final String CATALOGO_ROL = "ROL_";
	public static final String CATALOGO_SECURITY_QUESTION = "SQ_";
	public static final String CATALOGO_DEPARTAMENTO = "DEP_";
	public static final String CATALOGO_MUNICIPIO = "MUN_";
	public static final String CATALOGO_DERECHO = "DER_";
	
	/**
	 * Operaciones CRUD para auditoria
	 */
	public static final String CREAR = "crear";
	public static final String READ = "leer";
	public static final String DELETE = "eliminar";
	public static final String UPDATE = "actualizar";
	
	/**
	 * Tablas modificables para auditoria
	 */
	public static final String REGISTROS = "registros";
	public static final String USUARIOS = "usuarios";
	public static final String CATALOGOS = "catalogos";
	public static final String FICHA_DE_DERECHO = "ficha de derecho";

}