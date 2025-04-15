INSERT INTO usuario (nombre, email, contrasena_hash, rol_codigo, creado_en, activo)
VALUES 
('Daniel Morales', '00139419@uca.edu.sv', '1234', 'ROL1', CURRENT_TIMESTAMP, TRUE);


SELECT * FROM usuario;

UPDATE usuario SET rol_codigo = 'ROL2' WHERE email = '00139419@uca.edu.sv';
