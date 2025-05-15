-- Tabla catálogo
CREATE TABLE catalogo (
    codigo VARCHAR PRIMARY KEY,
    descripcion VARCHAR
);

-- Tabla usuario
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR NOT NULL,
    email VARCHAR UNIQUE NOT NULL,
    contrasena_hash TEXT NOT NULL,
    ROL_codigo VARCHAR REFERENCES catalogo(codigo),
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla auditoria
CREATE TABLE auditoria (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuario(id),
    tabla_afectada VARCHAR NOT NULL,
    operacion VARCHAR NOT NULL,
    registro_id VARCHAR,
    descripcion TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recovery_password (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuario(id) ON DELETE CASCADE,
    pregunta_codigo VARCHAR NOT NULL REFERENCES catalogo(codigo),
    respuesta_hash TEXT NOT NULL,
    intentos_fallidos INT DEFAULT 0,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE parametro_sistema (
    id SERIAL PRIMARY KEY,
    clave VARCHAR(100) UNIQUE NOT NULL,
    valor TEXT NOT NULL,
    descripcion TEXT,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla principal: publicación o ficha del derecho
CREATE TABLE nota_derecho (
    id SERIAL PRIMARY KEY,
    derecho_codigo VARCHAR REFERENCES catalogo(codigo),
    fecha DATE NOT NULL,
    titulo VARCHAR,
    descripcion TEXT,
    creado_por INT REFERENCES usuario(id),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_por INT REFERENCES usuario(id),
    modificado_en TIMESTAMP
);

-- Tabla de archivos adjuntos relacionados
CREATE TABLE nota_derecho_archivo (
    id SERIAL PRIMARY KEY,
    nota_id INT REFERENCES nota_derecho(id) ON DELETE CASCADE,
    nombre_original VARCHAR,
    archivo_url TEXT,
    tipo VARCHAR
);

-- Tabla registro de eventos
CREATE TABLE registro_evento (
    id SERIAL PRIMARY KEY,
    fecha_registro TIMESTAMP,
    fecha_hecho DATE,
    fuente_codigo VARCHAR REFERENCES catalogo(codigo),
    estado_actual_codigo VARCHAR REFERENCES catalogo(codigo),
    derecho_asociado_codigo VARCHAR REFERENCES catalogo(codigo),
    flag_violencia BOOLEAN,
    flag_detencion BOOLEAN,
    flag_expresion BOOLEAN,
    flag_justicia BOOLEAN,
    flag_censura BOOLEAN,
    flag_regimen_excepcion BOOLEAN,
    observaciones TEXT,
    creado_por INT REFERENCES usuario(id)
);

-- Tabla ubicación
CREATE TABLE ubicacion (
    id SERIAL PRIMARY KEY,
    evento_id INT REFERENCES registro_evento(id),
    DEP_artamento_codigo VARCHAR REFERENCES catalogo(codigo),
    municipio_codigo VARCHAR REFERENCES catalogo(codigo),
    lugar_exacto_codigo VARCHAR REFERENCES catalogo(codigo)
);

-- Tabla persona afectada
CREATE TABLE persona_afectada (
    id SERIAL PRIMARY KEY,
    evento_id INT REFERENCES registro_evento(id),
    nombre VARCHAR,
    genero_codigo VARCHAR REFERENCES catalogo(codigo),
    edad INT,
    nacionalidad_codigo VARCHAR REFERENCES catalogo(codigo),
    DEP_artamento_residencia_codigo VARCHAR REFERENCES catalogo(codigo),
    municipio_residencia_codigo VARCHAR REFERENCES catalogo(codigo),
    tipo_persona_codigo VARCHAR REFERENCES catalogo(codigo),
    estado_salud_codigo VARCHAR REFERENCES catalogo(codigo)
);

-- Tabla derechos vulnerados (específicos)
CREATE TABLE derecho_vulnerado (
    id SERIAL PRIMARY KEY,
    persona_afectada_id INT REFERENCES persona_afectada(id),
    derecho_vulnerado_codigo VARCHAR REFERENCES catalogo(codigo)
);

-- Tabla violencia
CREATE TABLE violencia (
    violencia_id SERIAL PRIMARY KEY,
    persona_id INT REFERENCES persona_afectada(id),
    es_asesinato BOOLEAN,
    tipo_violencia_codigo VARCHAR REFERENCES catalogo(codigo),
    artefacto_utilizado_codigo VARCHAR REFERENCES catalogo(codigo),
    contexto_codigo VARCHAR REFERENCES catalogo(codigo),
    actor_responsable_codigo VARCHAR REFERENCES catalogo(codigo),
    estado_salud_actor_responsable_codigo VARCHAR REFERENCES catalogo(codigo),
    hubo_proteccion BOOLEAN,
    investigacion_abierta BOOLEAN,
    respuesta_estado TEXT
);

-- Tabla detención e integridad
CREATE TABLE detencion_integridad (
    detencion_id SERIAL PRIMARY KEY,
    persona_id INT REFERENCES persona_afectada(id),
    tipo_detencion_codigo VARCHAR REFERENCES catalogo(codigo),
    orden_judicial BOOLEAN,
    autoridad_involucrada_codigo VARCHAR REFERENCES catalogo(codigo),
    hubo_tortura BOOLEAN,
    duracion_dias INT,
    acceso_abogado BOOLEAN,
    resultado TEXT,
    motivo_detencion_codigo VARCHAR REFERENCES catalogo(codigo)
);

-- Tabla expresión y censura
CREATE TABLE expresion_censura (
    expresion_id SERIAL PRIMARY KEY,
    persona_id INT REFERENCES persona_afectada(id),
    medio_expresion_codigo VARCHAR REFERENCES catalogo(codigo),
    tipo_represion_codigo VARCHAR REFERENCES catalogo(codigo),
    represalias_legales BOOLEAN,
    represalias_fisicas BOOLEAN,
    actor_censor_codigo VARCHAR REFERENCES catalogo(codigo),
    consecuencia TEXT
);

-- Tabla acceso a la justicia
CREATE TABLE acceso_justicia (
    justicia_id SERIAL PRIMARY KEY,
    persona_id INT REFERENCES persona_afectada(id),
    tipo_proceso_codigo VARCHAR REFERENCES catalogo(codigo),
    fecha_denuncia DATE,
    tipo_denunciante_codigo VARCHAR REFERENCES catalogo(codigo),
    duracion_proceso_codigo VARCHAR REFERENCES catalogo(codigo),
    acceso_abogado BOOLEAN,
    hubo_parcialidad BOOLEAN,
    resultado_proceso TEXT,
    instancia VARCHAR
);

INSERT INTO parametro_sistema (clave, valor, descripcion)
VALUES ('max_intentos_pregunta_seguridad', '3', 'Número máximo de intentos fallidos permitidos al responder la pregunta de seguridad para recuperar contraseña');

-- Catalogo de ROL_es del sistema
INSERT INTO catalogo (codigo, descripcion) VALUES
('ROL_1', 'admin'),
('ROL_2', 'user'),
('ROL_3', 'ayudante');

-- Catalogo de preguntas de seguridad
INSERT INTO catalogo (codigo, descripcion) VALUES 
('SQ_1', '¿Cuál es el nombre de tu primera mascota?'),
('SQ_2', '¿Cuál es el nombre de tu escuela primaria?'),
('SQ_3', '¿En qué ciudad naciste?'),
('SQ_4', '¿Cuál es el segundo nombre de tu madre?'),
('SQ_5', '¿Cuál fue tu primer empleo?'),
('SQ_6', '¿Cuál es tu película favorita?'),
('SQ_7', '¿Cuál es tu comida favorita de la infancia?'),
('SQ_8', '¿Cuál es el nombre de tu mejor amigo de la infancia?'),
('SQ_9', '¿Cuál es el nombre de tu primer profesor o profesora?'),
('SQ_10', '¿En qué calle viviste de niño?');

--Catalogo de DEP_artamentos
INSERT INTO catalogo (codigo, descripcion) VALUES 
('DEP_0', 'OTROS PAISES'),
('DEP_1', 'AHUACHAPAN'),
('DEP_2', 'SANTA ANA'),
('DEP_3', 'SONSONATE'),
('DEP_4', 'CHALATENANGO'),
('DEP_5', 'LA LIBERTAD'),
('DEP_6', 'SAN SALVADOR'),
('DEP_7', 'CUSCATLAN'),
('DEP_8', 'LA PAZ'),
('DEP_9', 'CABANAS'),
('DEP_10', 'SAN VICENTE'),
('DEP_11', 'USULUTAN'),
('DEP_12', 'SAN MIGUEL'),
('DEP_13', 'MORAZAN'),
('DEP_14', 'LA UNION'),
('MUN_1_1', 'AHUACHAPAN'),
('MUN_1_2', 'APANECA'),
('MUN_1_3', 'ATIQUIZAYA'),
('MUN_1_4', 'CONCEPCION DE ATACO'),
('MUN_1_5', 'EL REFUGIO'),
('MUN_1_6', 'GUAYMANGO'),
('MUN_1_7', 'JUJUTLA'),
('MUN_1_8', 'SAN FRANCISCO MENENDEZ'),
('MUN_1_9', 'SAN LORENZO'),
('MUN_1_10', 'SAN PEDRO PUXTLA'),
('MUN_1_11', 'TACUBA'),
('MUN_1_12', 'TURIN'),
('MUN_2_1', 'CANDELARIA DE LA FRONTERA'),
('MUN_2_2', 'COATEPEQUE'),
('MUN_2_3', 'CHALCHUAPA'),
('MUN_2_4', 'EL CONGO'),
('MUN_2_5', 'EL PORVENIR'),
('MUN_2_6', 'MASAHUAT'),
('MUN_2_7', 'METAPAN'),
('MUN_2_8', 'SAN ANTONIO PAJONAL'),
('MUN_2_9', 'SAN SEBASTIAN SALITRILLO'),
('MUN_2_10', 'SANTA ANA'),
('MUN_2_11', 'SANTA ROSA GUACHIPILIN'),
('MUN_2_12', 'SANTIAGO DE LA FRONTERA'),
('MUN_2_13', 'TEXISTEPEQUE'),
('MUN_3_1', 'ACAJUTLA'),
('MUN_3_2', 'ARMENIA'),
('MUN_3_3', 'CALUCO'),
('MUN_3_4', 'CUISNAHUAT'),
('MUN_3_5', 'SANTA ISABEL ISHUATAN'),
('MUN_3_6', 'IZALCO'),
('MUN_3_7', 'JUAYUA'),
('MUN_3_8', 'NAHUIZALCO'),
('MUN_3_9', 'NAHUILINGO'),
('MUN_3_10', 'SALCOATITAN'),
('MUN_3_11', 'SAN ANTONIO DEL MONTE'),
('MUN_3_12', 'SAN JULIAN'),
('MUN_3_13', 'SANTA CATARINA MASAHUAT'),
('MUN_3_14', 'SANTO DOMINGO GUZMAN'),
('MUN_3_15', 'SONSONATE'),
('MUN_3_16', 'SONZACATE'),
('MUN_4_1', 'AGUA CALIENTE'),
('MUN_4_2', 'ARCATAO'),
('MUN_4_3', 'AZACUALPA'),
('MUN_4_4', 'CITALA'),
('MUN_4_5', 'COMALAPA'),
('MUN_4_6', 'CONCEPCION QUEZALTEPEQUE'),
('MUN_4_7', 'CHALATENANGO'),
('MUN_4_8', 'DULCE NOMBRE DE MARIA'),
('MUN_4_9', 'EL CARRIZAL'),
('MUN_4_10', 'EL PARAISO'),
('MUN_4_11', 'LA LAGUNA'),
('MUN_4_12', 'LA PALMA'),
('MUN_4_13', 'LA REINA'),
('MUN_4_14', 'LAS VUELTAS'),
('MUN_4_15', 'NOMBRE DE JESUS'),
('MUN_4_16', 'NUEVA CONCEPCION'),
('MUN_4_17', 'NUEVA TRINIDAD'),
('MUN_4_18', 'OJOS DE AGUA'),
('MUN_4_19', 'POTONICO'),
('MUN_4_20', 'SAN ANTONIO DE LA CRUZ'),
('MUN_4_21', 'SAN ANTONIO LOS RANCHOS'),
('MUN_4_22', 'SAN FERNANDO'),
('MUN_4_23', 'SAN FRANCISCO LEMPA'),
('MUN_4_24', 'SAN FRANCISCO MORAZAN'),
('MUN_4_25', 'SAN IGNACIO'),
('MUN_4_26', 'SAN ISIDRO LABRADOR'),
('MUN_4_27', 'SAN J. CANCASQ_UE'),
('MUN_4_28', 'SAN JOSE LAS FLORES'),
('MUN_4_29', 'SAN LUIS DEL CARMEN'),
('MUN_4_30', 'SAN  MIGUEL DE MERCEDES'),
('MUN_4_31', 'SAN RAFAEL'),
('MUN_4_32', 'SANTA RITA'),
('MUN_4_33', 'TEJUTLA'),
('MUN_5_1', 'ANTIGUO CUSCATLAN'),
('MUN_5_2', 'CIUDAD ARCE'),
('MUN_5_3', 'COLON'),
('MUN_5_4', 'COMASAGUA'),
('MUN_5_5', 'CHILTIUPAN'),
('MUN_5_6', 'HUIZUCAR'),
('MUN_5_7', 'JAYAQUE'),
('MUN_5_8', 'JICALAPA'),
('MUN_5_9', 'LA LIBERTAD'),
('MUN_5_10', 'NUEVO CUSCATLAN'),
('MUN_5_11', 'SANTA TECLA'),
('MUN_5_12', 'QUEZALTEPEQUE'),
('MUN_5_13', 'SACACOYO'),
('MUN_5_14', 'SAN JOSE VILLANUEVA'),
('MUN_5_15', 'SAN JUAN OPICO'),
('MUN_5_16', 'SAN MATIAS'),
('MUN_5_17', 'SAN PABLO TACACHICO'),
('MUN_5_18', 'TAMANIQUE'),
('MUN_5_19', 'TALNIQUE'),
('MUN_5_20', 'TEOTEPEQUE'),
('MUN_5_21', 'TEPECOYO'),
('MUN_5_22', 'ZARAGOZA'),
('MUN_6_1', 'AGUILARES'),
('MUN_6_2', 'APOPA'),
('MUN_6_3', 'AYUTUXTEPEQUE'),
('MUN_6_4', 'CUSCATANCINGO'),
('MUN_6_5', 'EL PAISNAL'),
('MUN_6_6', 'GUAZAPA'),
('MUN_6_7', 'ILOPANGO'),
('MUN_6_8', 'MEJICANOS'),
('MUN_6_9', 'NEJAPA'),
('MUN_6_10', 'PANCHIMALCO'),
('MUN_6_11', 'ROSARIO DE MORA'),
('MUN_6_12', 'SAN MARCOS'),
('MUN_6_13', 'SAN MARTIN'),
('MUN_6_14', 'SAN SALVADOR'),
('MUN_6_15', 'SANTIAGO TEXACUANGOS'),
('MUN_6_16', 'SANTO TOMAS'),
('MUN_6_17', 'SOYAPANGO'),
('MUN_6_18', 'TONACATEPEQUE'),
('MUN_6_19', 'CIUDAD DELGADO'),
('MUN_7_1', 'CANDELARIA'),
('MUN_7_2', 'COJUTEPEQUE'),
('MUN_7_3', 'EL CARMEN'),
('MUN_7_4', 'EL ROSARIO'),
('MUN_7_5', 'MONTE SAN JUAN'),
('MUN_7_6', 'ORATORIO DE CONCEPCION'),
('MUN_7_7', 'SAN BARTOLOME PERULAPIA'),
('MUN_7_8', 'SAN CRISTOBAL'),
('MUN_7_9', 'SAN JOSE GUAYABAL'),
('MUN_7_10', 'SAN PEDRO PERULAPAN'),
('MUN_7_11', 'SAN RAFAEL CEDROS'),
('MUN_7_12', 'SAN RAMON'),
('MUN_7_13', 'SANTA CRUZ ANALQUITO'),
('MUN_7_14', 'SANTA CRUZ MICHAPA'),
('MUN_7_15', 'SUCHITOTO'),
('MUN_7_16', 'TENANCINGO'),
('MUN_8_1', 'CUYULTITAN'),
('MUN_8_2', 'ROSARIO LA PAZ'),
('MUN_8_3', 'JERUSALEN'),
('MUN_8_4', 'MERCEDES LA CEIBA'),
('MUN_8_5', 'OLOCUILTA'),
('MUN_8_6', 'PARAISO DE OSORIO'),
('MUN_8_7', 'SAN ANTONIO MASAHUAT'),
('MUN_8_8', 'SAN EMIGDIO'),
('MUN_8_9', 'SAN FRANCISCO CHINAMECA'),
('MUN_8_10', 'SAN JUAN NONUALCO'),
('MUN_8_11', 'SAN JUAN TALPA'),
('MUN_8_12', 'SAN JUAN TEPEZONTES'),
('MUN_8_13', 'SAN LUIS TALPA'),
('MUN_8_14', 'SAN MIGUEL TEPEZONTES'),
('MUN_8_15', 'SAN PEDRO MASAHUAT'),
('MUN_8_16', 'SAN PEDRO NONUALCO'),
('MUN_8_17', 'SAN RAFAEL OBRAJUELO'),
('MUN_8_18', 'SANTA MARIA OSTUMA'),
('MUN_8_19', 'SANTIAGO NONUALCO'),
('MUN_8_20', 'TAPALHUACA'),
('MUN_8_21', 'ZACATECOLUCA'),
('MUN_8_22', 'SAN LUIS LA HERRADURA'),
('MUN_9_1', 'CINQUERA'),
('MUN_9_2', 'GUACOTECTI'),
('MUN_9_3', 'ILOBASCO'),
('MUN_9_4', 'JUTIAPA'),
('MUN_9_5', 'SAN ISIDRO'),
('MUN_9_6', 'SENSUNTEPEQUE'),
('MUN_9_7', 'TEJUTEPEQUE'),
('MUN_9_8', 'VICTORIA'),
('MUN_9_9', 'VILLA DOLORES'),
('MUN_10_1', 'APASTEPEQUE'),
('MUN_10_2', 'GUADALUPE'),
('MUN_10_4', 'SANTA CLARA'),
('MUN_10_5', 'SANTO DOMINGO'),
('MUN_10_3', 'SAN CAYETANO ISTEPEQUE'),
('MUN_10_6', 'SAN ESTEBAN CATARINA'),
('MUN_10_7', 'SAN ILDEFONSO'),
('MUN_10_8', 'SAN LORENZO'),
('MUN_10_9', 'SAN SEBASTIAN'),
('MUN_10_10', 'SAN VICENTE'),
('MUN_10_11', 'TECOLUCA'),
('MUN_10_12', 'TEPETITAN'),
('MUN_10_13', 'VERAPAZ'),
('MUN_11_1', 'ALEGRIA'),
('MUN_11_2', 'BERLIN'),
('MUN_11_3', 'CALIFORNIA'),
('MUN_11_4', 'CONCEPCION BATRES'),
('MUN_11_5', 'EL TRIUNFO'),
('MUN_11_6', 'EREGUAYQUIN'),
('MUN_11_7', 'ESTANZUELAS'),
('MUN_11_8', 'JIQUILISCO'),
('MUN_11_9', 'JUCUAPA'),
('MUN_11_10', 'JUCUARAN'),
('MUN_11_11', 'MERCEDES UMAÑA'),
('MUN_11_12', 'NUEVA GRANADA'),
('MUN_11_13', 'OZATLAN'),
('MUN_11_14', 'PUERTO EL TRIUNFO'),
('MUN_11_15', 'SAN AGUSTIN'),
('MUN_11_16', 'SAN BUENAVENTURA'),
('MUN_11_17', 'SAN DIONISIO'),
('MUN_11_19', 'SAN FRANCISCO JAVIER'),
('MUN_11_18', 'SANTA ELENA'),
('MUN_11_20', 'SANTA MARIA'),
('MUN_11_21', 'SANTIAGO DE MARIA'),
('MUN_11_22', 'TECAPAN'),
('MUN_11_23', 'USULUTAN'),
('MUN_12_1', 'CAROL_INA'),
('MUN_12_2', 'CIUDAD BARRIOS'),
('MUN_12_3', 'COMACARAN'),
('MUN_12_4', 'CHAPELTIQUE'),
('MUN_12_5', 'CHINAMECA'),
('MUN_12_6', 'CHIRILAGUA'),
('MUN_12_7', 'EL TRANSITO'),
('MUN_12_8', 'LOLOTIQUE'),
('MUN_12_9', 'MONCAGUA'),
('MUN_12_10', 'NUEVA GUADALUPE'),
('MUN_12_11', 'NUEVO EDEN DE SAN JUAN'),
('MUN_12_12', 'QUELEPA'),
('MUN_12_13', 'SAN ANTONIO DEL MOSCO'),
('MUN_12_14', 'SAN GERARDO'),
('MUN_12_15', 'SAN JORGE'),
('MUN_12_16', 'SAN LUIS REINA'),
('MUN_12_17', 'SAN MIGUEL'),
('MUN_12_18', 'SAN RAFAEL ORIENTE'),
('MUN_12_19', 'SESORI'),
('MUN_12_20', 'ULUAZAPA'),
('MUN_13_1', 'ARAMBALA'),
('MUN_13_2', 'CACAOPERA'),
('MUN_13_3', 'CORINTO'),
('MUN_13_4', 'CHILANGA'),
('MUN_13_5', 'DELICIAS DE CONCEPCION'),
('MUN_13_6', 'EL DIVISADERO'),
('MUN_13_7', 'EL ROSARIO'),
('MUN_13_8', 'GUALOCOCTI'),
('MUN_13_9', 'GUATAJIAGUA'),
('MUN_13_10', 'JOATECA'),
('MUN_13_11', 'JOCOAITIQUE'),
('MUN_13_12', 'JOCORO'),
('MUN_13_13', 'LOLOTIQUILLO'),
('MUN_13_14', 'MEANGUERA'),
('MUN_13_15', 'OSICALA'),
('MUN_13_16', 'PERQUIN'),
('MUN_13_17', 'SAN CARLOS'),
('MUN_13_18', 'SAN FERNANDO'),
('MUN_13_19', 'SAN FRANCISCO GOTERA'),
('MUN_13_20', 'SAN ISIDRO'),
('MUN_13_21', 'SAN SIMON'),
('MUN_13_22', 'SENSEMBRA'),
('MUN_13_23', 'SOCIEDAD'),
('MUN_13_24', 'TOROL_A'),
('MUN_13_25', 'YAMABAL'),
('MUN_13_26', 'YOLOAIQUIN'),
('MUN_14_1', 'ANAMOROS'),
('MUN_14_2', 'BOLIVAR'),
('MUN_14_3', 'CONCEPCION DE ORIENTE'),
('MUN_14_4', 'CONCHAGUA'),
('MUN_14_5', 'EL CARMEN'),
('MUN_14_6', 'EL SAUCE'),
('MUN_14_7', 'INTIPUCA'),
('MUN_14_8', 'LA UNION'),
('MUN_14_9', 'LISLIQUE'),
('MUN_14_10', 'MEANGUERA DEL GOLFO'),
('MUN_14_11', 'NUEVA ESPARTA'),
('MUN_14_12', 'PASAQUINA'),
('MUN_14_13', 'POLOROS'),
('MUN_14_14', 'SAN ALEJO'),
('MUN_14_15', 'SAN JOSE'),
('MUN_14_16', 'SANTA ROSA DE LIMA'),
('MUN_14_17', 'YAYANTIQUE'),
('MUN_14_18', 'YUCUAIQUIN');

-- Catalogo de derechos 
INSERT INTO catalogo (codigo, descripcion) VALUES
('DER_1', 'Derecho a la Libertad Personal e Integridad personal'),
('DER_2', 'Derecho a la Libertad de Expresión'),
('DER_3', 'Derecho de Acceso a la Justicia'),
('DER_4', 'Derecho a la Vida');

-- Insertar Adminitrador
INSERT INTO usuario (nombre, email, contrasena_hash, ROL_codigo, creado_en, activo)
VALUES 
('Daniel Morales', '00139419@uca.edu.sv', '$2a$10$K7HufdmPI16g56n8O2hQtuk4mQ1RoXmx3uWB7AQO0FsIrjloRtTWG', 'ROL_1', CURRENT_TIMESTAMP, TRUE);

INSERT INTO recovery_password (usuario_id, pregunta_codigo, respuesta_hash, intentos_fallidos) 
VALUES
(1, 'SQ_1', '$2a$10$K7HufdmPI16g56n8O2hQtuk4mQ1RoXmx3uWB7AQO0FsIrjloRtTWG', 0);



