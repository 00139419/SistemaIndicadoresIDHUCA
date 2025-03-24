package com.uca.idhuca.sistema.indicadores.services.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.uca.idhuca.sistema.indicadores.dto.EmailRequest;
import com.uca.idhuca.sistema.indicadores.dto.SuperGenericResponse;
import com.uca.idhuca.sistema.indicadores.services.IEmailService;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.OK;
import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;

@Service
public class EmailServiceImpl implements IEmailService {
	
	@Value("${sendgrid.api.key}")
    private String sendGridApiKey;
	
	@Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
	
	@Override
    public SuperGenericResponse enviarCorreo(EmailRequest dto) {
    	SuperGenericResponse responseService = new SuperGenericResponse(ERROR, "Error generico en servicio!");
    	
        Email from = new Email("tucorreo@tudominio.com"); // Usa un correo verificado en SendGrid
        Email recipient = new Email(dto.getDestinatario());
        Content emailContent = new Content("text/plain", dto.getMensaje());
        Mail mail = new Mail(from, dto.getAsunto(), recipient, emailContent);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            responseService.setCodigo(OK);
            responseService.setMensaje("Código de respuesta: " + response.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return responseService;
    }
	
	
	public String sendEmail(EmailRequest dto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(dto.getDestinatario());
            message.setSubject(dto.getAsunto());
            message.setText(dto.getMensaje());

            mailSender.send(message);
            System.out.println("Ok");
            return "Correo enviado correctamente.";
        } catch (Exception e) {
        	e.printStackTrace();
            return "Error al enviar correo: " + e.getMessage();
        }
    }
}
