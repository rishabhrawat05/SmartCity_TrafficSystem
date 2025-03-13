package com.smartcity.trafficsystem.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailUtility {
	
	private JavaMailSender javaMailSender;
	
	public EmailUtility(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}
	
	@Async
	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		javaMailSender.send(message);
	}
}
