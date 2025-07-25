--
-- PostgreSQL database dump
--

-- Dumped from database version 15.13 (Debian 15.13-1.pgdg120+1)
-- Dumped by pg_dump version 16.9 (Ubuntu 16.9-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: acceso_justicia; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.acceso_justicia (
    justicia_id bigint NOT NULL,
    persona_id bigint,
    tipo_proceso_codigo character varying(255),
    fecha_denuncia timestamp(6) without time zone,
    tipo_denunciante_codigo character varying(255),
    duracion_proceso_codigo character varying(255),
    acceso_abogado boolean,
    hubo_parcialidad boolean,
    resultado_proceso character varying(255),
    instancia character varying(255)
);


ALTER TABLE public.acceso_justicia OWNER TO miusuario;

--
-- Name: acceso_justicia_justicia_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.acceso_justicia_justicia_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.acceso_justicia_justicia_id_seq OWNER TO miusuario;

--
-- Name: acceso_justicia_justicia_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.acceso_justicia_justicia_id_seq OWNED BY public.acceso_justicia.justicia_id;


--
-- Name: auditoria; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.auditoria (
    id bigint NOT NULL,
    usuario_id bigint,
    tabla_afectada character varying(255) NOT NULL,
    operacion character varying(255) NOT NULL,
    registro_id character varying(255),
    descripcion text,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.auditoria OWNER TO miusuario;

--
-- Name: auditoria_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.auditoria_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.auditoria_id_seq OWNER TO miusuario;

--
-- Name: auditoria_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.auditoria_id_seq OWNED BY public.auditoria.id;


--
-- Name: catalogo; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.catalogo (
    codigo character varying(255) NOT NULL,
    descripcion character varying(255)
);


ALTER TABLE public.catalogo OWNER TO miusuario;

--
-- Name: derecho_vulnerado; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.derecho_vulnerado (
    id bigint NOT NULL,
    persona_afectada_id bigint,
    derecho_vulnerado_codigo character varying(255)
);


ALTER TABLE public.derecho_vulnerado OWNER TO miusuario;

--
-- Name: derecho_vulnerado_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.derecho_vulnerado_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.derecho_vulnerado_id_seq OWNER TO miusuario;

--
-- Name: derecho_vulnerado_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.derecho_vulnerado_id_seq OWNED BY public.derecho_vulnerado.id;


--
-- Name: detencion_integridad; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.detencion_integridad (
    detencion_id bigint NOT NULL,
    persona_id bigint,
    tipo_detencion_codigo character varying(255),
    orden_judicial boolean,
    autoridad_involucrada_codigo character varying(255),
    hubo_tortura boolean,
    duracion_dias integer,
    acceso_abogado boolean,
    resultado character varying(255),
    motivo_detencion_codigo character varying(255)
);


ALTER TABLE public.detencion_integridad OWNER TO miusuario;

--
-- Name: detencion_integridad_detencion_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.detencion_integridad_detencion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.detencion_integridad_detencion_id_seq OWNER TO miusuario;

--
-- Name: detencion_integridad_detencion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.detencion_integridad_detencion_id_seq OWNED BY public.detencion_integridad.detencion_id;


--
-- Name: expresion_censura; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.expresion_censura (
    expresion_id bigint NOT NULL,
    persona_id bigint,
    medio_expresion_codigo character varying(255),
    tipo_represion_codigo character varying(255),
    represalias_legales boolean,
    represalias_fisicas boolean,
    actor_censor_codigo character varying(255),
    consecuencia character varying(255)
);


ALTER TABLE public.expresion_censura OWNER TO miusuario;

--
-- Name: expresion_censura_expresion_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.expresion_censura_expresion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.expresion_censura_expresion_id_seq OWNER TO miusuario;

--
-- Name: expresion_censura_expresion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.expresion_censura_expresion_id_seq OWNED BY public.expresion_censura.expresion_id;


--
-- Name: nota_derecho; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.nota_derecho (
    id bigint NOT NULL,
    derecho_codigo character varying(255),
    fecha timestamp(6) without time zone NOT NULL,
    titulo character varying(255),
    descripcion text,
    creado_por bigint,
    creado_en timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modificado_por bigint,
    modificado_en timestamp without time zone
);


ALTER TABLE public.nota_derecho OWNER TO miusuario;

--
-- Name: nota_derecho_archivo; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.nota_derecho_archivo (
    id bigint NOT NULL,
    nota_id bigint,
    nombre_original character varying(255),
    archivo_url character varying(255),
    tipo character varying(255)
);


ALTER TABLE public.nota_derecho_archivo OWNER TO miusuario;

--
-- Name: nota_derecho_archivo_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.nota_derecho_archivo_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.nota_derecho_archivo_id_seq OWNER TO miusuario;

--
-- Name: nota_derecho_archivo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.nota_derecho_archivo_id_seq OWNED BY public.nota_derecho_archivo.id;


--
-- Name: nota_derecho_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.nota_derecho_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.nota_derecho_id_seq OWNER TO miusuario;

--
-- Name: nota_derecho_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.nota_derecho_id_seq OWNED BY public.nota_derecho.id;


--
-- Name: parametro_sistema; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.parametro_sistema (
    id bigint NOT NULL,
    clave character varying(100) NOT NULL,
    valor text NOT NULL,
    descripcion text,
    actualizado_en timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.parametro_sistema OWNER TO miusuario;

--
-- Name: parametro_sistema_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.parametro_sistema_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.parametro_sistema_id_seq OWNER TO miusuario;

--
-- Name: parametro_sistema_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.parametro_sistema_id_seq OWNED BY public.parametro_sistema.id;


--
-- Name: persona_afectada; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.persona_afectada (
    id bigint NOT NULL,
    evento_id bigint,
    nombre character varying(255),
    genero_codigo character varying(255),
    edad integer,
    nacionalidad_codigo character varying(255),
    dep_artamento_residencia_codigo character varying(255),
    municipio_residencia_codigo character varying(255),
    tipo_persona_codigo character varying(255),
    estado_salud_codigo character varying(255)
);


ALTER TABLE public.persona_afectada OWNER TO miusuario;

--
-- Name: persona_afectada_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.persona_afectada_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.persona_afectada_id_seq OWNER TO miusuario;

--
-- Name: persona_afectada_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.persona_afectada_id_seq OWNED BY public.persona_afectada.id;


--
-- Name: recovery_password; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.recovery_password (
    id bigint NOT NULL,
    usuario_id bigint,
    pregunta_codigo character varying(255) NOT NULL,
    respuesta_hash character varying(255) NOT NULL,
    intentos_fallidos integer DEFAULT 0,
    actualizado_en timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.recovery_password OWNER TO miusuario;

--
-- Name: recovery_password_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.recovery_password_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.recovery_password_id_seq OWNER TO miusuario;

--
-- Name: recovery_password_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.recovery_password_id_seq OWNED BY public.recovery_password.id;


--
-- Name: registro_evento; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.registro_evento (
    id bigint NOT NULL,
    fecha_registro timestamp without time zone,
    fecha_hecho date,
    fuente_codigo character varying(255),
    estado_actual_codigo character varying(255),
    derecho_asociado_codigo character varying(255),
    flag_violencia boolean,
    flag_detencion boolean,
    flag_expresion boolean,
    flag_justicia boolean,
    flag_censura boolean,
    flag_regimen_excepcion boolean,
    observaciones text,
    creado_por bigint
);


ALTER TABLE public.registro_evento OWNER TO miusuario;

--
-- Name: registro_evento_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.registro_evento_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.registro_evento_id_seq OWNER TO miusuario;

--
-- Name: registro_evento_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.registro_evento_id_seq OWNED BY public.registro_evento.id;


--
-- Name: ubicacion; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.ubicacion (
    id bigint NOT NULL,
    evento_id bigint,
    dep_artamento_codigo character varying(255),
    municipio_codigo character varying(255),
    lugar_exacto_codigo character varying(255)
);


ALTER TABLE public.ubicacion OWNER TO miusuario;

--
-- Name: ubicacion_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.ubicacion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ubicacion_id_seq OWNER TO miusuario;

--
-- Name: ubicacion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.ubicacion_id_seq OWNED BY public.ubicacion.id;


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.usuario (
    id bigint NOT NULL,
    nombre character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    contrasena_hash character varying(255) NOT NULL,
    rol_codigo character varying(255),
    activo boolean DEFAULT true,
    es_provisional boolean DEFAULT true,
    creado_en timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.usuario OWNER TO miusuario;

--
-- Name: usuario_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.usuario_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuario_id_seq OWNER TO miusuario;

--
-- Name: usuario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.usuario_id_seq OWNED BY public.usuario.id;


--
-- Name: violencia; Type: TABLE; Schema: public; Owner: miusuario
--

CREATE TABLE public.violencia (
    violencia_id bigint NOT NULL,
    persona_id bigint,
    es_asesinato boolean,
    tipo_violencia_codigo character varying(255),
    artefacto_utilizado_codigo character varying(255),
    contexto_codigo character varying(255),
    actor_responsable_codigo character varying(255),
    estado_salud_actor_responsable_codigo character varying(255),
    hubo_proteccion boolean,
    investigacion_abierta boolean,
    respuesta_estado character varying(255)
);


ALTER TABLE public.violencia OWNER TO miusuario;

--
-- Name: violencia_violencia_id_seq; Type: SEQUENCE; Schema: public; Owner: miusuario
--

CREATE SEQUENCE public.violencia_violencia_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.violencia_violencia_id_seq OWNER TO miusuario;

--
-- Name: violencia_violencia_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: miusuario
--

ALTER SEQUENCE public.violencia_violencia_id_seq OWNED BY public.violencia.violencia_id;


--
-- Name: acceso_justicia justicia_id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.acceso_justicia ALTER COLUMN justicia_id SET DEFAULT nextval('public.acceso_justicia_justicia_id_seq'::regclass);


--
-- Name: auditoria id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.auditoria ALTER COLUMN id SET DEFAULT nextval('public.auditoria_id_seq'::regclass);


--
-- Name: derecho_vulnerado id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.derecho_vulnerado ALTER COLUMN id SET DEFAULT nextval('public.derecho_vulnerado_id_seq'::regclass);


--
-- Name: detencion_integridad detencion_id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.detencion_integridad ALTER COLUMN detencion_id SET DEFAULT nextval('public.detencion_integridad_detencion_id_seq'::regclass);


--
-- Name: expresion_censura expresion_id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.expresion_censura ALTER COLUMN expresion_id SET DEFAULT nextval('public.expresion_censura_expresion_id_seq'::regclass);


--
-- Name: nota_derecho id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.nota_derecho ALTER COLUMN id SET DEFAULT nextval('public.nota_derecho_id_seq'::regclass);


--
-- Name: nota_derecho_archivo id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.nota_derecho_archivo ALTER COLUMN id SET DEFAULT nextval('public.nota_derecho_archivo_id_seq'::regclass);


--
-- Name: parametro_sistema id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.parametro_sistema ALTER COLUMN id SET DEFAULT nextval('public.parametro_sistema_id_seq'::regclass);


--
-- Name: persona_afectada id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada ALTER COLUMN id SET DEFAULT nextval('public.persona_afectada_id_seq'::regclass);


--
-- Name: recovery_password id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.recovery_password ALTER COLUMN id SET DEFAULT nextval('public.recovery_password_id_seq'::regclass);


--
-- Name: registro_evento id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.registro_evento ALTER COLUMN id SET DEFAULT nextval('public.registro_evento_id_seq'::regclass);


--
-- Name: ubicacion id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.ubicacion ALTER COLUMN id SET DEFAULT nextval('public.ubicacion_id_seq'::regclass);


--
-- Name: usuario id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.usuario ALTER COLUMN id SET DEFAULT nextval('public.usuario_id_seq'::regclass);


--
-- Name: violencia violencia_id; Type: DEFAULT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.violencia ALTER COLUMN violencia_id SET DEFAULT nextval('public.violencia_violencia_id_seq'::regclass);


--
-- Data for Name: acceso_justicia; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.acceso_justicia (justicia_id, persona_id, tipo_proceso_codigo, fecha_denuncia, tipo_denunciante_codigo, duracion_proceso_codigo, acceso_abogado, hubo_parcialidad, resultado_proceso, instancia) FROM stdin;
\.


--
-- Data for Name: auditoria; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.auditoria (id, usuario_id, tabla_afectada, operacion, registro_id, descripcion, fecha) FROM stdin;
\.


--
-- Data for Name: catalogo; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.catalogo (codigo, descripcion) FROM stdin;
ROL_1	admin
ROL_2	user
ROL_3	ayudante
SQ_1	¿Cuál es el nombre de tu primera mascota?
SQ_2	¿Cuál es el nombre de tu escuela primaria?
SQ_3	¿En qué ciudad naciste?
SQ_4	¿Cuál es el segundo nombre de tu madre?
SQ_5	¿Cuál fue tu primer empleo?
SQ_6	¿Cuál es tu película favorita?
SQ_7	¿Cuál es tu comida favorita de la infancia?
SQ_8	¿Cuál es el nombre de tu mejor amigo de la infancia?
SQ_9	¿Cuál es el nombre de tu primer profesor o profesora?
SQ_10	¿En qué calle viviste de niño?
DEP_0	Desconocido
DEP_1	AHUACHAPAN
DEP_2	SANTA ANA
DEP_3	SONSONATE
DEP_4	CHALATENANGO
DEP_5	LA LIBERTAD
DEP_6	SAN SALVADOR
DEP_7	CUSCATLAN
DEP_8	LA PAZ
DEP_9	CABANAS
DEP_10	SAN VICENTE
DEP_11	USULUTAN
DEP_12	SAN MIGUEL
DEP_13	MORAZAN
DEP_14	LA UNION
MUN_0_0	Desconocido
MUN_1_1	AHUACHAPAN
MUN_1_2	APANECA
MUN_1_3	ATIQUIZAYA
MUN_1_4	CONCEPCION DE ATACO
MUN_1_5	EL REFUGIO
MUN_1_6	GUAYMANGO
MUN_1_7	JUJUTLA
MUN_1_8	SAN FRANCISCO MENENDEZ
MUN_1_9	SAN LORENZO
MUN_1_10	SAN PEDRO PUXTLA
MUN_1_11	TACUBA
MUN_1_12	TURIN
MUN_2_1	CANDELARIA DE LA FRONTERA
MUN_2_2	COATEPEQUE
MUN_2_3	CHALCHUAPA
MUN_2_4	EL CONGO
MUN_2_5	EL PORVENIR
MUN_2_6	MASAHUAT
MUN_2_7	METAPAN
MUN_2_8	SAN ANTONIO PAJONAL
MUN_2_9	SAN SEBASTIAN SALITRILLO
MUN_2_10	SANTA ANA
MUN_2_11	SANTA ROSA GUACHIPILIN
MUN_2_12	SANTIAGO DE LA FRONTERA
MUN_2_13	TEXISTEPEQUE
MUN_3_1	ACAJUTLA
MUN_3_2	ARMENIA
MUN_3_3	CALUCO
MUN_3_4	CUISNAHUAT
MUN_3_5	SANTA ISABEL ISHUATAN
MUN_3_6	IZALCO
MUN_3_7	JUAYUA
MUN_3_8	NAHUIZALCO
MUN_3_9	NAHUILINGO
MUN_3_10	SALCOATITAN
MUN_3_11	SAN ANTONIO DEL MONTE
MUN_3_12	SAN JULIAN
MUN_3_13	SANTA CATARINA MASAHUAT
MUN_3_14	SANTO DOMINGO GUZMAN
MUN_3_15	SONSONATE
MUN_3_16	SONZACATE
MUN_4_1	AGUA CALIENTE
MUN_4_2	ARCATAO
MUN_4_3	AZACUALPA
MUN_4_4	CITALA
MUN_4_5	COMALAPA
MUN_4_6	CONCEPCION QUEZALTEPEQUE
MUN_4_7	CHALATENANGO
MUN_4_8	DULCE NOMBRE DE MARIA
MUN_4_9	EL CARRIZAL
MUN_4_10	EL PARAISO
MUN_4_11	LA LAGUNA
MUN_4_12	LA PALMA
MUN_4_13	LA REINA
MUN_4_14	LAS VUELTAS
MUN_4_15	NOMBRE DE JESUS
MUN_4_16	NUEVA CONCEPCION
MUN_4_17	NUEVA TRINIDAD
MUN_4_18	OJOS DE AGUA
MUN_4_19	POTONICO
MUN_4_20	SAN ANTONIO DE LA CRUZ
MUN_4_21	SAN ANTONIO LOS RANCHOS
MUN_4_22	SAN FERNANDO
MUN_4_23	SAN FRANCISCO LEMPA
MUN_4_24	SAN FRANCISCO MORAZAN
MUN_4_25	SAN IGNACIO
MUN_4_26	SAN ISIDRO LABRADOR
MUN_4_27	SAN J. CANCASQ_UE
MUN_4_28	SAN JOSE LAS FLORES
MUN_4_29	SAN LUIS DEL CARMEN
MUN_4_30	SAN  MIGUEL DE MERCEDES
MUN_4_31	SAN RAFAEL
MUN_4_32	SANTA RITA
MUN_4_33	TEJUTLA
MUN_5_1	ANTIGUO CUSCATLAN
MUN_5_2	CIUDAD ARCE
MUN_5_3	COLON
MUN_5_4	COMASAGUA
MUN_5_5	CHILTIUPAN
MUN_5_6	HUIZUCAR
MUN_5_7	JAYAQUE
MUN_5_8	JICALAPA
MUN_5_9	LA LIBERTAD
MUN_5_10	NUEVO CUSCATLAN
MUN_5_11	SANTA TECLA
MUN_5_12	QUEZALTEPEQUE
MUN_5_13	SACACOYO
MUN_5_14	SAN JOSE VILLANUEVA
MUN_5_15	SAN JUAN OPICO
MUN_5_16	SAN MATIAS
MUN_5_17	SAN PABLO TACACHICO
MUN_5_18	TAMANIQUE
MUN_5_19	TALNIQUE
MUN_5_20	TEOTEPEQUE
MUN_5_21	TEPECOYO
MUN_5_22	ZARAGOZA
MUN_6_1	AGUILARES
MUN_6_2	APOPA
MUN_6_3	AYUTUXTEPEQUE
MUN_6_4	CUSCATANCINGO
MUN_6_5	EL PAISNAL
MUN_6_6	GUAZAPA
MUN_6_7	ILOPANGO
MUN_6_8	MEJICANOS
MUN_6_9	NEJAPA
MUN_6_10	PANCHIMALCO
MUN_6_11	ROSARIO DE MORA
MUN_6_12	SAN MARCOS
MUN_6_13	SAN MARTIN
MUN_6_14	SAN SALVADOR
MUN_6_15	SANTIAGO TEXACUANGOS
MUN_6_16	SANTO TOMAS
MUN_6_17	SOYAPANGO
MUN_6_18	TONACATEPEQUE
MUN_6_19	CIUDAD DELGADO
MUN_7_1	CANDELARIA
MUN_7_2	COJUTEPEQUE
MUN_7_3	EL CARMEN
MUN_7_4	EL ROSARIO
MUN_7_5	MONTE SAN JUAN
MUN_7_6	ORATORIO DE CONCEPCION
MUN_7_7	SAN BARTOLOME PERULAPIA
MUN_7_8	SAN CRISTOBAL
MUN_7_9	SAN JOSE GUAYABAL
MUN_7_10	SAN PEDRO PERULAPAN
MUN_7_11	SAN RAFAEL CEDROS
MUN_7_12	SAN RAMON
MUN_7_13	SANTA CRUZ ANALQUITO
MUN_7_14	SANTA CRUZ MICHAPA
MUN_7_15	SUCHITOTO
MUN_7_16	TENANCINGO
MUN_8_1	CUYULTITAN
MUN_8_2	ROSARIO LA PAZ
MUN_8_3	JERUSALEN
MUN_8_4	MERCEDES LA CEIBA
MUN_8_5	OLOCUILTA
MUN_8_6	PARAISO DE OSORIO
MUN_8_7	SAN ANTONIO MASAHUAT
MUN_8_8	SAN EMIGDIO
MUN_8_9	SAN FRANCISCO CHINAMECA
MUN_8_10	SAN JUAN NONUALCO
MUN_8_11	SAN JUAN TALPA
MUN_8_12	SAN JUAN TEPEZONTES
MUN_8_13	SAN LUIS TALPA
MUN_8_14	SAN MIGUEL TEPEZONTES
MUN_8_15	SAN PEDRO MASAHUAT
MUN_8_16	SAN PEDRO NONUALCO
MUN_8_17	SAN RAFAEL OBRAJUELO
MUN_8_18	SANTA MARIA OSTUMA
MUN_8_19	SANTIAGO NONUALCO
MUN_8_20	TAPALHUACA
MUN_8_21	ZACATECOLUCA
MUN_8_22	SAN LUIS LA HERRADURA
MUN_9_1	CINQUERA
MUN_9_2	GUACOTECTI
MUN_9_3	ILOBASCO
MUN_9_4	JUTIAPA
MUN_9_5	SAN ISIDRO
MUN_9_6	SENSUNTEPEQUE
MUN_9_7	TEJUTEPEQUE
MUN_9_8	VICTORIA
MUN_9_9	VILLA DOLORES
MUN_10_1	APASTEPEQUE
MUN_10_2	GUADALUPE
MUN_10_4	SANTA CLARA
MUN_10_5	SANTO DOMINGO
MUN_10_3	SAN CAYETANO ISTEPEQUE
MUN_10_6	SAN ESTEBAN CATARINA
MUN_10_7	SAN ILDEFONSO
MUN_10_8	SAN LORENZO
MUN_10_9	SAN SEBASTIAN
MUN_10_10	SAN VICENTE
MUN_10_11	TECOLUCA
MUN_10_12	TEPETITAN
MUN_10_13	VERAPAZ
MUN_11_1	ALEGRIA
MUN_11_2	BERLIN
MUN_11_3	CALIFORNIA
MUN_11_4	CONCEPCION BATRES
MUN_11_5	EL TRIUNFO
MUN_11_6	EREGUAYQUIN
MUN_11_7	ESTANZUELAS
MUN_11_8	JIQUILISCO
MUN_11_9	JUCUAPA
MUN_11_10	JUCUARAN
MUN_11_11	MERCEDES UMAÑA
MUN_11_12	NUEVA GRANADA
MUN_11_13	OZATLAN
MUN_11_14	PUERTO EL TRIUNFO
MUN_11_15	SAN AGUSTIN
MUN_11_16	SAN BUENAVENTURA
MUN_11_17	SAN DIONISIO
MUN_11_19	SAN FRANCISCO JAVIER
MUN_11_18	SANTA ELENA
MUN_11_20	SANTA MARIA
MUN_11_21	SANTIAGO DE MARIA
MUN_11_22	TECAPAN
MUN_11_23	USULUTAN
MUN_12_1	CAROL_INA
MUN_12_2	CIUDAD BARRIOS
MUN_12_3	COMACARAN
MUN_12_4	CHAPELTIQUE
MUN_12_5	CHINAMECA
MUN_12_6	CHIRILAGUA
MUN_12_7	EL TRANSITO
MUN_12_8	LOLOTIQUE
MUN_12_9	MONCAGUA
MUN_12_10	NUEVA GUADALUPE
MUN_12_11	NUEVO EDEN DE SAN JUAN
MUN_12_12	QUELEPA
MUN_12_13	SAN ANTONIO DEL MOSCO
MUN_12_14	SAN GERARDO
MUN_12_15	SAN JORGE
MUN_12_16	SAN LUIS REINA
MUN_12_17	SAN MIGUEL
MUN_12_18	SAN RAFAEL ORIENTE
MUN_12_19	SESORI
MUN_12_20	ULUAZAPA
MUN_13_1	ARAMBALA
MUN_13_2	CACAOPERA
MUN_13_3	CORINTO
MUN_13_4	CHILANGA
MUN_13_5	DELICIAS DE CONCEPCION
MUN_13_6	EL DIVISADERO
MUN_13_7	EL ROSARIO
MUN_13_8	GUALOCOCTI
MUN_13_9	GUATAJIAGUA
MUN_13_10	JOATECA
MUN_13_11	JOCOAITIQUE
MUN_13_12	JOCORO
MUN_13_13	LOLOTIQUILLO
MUN_13_14	MEANGUERA
MUN_13_15	OSICALA
MUN_13_16	PERQUIN
MUN_13_17	SAN CARLOS
MUN_13_18	SAN FERNANDO
MUN_13_19	SAN FRANCISCO GOTERA
MUN_13_20	SAN ISIDRO
MUN_13_21	SAN SIMON
MUN_13_22	SENSEMBRA
MUN_13_23	SOCIEDAD
MUN_13_24	TOROL_A
MUN_13_25	YAMABAL
MUN_13_26	YOLOAIQUIN
MUN_14_1	ANAMOROS
MUN_14_2	BOLIVAR
MUN_14_3	CONCEPCION DE ORIENTE
MUN_14_4	CONCHAGUA
MUN_14_5	EL CARMEN
MUN_14_6	EL SAUCE
MUN_14_7	INTIPUCA
MUN_14_8	LA UNION
MUN_14_9	LISLIQUE
MUN_14_10	MEANGUERA DEL GOLFO
MUN_14_11	NUEVA ESPARTA
MUN_14_12	PASAQUINA
MUN_14_13	POLOROS
MUN_14_14	SAN ALEJO
MUN_14_15	SAN JOSE
MUN_14_16	SANTA ROSA DE LIMA
MUN_14_17	YAYANTIQUE
MUN_14_18	YUCUAIQUIN
PAIS_0	Desconocido
PAIS_9300	EL SALVADOR
PAIS_9303	AFGANISTAN
PAIS_9306	ALBANIA
PAIS_9309	ALEMANIA OCCIDENTAL
PAIS_9310	ALEMANIA ORIENTAL
PAIS_9315	ALTO VOLTA
PAIS_9317	ANDORRA
PAIS_9318	ANGOLA
PAIS_9319	ANTIGUA Y BARBUDA
PAIS_9324	ARABIA SAUDITA
PAIS_9327	ARGELIA
PAIS_9330	ARGENTINA
PAIS_9333	AUSTRALIA
PAIS_9336	AUSTRIA
PAIS_9339	BANGLADESH
PAIS_9342	BEHREIN
PAIS_9345	BARBADOS
PAIS_9348	BELGICA
PAIS_9349	BELICE
PAIS_9350	BENIN
PAIS_9354	BIRMANIA
PAIS_9357	BOLIVIA
PAIS_9360	BOTSWANA
PAIS_9363	BRASIL
PAIS_9366	BRUNEI
PAIS_9369	BULGARIA
PAIS_9372	BURUNDI
PAIS_9374	BOPHUTHATSWANA
PAIS_9375	BUTAN
PAIS_9377	CABO VERDE
PAIS_9378	CAMBOYA
PAIS_9381	CAMERUM
PAIS_9384	CANADA
PAIS_9387	CEILAN
PAIS_9390	CENTRO AFRICA REPUBLICA
PAIS_9393	COLOMBIA
PAIS_9394	COMORA-ISLAS
PAIS_9396	CONGO REPUBLICA DEL
PAIS_9399	CONGO REPUBLICA DEMOCRATICA
PAIS_9402	COREA NORTE
PAIS_9405	COREA SUR
PAIS_9408	COSTA MARFIL
PAIS_9411	COSTA RICA
PAIS_9414	CUBA
PAIS_9417	CHAD
PAIS_9420	CHECOSLOVAQUIA
PAIS_9423	CHILE
PAIS_9426	CHINA REPUBLICA POPULAR
PAIS_9432	CHIPRE
PAIS_9435	DAHOMEY
PAIS_9438	DINAMARCA
PAIS_9439	DJIBOUTI
PAIS_9440	DOMINICA
PAIS_9441	DOMINICANA REPUBLICA
PAIS_9444	ECUADOR
PAIS_9446	EMIRATOS ARABES UNIDOS
PAIS_9447	ESPANA
PAIS_9450	ESTADOS UNIDOS DE AMERICA
PAIS_9453	ETIOPIA
PAIS_9456	FIJI-ISLAS
PAIS_9459	FILIPINAS
PAIS_9462	FINLANDIA
PAIS_9465	FRANCIA
PAIS_9468	GABON
PAIS_9471	GAMBIA
PAIS_9474	GHANA
PAIS_9477	GIBRALTAR
PAIS_9480	GRECIA
PAIS_9481	GRENADA
PAIS_9483	GUATEMALA
PAIS_9486	GUINEA
PAIS_9487	GUYANA
PAIS_9495	HAITI
PAIS_9498	HOLANDA
PAIS_9501	HONDURAS
PAIS_9504	HONG KONG
PAIS_9507	HUNGRIA
PAIS_9510	INDIA
PAIS_9513	INDONESIA
PAIS_9516	IRAK
PAIS_9519	IRAN
PAIS_9522	IRLANDA
PAIS_9525	ISLANDIA
PAIS_9526	ISLAS SALOMON
PAIS_9528	ISRAEL
PAIS_9531	ITALIA
PAIS_9534	JAMAICA
PAIS_9537	JAPON
PAIS_9540	JORDANIA
PAIS_9543	KENIA
PAIS_9544	KIRIBATI
PAIS_9546	KUWAIT
PAIS_9549	LAOS
PAIS_9552	LESOTHO
PAIS_9555	LIBANO
PAIS_9558	LIBERIA
PAIS_9561	LIBIA
PAIS_9564	LIECHTENSTEIN
PAIS_9567	LUXEMBURGO
PAIS_9570	MADAGASCAR
PAIS_9573	MALASIA
PAIS_9576	MALAWI
PAIS_9577	MALDIVAS
PAIS_9579	MALI
PAIS_9582	MALTA
PAIS_9585	MARRUECOS
PAIS_9591	MASCATE Y OMAN
PAIS_9594	MAURICIO
PAIS_9597	MAURITANIA
PAIS_9600	MEXICO
PAIS_9601	MICRONESIA
PAIS_9603	MONACO
PAIS_9606	MONGOLIA
PAIS_9609	MOZAMBIQUE
PAIS_9611	NAURU
PAIS_9612	NEPAL
PAIS_9615	NICARAGUA
PAIS_9618	NIGER
PAIS_9621	NIGERIA
PAIS_9624	NORUEGA
PAIS_9627	NUEVA CALEDONIA
PAIS_9633	NUEVA ZELANDIA
PAIS_9636	NUEVAS HEBRIDAS
PAIS_9638	PAPUA NUEVA GUINEA
PAIS_9639	PAKISTAN
PAIS_9642	PANAMA
PAIS_9645	PARAGUAY
PAIS_9648	PERU
PAIS_9651	POLONIA
PAIS_9654	PORTUGAL
PAIS_9660	QATAR EL
PAIS_9663	REINO UNIDO
PAIS_9666	EGIPTO
PAIS_9669	RODESIA
PAIS_9672	RUANDA
PAIS_9675	RUMANIA
PAIS_9677	SAN MARINO
PAIS_9678	SAMOA OCCIDENTAL
PAIS_9679	SAINT CHRIST NEVIS
PAIS_9680	SANTA LUCIA
PAIS_9681	SENEGAL
PAIS_9682	SAOTOME Y PRINCIPE
PAIS_9683	SAINT VINCENT Y GRENADINES
PAIS_9684	SIERRA LEONA
PAIS_9687	SINGAPUR
PAIS_9690	SIRIA
PAIS_9691	SEYCHELLES
PAIS_9693	SOMALIA
PAIS_9696	SUDAFRICA REPUBLICA
PAIS_9699	SUDAN
PAIS_9702	SUECIA
PAIS_9705	SUIZA
PAIS_9706	SURINAM
PAIS_9707	SRILANKA
PAIS_9708	SUECILANDIA
PAIS_9711	TAILANDIA
PAIS_9714	TANZANIA
PAIS_9717	TOGO
PAIS_9720	TRINIDAD Y TOBAGO
PAIS_9722	TONGA
PAIS_9723	TUNEZ
PAIS_9725	TRANSKEI
PAIS_9726	TURQUIA
PAIS_9727	TUVALU
PAIS_9729	UGANDA
PAIS_9732	UNION DE REPUBLICAS SOCIALISTAS SOV
PAIS_9735	URUGUAY
PAIS_9738	VATICANO
PAIS_9739	VANUATU
PAIS_9740	VENDA
PAIS_9741	VENEZUELA
PAIS_9744	VIETNAM NORTE
PAIS_9747	VIETNAM SUR
PAIS_9750	YEMEN SUR REPUBLICA
PAIS_9756	YUGOSLAVIA
PAIS_9758	ZAIRE
PAIS_9759	ZAMBIA
PAIS_9760	ZIMBABWE
PAIS_9850	PUERTO RICO
PAIS_9862	BAHAMAS
PAIS_9863	BERMUDAS
PAIS_9865	MARTINICA
PAIS_9886	NUEVA GUINEA
PAIS_9897	ISLAS VIRGENES BRITANICAS
PAIS_9898	ANTILLAS HOLANDESAS
PAIS_9899	TAIWAN
PAIS_9999	OTROS
PAIS_1008	BIELORRUSIA
PAIS_1010	BOSNIA Y HERZEGOVINA
PAIS_1041	MACEDONIA
PAIS_1047	MONTENEGRO
PAIS_1071	SERBIA
PAIS_1081	UCRANIA
PAIS_9304	ALAND
PAIS_1002	ALEMANIA
PAIS_1003	ANGUILA
PAIS_1006	ARUBA
PAIS_1007	AZERBAIYAN
PAIS_1012	BURKINA FASO
PAIS_1017	CURAZAO
PAIS_1020	ESLOVAQUIA
PAIS_1019	ESLOVENIA
PAIS_1018	ERITREA
PAIS_1023	GEORGIA
PAIS_1025	GROENLANDIA
PAIS_1026	GUADALUPE
PAIS_1027	GUAM
PAIS_1031	GUAYANA FRANCESA
PAIS_1028	GUERNSEY
PAIS_1030	GUINEA ECUATORIAL
PAIS_1029	GUINEA-BISSAU
PAIS_1033	ISLA DE MAN
PAIS_1015	ISLAS COOK
PAIS_1022	ISLAS FEROE
PAIS_9530	ISLAS AZORES
PAIS_1043	ISLAS MARIANAS DEL NORTE
PAIS_1079	ISLAS TURCAS Y CAICOS
PAIS_9532	ISLAS QESHM
PAIS_1044	ISLAS MARSHALL
PAIS_1042	ISLAS MALVINAS
PAIS_9538	ISLA PITCAIRN
PAIS_1036	KASAKISTAN
PAIS_1034	ISLAS ULTRAMARINAS DE EEUU
PAIS_1084	ISLAS VIRGENES ESTAUNIDENSES
PAIS_1037	KIRGUISTAN
PAIS_1038	LETONIA
PAIS_1035	JERSEY
PAIS_1039	LITUANIA
PAIS_1040	MACAO
PAIS_1045	MAYOTTE
PAIS_1046	MOLDAVIA
PAIS_1048	MONSERRAT
PAIS_1050	NAMIBIA
PAIS_1052	NIUE
PAIS_1053	NORFOLK
PAIS_1016	CROACIA
PAIS_9643	PALAOS
PAIS_1058	POLINESIA FRANCESA
PAIS_1059	REPUBLICA CHEKA
PAIS_1060	REUNION
PAIS_1005	REPUBLICA DE ARMENIA
PAIS_1062	SAHARA OCCIDENTAL
PAIS_1064	SAMOA AMERICANA
PAIS_1070	SANTA ELENA
PAIS_1069	SAN PEDRO Y MIGUELON
PAIS_1073	SVALBARD Y JAN MAYEN
PAIS_1074	TAYIKISTAN
PAIS_1075	TERRITORIO BRIT DEL OCEANO INDICO
PAIS_1077	TIMOR ORIENTAL
PAIS_1076	TERRITORIOS AUSTRALES FRANCESES
PAIS_1078	TOKELAU
PAIS_1056	TERRITORIOS PALESTINOS
PAIS_1080	TURKMENISTAN
PAIS_1061	RUSIA
PAIS_1082	UZBEKISTAN
PAIS_1083	VIETNAM
PAIS_9751	YIBUTI
PAIS_1085	WALLIS Y FUTUNA
PAIS_1013	ISLAS GRAN CAIMAN
PAIS_1068	SAN MARTIN
PAIS_9457	ESTONIA
PAIS_9900	ANTARTIDA
PAIS_9901	BAREIN
PAIS_9902	ISLA BOUVET
PAIS_9903	ISLA DE NAVIDAD
PAIS_9904	ISLA PITCAIRN
PAIS_9905	ISLA SAN BARTOLOME
PAIS_9906	ISLAS ALAND
PAIS_9907	ISLAS COCOS
PAIS_9908	ISLAS HEARD Y MCDONALD
PAIS_9909	ISLAS JAN MAYEN
PAIS_9910	SAN CRISTOBAL Y NIEVES
PAIS_9911	SINT MAARTEN
PAIS_9912	SWASILANDIA
PAIS_9913	TERRITORIO BRITANICO DE ULTRAMAR
FUENTE_0	Otros
FUENTE_1	Facebook
FUENTE_2	Twitter/X
FUENTE_3	Instagram
FUENTE_4	YouTube
FUENTE_5	TikTok
FUENTE_6	LinkedIn
FUENTE_7	El Diario de Hoy
FUENTE_8	La Prensa Gráfica
FUENTE_9	El Mundo
FUENTE_10	Diario El Salvador
FUENTE_11	Diario Co Latino
FUENTE_12	El Faro
FUENTE_13	Contrapunto
FUENTE_14	Revista Factum
FUENTE_15	La Página
FUENTE_16	Canal 2 TCS
FUENTE_17	Canal 4 TCS
FUENTE_18	Canal 6 TCS
FUENTE_19	Canal 12
FUENTE_20	Canal 21
FUENTE_21	Canal 19
FUENTE_22	Canal 10(Canal Estatal)
FUENTE_23	Megavisión Canal 15
FUENTE_24	Megavisión Canal 19
FUENTE_25	Gentevé
FUENTE_26	YSKL Radio
FUENTE_27	Radio Sonora
FUENTE_28	Radio Nacional de El Salvador
FUENTE_29	Radio YSUCA
FUENTE_30	Radio Maya Visión
FUENTE_31	El Blog
FUENTE_32	Última Hora SV
FUENTE_33	Salud con lupa
FUENTE_34	Focos TV
FUENTE_35	Disruptiva.media
FUENTE_36	Alerta El Salvador
FUENTE_37	Teleprensa
FUENTE_38	La Britany
FUENTE_39	Noticias de El Salvador
FUENTE_40	Diario 1
FUENTE_41	Agencia EFE(oficina El Salvador)
FUENTE_42	Associated Press(AP)(cobertura El Salvador)
FUENTE_43	Reuters(cobertura El Salvador)
ESTREG_0	Desconocido
ESTREG_1	Caso en proceso judicial
ESTREG_2	Caso cerrado
ESTREG_3	Caso en investigación
ESTREG_4	Caso archivado
ESTREG_5	Caso con sentencia
ESTREG_6	Caso en apelación
DER_1	Derecho a la Libertad Personal e Integridad personal
DER_2	Derecho a la Libertad de Expresión
DER_3	Derecho de Acceso a la Justicia
DER_4	Derecho a la Vida
LUGEXAC_0	Desconocido
LUGEXAC_1	Fosa clandestina
LUGEXAC_2	Lugar público
LUGEXAC_3	Centro penitenciario
LUGEXAC_4	Instalación policial
LUGEXAC_5	Centro de detención migratoria
LUGEXAC_6	Hospital o centro de salud
LUGEXAC_7	Vivienda particular
LUGEXAC_8	Lugar de trabajo
LUGEXAC_9	Centro educativo
LUGEXAC_10	Instalación militar
LUGEXAC_11	Río, lago u otro cuerpo de agua
LUGEXAC_12	Carretera o vía pública
LUGEXAC_13	Zona rural remota
LUGEXAC_14	Zona urbana
LUGEXAC_15	Frontera o punto de control migratorio
LUGEXAC_16	Instalación estatal no identificada
LUGEXAC_17	Vehículo de transporte público
LUGEXAC_18	Vehículo policial o militar
LUGEXAC_19	Oficina pública o institución estatal
TIPOPER_0	Desconocido
TIPOPER_1	Persona particular
TIPOPER_2	Servidor público
TIPOPER_3	Miembro de la Policía Nacional Civil
TIPOPER_4	Miembro de la Fuerza Armada
TIPOPER_5	Funcionario del sistema judicial
TIPOPER_6	Autoridad municipal
TIPOPER_7	Personal de centros penitenciarios
TIPOPER_8	Empleado de salud pública
TIPOPER_9	Agente migratorio
TIPOPER_10	Miembro de cuerpo diplomático
TIPOPER_11	Persona no identificada
ESTSALUD_0	Desconocido
ESTSALUD_1	Sin afectaciones visibles
ESTSALUD_2	Lesiones leves
ESTSALUD_3	Lesiones graves
ESTSALUD_4	En estado crítico
ESTSALUD_5	Con discapacidad permanente
ESTSALUD_6	Fallecida
TIPOVIOLENCIA_0	Otro
TIPOVIOLENCIA_1	Violencia física
TIPOVIOLENCIA_2	Violencia psicológica
TIPOVIOLENCIA_3	Violencia sexual
TIPOVIOLENCIA_4	Violencia institucional
TIPOVIOLENCIA_5	Violencia simbólica
TIPOVIOLENCIA_6	Violencia estructural
TIPOVIOLENCIA_7	Violencia económica
TIPOVIOLENCIA_8	Violencia política
TIPOVIOLENCIA_9	Violencia digital
TIPOVIOLENCIA_10	Tortura o tratos crueles, inhumanos o degradantes
TIPOVIOLENCIA_11	Ejecución extrajudicial
TIPOVIOLENCIA_12	Desaparición forzada
GEN_0	Desconocido
GEN_1	Mujer
GEN_2	Hombre
ARTEFACTO_0	Desconocido
ARTEFACTO_1	Arma de fuego
ARTEFACTO_2	Cuchillo o arma blanca
ARTEFACTO_3	Objeto contundente
ARTEFACTO_4	Explosivo
ARTEFACTO_5	Gas lacrimógeno o químico
ARTEFACTO_6	Instrumento de tortura
ARTEFACTO_7	Vehículo
ARTEFACTO_8	Herramienta de inmovilización(esposas, grilletes, etc.)
ARTEFACTO_9	Dispositivo electrónico(taser, etc.)
DETENCION_0	Desconocido
DETENCION_1	Detención en flagrancia
DETENCION_2	Detención administrativa
DETENCION_3	Detención judicial
DETENCION_4	Detención sin orden judicial
DETENCION_5	Detención con uso excesivo de fuerza
DETENCION_6	Detención arbitraria
MOTIVO_DETENCION_0	Desconocido
MOTIVO_DETENCION_1	Presunto delito de robo
MOTIVO_DETENCION_2	Presunto delito de homicidio
MOTIVO_DETENCION_3	Presunto delito de extorsión
MOTIVO_DETENCION_4	Presunto delito de agrupaciones ilícitas
MOTIVO_DETENCION_5	Orden judicial por delito de estafa
MOTIVO_DETENCION_6	Desorden público
MOTIVO_DETENCION_7	Manifestación o protesta
MOTIVO_DETENCION_8	Falta administrativa
MOTIVO_DETENCION_9	Sospecha no especificada
MEDIO_EXPRESION_0	Otro
MEDIO_EXPRESION_1	Redes sociales
MEDIO_EXPRESION_2	Medio de comunicación impreso
MEDIO_EXPRESION_3	Medio de comunicación digital
MEDIO_EXPRESION_4	Radio
MEDIO_EXPRESION_5	Televisión
MEDIO_EXPRESION_6	Entrevista pública o conferencia
MEDIO_EXPRESION_7	Protesta o manifestación
MEDIO_EXPRESION_8	Artículo de opinión
MEDIO_EXPRESION_9	Blog o sitio web personal
TIPO_REPRESION_0	Desconocido
TIPO_REPRESION_1	Censura directa
TIPO_REPRESION_2	Amenaza verbal
TIPO_REPRESION_3	Agresión física
TIPO_REPRESION_4	Desacreditación pública
TIPO_REPRESION_5	Retiro de contenido o cierre de cuentas
TIPO_REPRESION_6	Judicialización o demanda
TIPO_REPRESION_7	Despido o sanción laboral
TIPO_REPRESION_8	Detención arbitraria
TIPO_REPRESION_9	Vigilancia o seguimiento
TIPO_PROCESO_JUDICIAL_0	Otro
TIPO_PROCESO_JUDICIAL_1	Proceso penal ordinario
TIPO_PROCESO_JUDICIAL_2	Proceso penal con detención provisional
TIPO_PROCESO_JUDICIAL_3	Proceso penal abreviado
TIPO_PROCESO_JUDICIAL_4	Proceso administrativo
TIPO_PROCESO_JUDICIAL_5	Proceso civil
TIPO_PROCESO_JUDICIAL_6	Proceso laboral
TIPO_PROCESO_JUDICIAL_7	Proceso constitucional
TIPO_PROCESO_JUDICIAL_8	Proceso disciplinario
TIPO_PROCESO_JUDICIAL_9	Proceso en jurisdicción especializada(ej. niñez, género)
TIPO_DENUNCIANTE_0	Desconocido
TIPO_DENUNCIANTE_1	Denuncia de la víctima
TIPO_DENUNCIANTE_2	Parte policial
TIPO_DENUNCIANTE_3	Autoridad municipal
TIPO_DENUNCIANTE_4	Familiar de la víctima
TIPO_DENUNCIANTE_5	Organización de derechos humanos
TIPO_DENUNCIANTE_6	Ministerio Público
TIPO_DENUNCIANTE_7	Docente o autoridad educativa
TIPO_DENUNCIANTE_8	Personal médico o de salud
TIPO_DENUNCIANTE_9	Testigo presencial
DURACION_PROCESO_0	Desconocido
DURACION_PROCESO_1	Menos de 3 meses
DURACION_PROCESO_2	Entre 3 y 6 meses
DURACION_PROCESO_3	Entre 6 meses y 1 año
DURACION_PROCESO_4	Entre 1 y 2 años
DURACION_PROCESO_5	Más de 2 años
DURACION_PROCESO_6	En curso, sin información clara de tiempo
SUBDER_1_0	Otros
SUBDER_1_1	Detención arbitraria
SUBDER_1_2	Tortura o tratos crueles, inhumanos o degradantes
SUBDER_1_3	Desaparición forzada
SUBDER_1_4	Trato inhumano durante la detención
SUBDER_1_5	Privación de libertad sin orden judicial
SUBDER_2_0	Otros
SUBDER_2_1	Censura directa o indirecta
SUBDER_2_2	Amenazas o represalias por expresarse
SUBDER_2_3	Criminalización de periodistas o activistas
SUBDER_2_4	Cierre de medios de comunicación
SUBDER_2_5	Vigilancia o seguimiento por ejercer la libertad de expresión
SUBDER_3_0	Otros
SUBDER_3_1	Imposibilidad de presentar denuncia
SUBDER_3_2	Negación de acceso a mecanismos judiciales
SUBDER_3_3	Falta de imparcialidad judicial
SUBDER_3_4	Retrasos injustificados en procesos
SUBDER_3_5	Corrupción o influencia indebida
SUBDER_3_6	Falta de asistencia legal adecuada
SUBDER_3_7	Negación del recurso de hábeas corpus
SUBDER_4_0	Otros
SUBDER_4_1	Ejecución extrajudicial
SUBDER_4_2	Uso excesivo de la fuerza letal
SUBDER_4_3	Negligencia médica en custodia estatal
SUBDER_4_4	Feminicidio o violencia letal de género
SUBDER_4_5	Falta de investigación en muertes violentas
CONTEXTO_0	Desconocido
CONTEXTO_1	Protesta social
CONTEXTO_2	Desalojo forzado
CONTEXTO_3	Conflicto con fuerzas de seguridad
CONTEXTO_4	Privación de libertad
CONTEXTO_5	Violencia intrafamiliar
CONTEXTO_6	Represalia por denuncia
CONTEXTO_7	Conflicto territorial o agrario
CONTEXTO_8	Discriminación estructural
CONTEXTO_9	Detención arbitraria
CONTEXTO_10	Crimen organizado
CONTEXTO_11	Violencia política
CONTEXTO_12	Violencia sexual
\.


--
-- Data for Name: derecho_vulnerado; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.derecho_vulnerado (id, persona_afectada_id, derecho_vulnerado_codigo) FROM stdin;
\.


--
-- Data for Name: detencion_integridad; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.detencion_integridad (detencion_id, persona_id, tipo_detencion_codigo, orden_judicial, autoridad_involucrada_codigo, hubo_tortura, duracion_dias, acceso_abogado, resultado, motivo_detencion_codigo) FROM stdin;
\.


--
-- Data for Name: expresion_censura; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.expresion_censura (expresion_id, persona_id, medio_expresion_codigo, tipo_represion_codigo, represalias_legales, represalias_fisicas, actor_censor_codigo, consecuencia) FROM stdin;
\.


--
-- Data for Name: nota_derecho; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.nota_derecho (id, derecho_codigo, fecha, titulo, descripcion, creado_por, creado_en, modificado_por, modificado_en) FROM stdin;
\.


--
-- Data for Name: nota_derecho_archivo; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.nota_derecho_archivo (id, nota_id, nombre_original, archivo_url, tipo) FROM stdin;
\.


--
-- Data for Name: parametro_sistema; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.parametro_sistema (id, clave, valor, descripcion, actualizado_en) FROM stdin;
1	max_intentos_pregunta_seguridad	3	Número máximo de intentos fallidos permitidos al responder la pregunta de seguridad para recuperar la contraseña.	2025-07-22 01:30:32.339668
2	tiempo_de_vida_de_sesion	3	Tiempo en horas durante el cual la sesión del usuario permanecerá activa.	2025-07-22 01:30:32.339668
3	descripcion_DER_1	El Derecho a la Libertad Personal e Integridad Personal protege a toda persona contra detenciones arbitrarias, tortura, desapariciones forzadas y cualquier forma de trato cruel, inhumano o degradante.	Descripción del Derecho a la Libertad Personal e Integridad Personal	2025-07-22 01:30:32.339668
4	descripcion_DER_2	El Derecho a la Libertad de Expresión garantiza a toda persona la facultad de buscar, recibir y difundir informaciones e ideas de toda índole, sin censura previa y por cualquier medio de comunicación.	Descripción del Derecho a la Libertad de Expresión	2025-07-22 01:30:32.339668
5	descripcion_DER_3	El Derecho de Acceso a la Justicia asegura a las personas la posibilidad de acudir a tribunales imparciales, competentes e independientes para hacer valer sus derechos, obtener reparación y garantizar el debido proceso.	Descripción del Derecho de Acceso a la Justicia	2025-07-22 01:30:32.339668
6	descripcion_DER_4	El Derecho a la Vida es el derecho fundamental de toda persona a no ser privada arbitrariamente de la vida, y obliga al Estado a proteger y garantizar este derecho en todo momento.	Descripción del Derecho a la Vida	2025-07-22 01:30:32.339668
7	investigador_DER_1	Fátima Enriqueta Rosales Zelaya	Investigador responsable del Derecho a la Libertad Personal e Integridad personal	2025-07-22 01:30:32.339668
8	investigador_DER_2	Carlos Daniel Fernández Castro	Investigador responsable del Derecho a la Libertad de Expresión	2025-07-22 01:30:32.339668
9	investigador_DER_3	Álvaro Javier Delgado Portillo	Investigador responsable del Derecho de Acceso a la Justicia	2025-07-22 01:30:32.339668
10	investigador_DER_4	Mauricio Enrique Erazo Alfaro	Investigador responsable del Derecho a la Vida	2025-07-22 01:30:32.339668
11	max_tiempo_inactividad	15	Tiempo máximo permitido, en minutos, que un usuario puede permanecer inactivo en la aplicación antes de cerrar la sesión automáticamente.	2025-07-22 01:30:32.339668
12	acerca_del_proyecto	El Sistema de registros del vulneraciones de derechos del IDHUCA es una plataforma diseñada para centralizar y analizar información clave sobre derechos humanos y justicia social. Su propósito es facilitar la recopilación y visualización de datos provenientes de diversas fuentes, permitiendo una interpretación clara y fundamentada. Mediante la centralización de datos y la generación de gráficos dinámicos, el sistema transforma la información en un recurso accesible que apoya la toma de decisiones y la incidencia en políticas públicas. Al unificar los datos en un solo espacio digital, mejora el seguimiento de tendencias y optimiza los procesos de análisis, facilitando el trabajo del IDHUCA en la defensa de los derechos humanos.	Descripción del proyecto visible al iniciar sesión	2025-07-22 01:30:32.339668
\.


--
-- Data for Name: persona_afectada; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.persona_afectada (id, evento_id, nombre, genero_codigo, edad, nacionalidad_codigo, dep_artamento_residencia_codigo, municipio_residencia_codigo, tipo_persona_codigo, estado_salud_codigo) FROM stdin;
\.


--
-- Data for Name: recovery_password; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.recovery_password (id, usuario_id, pregunta_codigo, respuesta_hash, intentos_fallidos, actualizado_en) FROM stdin;
1	1	SQ_1	$2a$10$K7HufdmPI16g56n8O2hQtuk4mQ1RoXmx3uWB7AQO0FsIrjloRtTWG	0	2025-07-22 01:30:32.347273
\.


--
-- Data for Name: registro_evento; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.registro_evento (id, fecha_registro, fecha_hecho, fuente_codigo, estado_actual_codigo, derecho_asociado_codigo, flag_violencia, flag_detencion, flag_expresion, flag_justicia, flag_censura, flag_regimen_excepcion, observaciones, creado_por) FROM stdin;
\.


--
-- Data for Name: ubicacion; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.ubicacion (id, evento_id, dep_artamento_codigo, municipio_codigo, lugar_exacto_codigo) FROM stdin;
\.


--
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.usuario (id, nombre, email, contrasena_hash, rol_codigo, activo, es_provisional, creado_en) FROM stdin;
1	Admin	administrador@uca.edu.sv	$2a$10$K7HufdmPI16g56n8O2hQtuk4mQ1RoXmx3uWB7AQO0FsIrjloRtTWG	ROL_1	t	f	2025-07-22 01:30:32.343564
\.


--
-- Data for Name: violencia; Type: TABLE DATA; Schema: public; Owner: miusuario
--

COPY public.violencia (violencia_id, persona_id, es_asesinato, tipo_violencia_codigo, artefacto_utilizado_codigo, contexto_codigo, actor_responsable_codigo, estado_salud_actor_responsable_codigo, hubo_proteccion, investigacion_abierta, respuesta_estado) FROM stdin;
\.


--
-- Name: acceso_justicia_justicia_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.acceso_justicia_justicia_id_seq', 1, false);


--
-- Name: auditoria_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.auditoria_id_seq', 1, false);


--
-- Name: derecho_vulnerado_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.derecho_vulnerado_id_seq', 1, false);


--
-- Name: detencion_integridad_detencion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.detencion_integridad_detencion_id_seq', 1, false);


--
-- Name: expresion_censura_expresion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.expresion_censura_expresion_id_seq', 1, false);


--
-- Name: nota_derecho_archivo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.nota_derecho_archivo_id_seq', 1, false);


--
-- Name: nota_derecho_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.nota_derecho_id_seq', 1, false);


--
-- Name: parametro_sistema_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.parametro_sistema_id_seq', 12, true);


--
-- Name: persona_afectada_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.persona_afectada_id_seq', 1, false);


--
-- Name: recovery_password_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.recovery_password_id_seq', 1, true);


--
-- Name: registro_evento_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.registro_evento_id_seq', 1, false);


--
-- Name: ubicacion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.ubicacion_id_seq', 1, false);


--
-- Name: usuario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.usuario_id_seq', 1, true);


--
-- Name: violencia_violencia_id_seq; Type: SEQUENCE SET; Schema: public; Owner: miusuario
--

SELECT pg_catalog.setval('public.violencia_violencia_id_seq', 1, false);


--
-- Name: acceso_justicia acceso_justicia_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.acceso_justicia
    ADD CONSTRAINT acceso_justicia_pkey PRIMARY KEY (justicia_id);


--
-- Name: auditoria auditoria_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT auditoria_pkey PRIMARY KEY (id);


--
-- Name: catalogo catalogo_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.catalogo
    ADD CONSTRAINT catalogo_pkey PRIMARY KEY (codigo);


--
-- Name: derecho_vulnerado derecho_vulnerado_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.derecho_vulnerado
    ADD CONSTRAINT derecho_vulnerado_pkey PRIMARY KEY (id);


--
-- Name: detencion_integridad detencion_integridad_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.detencion_integridad
    ADD CONSTRAINT detencion_integridad_pkey PRIMARY KEY (detencion_id);


--
-- Name: expresion_censura expresion_censura_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.expresion_censura
    ADD CONSTRAINT expresion_censura_pkey PRIMARY KEY (expresion_id);


--
-- Name: nota_derecho_archivo nota_derecho_archivo_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.nota_derecho_archivo
    ADD CONSTRAINT nota_derecho_archivo_pkey PRIMARY KEY (id);


--
-- Name: nota_derecho nota_derecho_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.nota_derecho
    ADD CONSTRAINT nota_derecho_pkey PRIMARY KEY (id);


--
-- Name: parametro_sistema parametro_sistema_clave_key; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.parametro_sistema
    ADD CONSTRAINT parametro_sistema_clave_key UNIQUE (clave);


--
-- Name: parametro_sistema parametro_sistema_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.parametro_sistema
    ADD CONSTRAINT parametro_sistema_pkey PRIMARY KEY (id);


--
-- Name: persona_afectada persona_afectada_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada
    ADD CONSTRAINT persona_afectada_pkey PRIMARY KEY (id);


--
-- Name: recovery_password recovery_password_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.recovery_password
    ADD CONSTRAINT recovery_password_pkey PRIMARY KEY (id);


--
-- Name: registro_evento registro_evento_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.registro_evento
    ADD CONSTRAINT registro_evento_pkey PRIMARY KEY (id);


--
-- Name: ubicacion ubicacion_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.ubicacion
    ADD CONSTRAINT ubicacion_pkey PRIMARY KEY (id);


--
-- Name: usuario usuario_email_key; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_email_key UNIQUE (email);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);


--
-- Name: violencia violencia_pkey; Type: CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.violencia
    ADD CONSTRAINT violencia_pkey PRIMARY KEY (violencia_id);


--
-- Name: acceso_justicia acceso_justicia_duracion_proceso_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.acceso_justicia
    ADD CONSTRAINT acceso_justicia_duracion_proceso_codigo_fkey FOREIGN KEY (duracion_proceso_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: acceso_justicia acceso_justicia_persona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.acceso_justicia
    ADD CONSTRAINT acceso_justicia_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.persona_afectada(id) ON DELETE CASCADE;


--
-- Name: acceso_justicia acceso_justicia_tipo_denunciante_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.acceso_justicia
    ADD CONSTRAINT acceso_justicia_tipo_denunciante_codigo_fkey FOREIGN KEY (tipo_denunciante_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: acceso_justicia acceso_justicia_tipo_proceso_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.acceso_justicia
    ADD CONSTRAINT acceso_justicia_tipo_proceso_codigo_fkey FOREIGN KEY (tipo_proceso_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: auditoria auditoria_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT auditoria_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- Name: derecho_vulnerado derecho_vulnerado_derecho_vulnerado_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.derecho_vulnerado
    ADD CONSTRAINT derecho_vulnerado_derecho_vulnerado_codigo_fkey FOREIGN KEY (derecho_vulnerado_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: derecho_vulnerado derecho_vulnerado_persona_afectada_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.derecho_vulnerado
    ADD CONSTRAINT derecho_vulnerado_persona_afectada_id_fkey FOREIGN KEY (persona_afectada_id) REFERENCES public.persona_afectada(id) ON DELETE CASCADE;


--
-- Name: detencion_integridad detencion_integridad_autoridad_involucrada_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.detencion_integridad
    ADD CONSTRAINT detencion_integridad_autoridad_involucrada_codigo_fkey FOREIGN KEY (autoridad_involucrada_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: detencion_integridad detencion_integridad_motivo_detencion_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.detencion_integridad
    ADD CONSTRAINT detencion_integridad_motivo_detencion_codigo_fkey FOREIGN KEY (motivo_detencion_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: detencion_integridad detencion_integridad_persona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.detencion_integridad
    ADD CONSTRAINT detencion_integridad_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.persona_afectada(id) ON DELETE CASCADE;


--
-- Name: detencion_integridad detencion_integridad_tipo_detencion_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.detencion_integridad
    ADD CONSTRAINT detencion_integridad_tipo_detencion_codigo_fkey FOREIGN KEY (tipo_detencion_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: expresion_censura expresion_censura_actor_censor_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.expresion_censura
    ADD CONSTRAINT expresion_censura_actor_censor_codigo_fkey FOREIGN KEY (actor_censor_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: expresion_censura expresion_censura_medio_expresion_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.expresion_censura
    ADD CONSTRAINT expresion_censura_medio_expresion_codigo_fkey FOREIGN KEY (medio_expresion_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: expresion_censura expresion_censura_persona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.expresion_censura
    ADD CONSTRAINT expresion_censura_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.persona_afectada(id) ON DELETE CASCADE;


--
-- Name: expresion_censura expresion_censura_tipo_represion_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.expresion_censura
    ADD CONSTRAINT expresion_censura_tipo_represion_codigo_fkey FOREIGN KEY (tipo_represion_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: nota_derecho_archivo nota_derecho_archivo_nota_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.nota_derecho_archivo
    ADD CONSTRAINT nota_derecho_archivo_nota_id_fkey FOREIGN KEY (nota_id) REFERENCES public.nota_derecho(id) ON DELETE CASCADE;


--
-- Name: nota_derecho nota_derecho_creado_por_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.nota_derecho
    ADD CONSTRAINT nota_derecho_creado_por_fkey FOREIGN KEY (creado_por) REFERENCES public.usuario(id);


--
-- Name: nota_derecho nota_derecho_derecho_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.nota_derecho
    ADD CONSTRAINT nota_derecho_derecho_codigo_fkey FOREIGN KEY (derecho_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: nota_derecho nota_derecho_modificado_por_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.nota_derecho
    ADD CONSTRAINT nota_derecho_modificado_por_fkey FOREIGN KEY (modificado_por) REFERENCES public.usuario(id);


--
-- Name: persona_afectada persona_afectada_dep_artamento_residencia_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada
    ADD CONSTRAINT persona_afectada_dep_artamento_residencia_codigo_fkey FOREIGN KEY (dep_artamento_residencia_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: persona_afectada persona_afectada_estado_salud_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada
    ADD CONSTRAINT persona_afectada_estado_salud_codigo_fkey FOREIGN KEY (estado_salud_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: persona_afectada persona_afectada_evento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada
    ADD CONSTRAINT persona_afectada_evento_id_fkey FOREIGN KEY (evento_id) REFERENCES public.registro_evento(id) ON DELETE CASCADE;


--
-- Name: persona_afectada persona_afectada_genero_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada
    ADD CONSTRAINT persona_afectada_genero_codigo_fkey FOREIGN KEY (genero_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: persona_afectada persona_afectada_municipio_residencia_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada
    ADD CONSTRAINT persona_afectada_municipio_residencia_codigo_fkey FOREIGN KEY (municipio_residencia_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: persona_afectada persona_afectada_nacionalidad_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada
    ADD CONSTRAINT persona_afectada_nacionalidad_codigo_fkey FOREIGN KEY (nacionalidad_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: persona_afectada persona_afectada_tipo_persona_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.persona_afectada
    ADD CONSTRAINT persona_afectada_tipo_persona_codigo_fkey FOREIGN KEY (tipo_persona_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: recovery_password recovery_password_pregunta_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.recovery_password
    ADD CONSTRAINT recovery_password_pregunta_codigo_fkey FOREIGN KEY (pregunta_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: recovery_password recovery_password_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.recovery_password
    ADD CONSTRAINT recovery_password_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuario(id) ON DELETE CASCADE;


--
-- Name: registro_evento registro_evento_creado_por_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.registro_evento
    ADD CONSTRAINT registro_evento_creado_por_fkey FOREIGN KEY (creado_por) REFERENCES public.usuario(id);


--
-- Name: registro_evento registro_evento_derecho_asociado_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.registro_evento
    ADD CONSTRAINT registro_evento_derecho_asociado_codigo_fkey FOREIGN KEY (derecho_asociado_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: registro_evento registro_evento_estado_actual_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.registro_evento
    ADD CONSTRAINT registro_evento_estado_actual_codigo_fkey FOREIGN KEY (estado_actual_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: registro_evento registro_evento_fuente_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.registro_evento
    ADD CONSTRAINT registro_evento_fuente_codigo_fkey FOREIGN KEY (fuente_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: ubicacion ubicacion_dep_artamento_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.ubicacion
    ADD CONSTRAINT ubicacion_dep_artamento_codigo_fkey FOREIGN KEY (dep_artamento_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: ubicacion ubicacion_evento_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.ubicacion
    ADD CONSTRAINT ubicacion_evento_id_fkey FOREIGN KEY (evento_id) REFERENCES public.registro_evento(id) ON DELETE CASCADE;


--
-- Name: ubicacion ubicacion_lugar_exacto_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.ubicacion
    ADD CONSTRAINT ubicacion_lugar_exacto_codigo_fkey FOREIGN KEY (lugar_exacto_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: ubicacion ubicacion_municipio_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.ubicacion
    ADD CONSTRAINT ubicacion_municipio_codigo_fkey FOREIGN KEY (municipio_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: usuario usuario_rol_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_rol_codigo_fkey FOREIGN KEY (rol_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: violencia violencia_actor_responsable_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.violencia
    ADD CONSTRAINT violencia_actor_responsable_codigo_fkey FOREIGN KEY (actor_responsable_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: violencia violencia_artefacto_utilizado_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.violencia
    ADD CONSTRAINT violencia_artefacto_utilizado_codigo_fkey FOREIGN KEY (artefacto_utilizado_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: violencia violencia_contexto_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.violencia
    ADD CONSTRAINT violencia_contexto_codigo_fkey FOREIGN KEY (contexto_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: violencia violencia_estado_salud_actor_responsable_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.violencia
    ADD CONSTRAINT violencia_estado_salud_actor_responsable_codigo_fkey FOREIGN KEY (estado_salud_actor_responsable_codigo) REFERENCES public.catalogo(codigo);


--
-- Name: violencia violencia_persona_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.violencia
    ADD CONSTRAINT violencia_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.persona_afectada(id) ON DELETE CASCADE;


--
-- Name: violencia violencia_tipo_violencia_codigo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: miusuario
--

ALTER TABLE ONLY public.violencia
    ADD CONSTRAINT violencia_tipo_violencia_codigo_fkey FOREIGN KEY (tipo_violencia_codigo) REFERENCES public.catalogo(codigo);


--
-- PostgreSQL database dump complete
--

