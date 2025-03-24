package com.uca.idhuca.sistema.indicadores.services;

import com.uca.idhuca.sistema.indicadores.dto.EmailRequest;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;

public interface IEmailService {
	 public SuperGenericResponse enviarCorreo(EmailRequest request);
	 
	 String sendEmail(EmailRequest dto);
}
