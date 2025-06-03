detencion_integridadINSERT INTO usuario (nombre, email, contrasena_hash, rol_codigo, creado_en, activo)
VALUES 
('Daniel Morales', '00139419@uca.edu.sv', '$2a$10$K7HufdmPI16g56n8O2hQtuk4mQ1RoXmx3uWB7AQO0FsIrjloRtTWG', 'ROL1', CURRENT_TIMESTAMP, TRUE);


select * from catalogo;
select * from auditoria;
SELECT * FROM usuario;
SELECT * FROM recovery_password;

UPDATE usuario SET contrasena_hash  = '$2a$10$PaAL.LAa0uao9YR3Eb4qeOvd6ukwo9VRWQB/pRn7nF.5XhbJKKl5u' WHERE email = '00139419@uca.edu.sv';

-- password '123' encriptada '$2a$10$K7HufdmPI16g56n8O2hQtuk4mQ1RoXmx3uWB7AQO0FsIrjloRtTWG'


DELETE FROM usuario  WHERE email = 'maria123@gmail.com';

ALTER TABLE auditoria ALTER COLUMN registro_id TYPE VARCHAR;


TRUNCATE TABLE auditoria;

INSERT INTO catalogo VALUES ('ROL_1', 'admin');


DELETE FROM catalogo  WHERE codigo IN ('ROL_1');


DELETE FROM acceso_justicia;
DELETE FROM expresion_censura;
DELETE FROM detencion_integridad;
DELETE FROM violencia;
DELETE FROM derecho_vulnerado;
DELETE FROM persona_afectada;
DELETE FROM ubicacion;
DELETE FROM registro_evento;


SELECT * FROM registro_evento;
SELECT * FROM ubicacion WHERE evento_id = 1;
SELECT * FROM persona_afectada WHERE evento_id = 1;
SELECT * FROM derecho_vulnerado WHERE persona_afectada_id = 1;
SELECT * FROM violencia WHERE persona_id = 1;
SELECT * FROM detencion_integridad WHERE persona_id = 1;
SELECT * FROM expresion_censura WHERE persona_id = 1;
SELECT * FROM acceso_justicia WHERE persona_id = 1;
