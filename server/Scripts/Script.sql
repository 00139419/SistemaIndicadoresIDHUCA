INSERT INTO usuario (nombre, email, contrasena_hash, rol_codigo, creado_en, activo)
VALUES 
('Daniel Morales', '00139419@uca.edu.sv', '1234', 'ROL1', CURRENT_TIMESTAMP, TRUE);


SELECT * FROM usuario;

UPDATE usuario SET contrasena_hash  = '$2a$10$PaAL.LAa0uao9YR3Eb4qeOvd6ukwo9VRWQB/pRn7nF.5XhbJKKl5u' WHERE email = '00139419@uca.edu.sv';

-- password '123' encriptada '$2a$10$K7HufdmPI16g56n8O2hQtuk4mQ1RoXmx3uWB7AQO0FsIrjloRtTWG'
