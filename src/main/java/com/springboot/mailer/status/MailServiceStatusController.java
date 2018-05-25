package com.springboot.mailer.status;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.mailer.MailServiceApp;

@RestController
public class MailServiceStatusController {
	
	@RequestMapping("/mail/status")
	public String status() {
		return "Up and running since "+MailServiceApp.getStartTime()+" ...";
	}
}
