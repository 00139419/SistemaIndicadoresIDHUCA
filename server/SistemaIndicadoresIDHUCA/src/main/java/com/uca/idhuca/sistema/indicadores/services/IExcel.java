package com.uca.idhuca.sistema.indicadores.services;

import java.io.OutputStream;
import java.util.List;

import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;

public interface IExcel {

	public void generarExcel(List<RegistroEvento> datos, OutputStream outputStream, String key) throws Exception;
}
