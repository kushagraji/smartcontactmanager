package com.kushagra.project.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to, String from) {

		// variable for gmail
		String host = "smtp.gmail.com";

		// get system properties
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES" + properties);

		// host set

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step1: To get session object

		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("kushagragupta76@gmail.com","dwixxodjhuvepfxk");
			}

		});

		session.setDebug(true);

		// session2:Compose message

		MimeMessage mimeMessage = new MimeMessage(session);

		try {

			// from email
			mimeMessage.setFrom(from);

			// adding recipient to message

			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message

			mimeMessage.setSubject(subject);

			// adding text to message

			//mimeMessage.setText(message);
			mimeMessage.setContent(message, "text/html");

			// Step3:send message using transport class

			Transport.send(mimeMessage);

			System.out.println("sent success..............");

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
