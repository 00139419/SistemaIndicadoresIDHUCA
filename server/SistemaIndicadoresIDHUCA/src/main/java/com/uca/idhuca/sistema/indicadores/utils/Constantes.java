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
	
	
	
	public static final String CATALOGO_TIPO_PROCESO_JUDICIAL = "TIPO_PROCESO_JUDICIAL_";
	public static final String CATALOGO_TIPO_DENUNCIANTE = "TIPO_DENUNCIANTE_";
	public static final String CATALOGO_DURACION_PROCESO = "DURACION_PROCESO_";
	public static final String CATALOGO_TIPO_DE_REPRESION = "TIPO_REPRESION_";
	public static final String CATALOGO_MEDIO_DE_EXPRESION = "MEDIO_EXPRESION_";
	public static final String CATALOGO_MOTIVO_DETENCION = "MOTIVO_DETENCION_";
	public static final String CATALOGO_TIPO_DE_ARMA = "ARTEFACTO_";
	public static final String CATALOGO_TIPO_DE_DETENCION = "DETENCION_";
	public static final String CATALOGO_TIPO_DE_VIOLENCIA = "TIPOVIOLENCIA_";
	public static final String CATALOGO_ESTADO_SALUD = "ESTSALUD_";
	public static final String CATALOGO_TIPO_PERSONA = "TIPOPER_";
	public static final String CATALOGO_GENERO = "GEN_";
	public static final String CATALOGO_ROL = "ROL_";
	public static final String CATALOGO_SECURITY_QUESTION = "SQ_";
	public static final String CATALOGO_DEPARTAMENTO = "DEP_";
	public static final String CATALOGO_MUNICIPIO = "MUN_";
	public static final String CATALOGO_DERECHO = "DER_";
	public static final String CATALOGO_SUB_DERECHO = "SUBDER_";
	public static final String CATALOGO_PAISES = "PAIS_";
	public static final String CATALOGO_FUENTE = "FUENTE_";
	public static final String CATALOGO_ESTADO_DEL_REGISTROS = "ESTREG_";
	public static final String CATALOGO_LUGAR_EXACTO = "LUGEXAC_";
	
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