/*
 Main Class
 @author bobbyj79@gmail.com
 */

package com.springboot.mailer;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailServiceApp {

	//When did the app start	
	private static Date startTime = null;

	//Used by /mail/status route	
	public static String getStartTime() {
		return startTime.toString();
	}

	public static void main(String[] args) {
		MailServiceApp.startTime = new Date();
		System.out.println("Starting MailService Application at: " + MailServiceApp.startTime);
		SpringApplication.run(MailServiceApp.class, args);
	}
}
