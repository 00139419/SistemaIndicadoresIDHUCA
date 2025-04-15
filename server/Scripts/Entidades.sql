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
    rol_codigo VARCHAR REFERENCES catalogo(codigo),
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla auditoria
CREATE TABLE auditoria (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuario(id),
    tabla_afectada VARCHAR NOT NULL,
    operacion VARCHAR NOT NULL,
    registro_id INT,
    descripcion TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla nota_derecho
CREATE TABLE nota_derecho (
    id SERIAL PRIMARY KEY,
    derecho_codigo VARCHAR REFERENCES catalogo(codigo),
    fecha DATE NOT NULL,
    titulo VARCHAR,
    descripcion TEXT,
    archivo_url TEXT, -- ruta al archivo en el servidor
    creado_por INT REFERENCES usuario(id),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
    departamento_codigo VARCHAR REFERENCES catalogo(codigo),
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
    departamento_residencia_codigo VARCHAR REFERENCES catalogo(codigo),
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