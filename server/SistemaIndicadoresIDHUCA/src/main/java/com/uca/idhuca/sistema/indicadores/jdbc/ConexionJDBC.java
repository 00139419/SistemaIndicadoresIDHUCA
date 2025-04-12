package com.uca.idhuca.sistema.indicadores.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.utils.Constantes;
import com.uca.idhuca.sistema.indicadores.utils.ProjectProperties;

@Repository
public class ConexionJDBC {

	@Autowired
	ProjectProperties projectProperties;

	public SuperGenericResponse testDbConecction() {
		SuperGenericResponse resonse = new SuperGenericResponse(Constantes.ERROR, "Error generico conectando a la DB");
		Connection con = null;
		
		try {
			con = DriverManager.getConnection(
					projectProperties.getJdbcUrl(),
					projectProperties.getJdbcUsername(),
					projectProperties.getJdbcPassword()
					);
			
			if(con != null) {
				resonse.setCodigo(Constantes.OK);
				resonse.setMensaje("Conexion exitosa a la base de datos.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			resonse.setMensaje("Error conectando a la base de datos. MJS: " + e.getMessage());
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return resonse;
	}
}
