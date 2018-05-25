package com.springboot.mailer.send;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.mailer.error.InvalidInputException;
import com.springboot.mailer.send.providers.MailService;
import com.springboot.mailer.send.providers.MailServiceFactory;

@RestController
public class MailServiceSendMailController {

	@PostMapping("/mail/send")
	public ResponseEntity<?> sendMail(@RequestBody MailContent mailContent) throws InvalidInputException, IOException {
		// return new ResponseEntity<String>("ACK", HttpStatus.OK);
		// return mailGunService.sendMessage(mailContent);
		// return sendGridService.sendMessage(mailContent);

		List<MailService> mailServices = null;
		mailServices = MailServiceFactory.getMailServices(mailContent.getProviderName());
		ArrayList<ResponseEntity<?>> responses = new ArrayList<ResponseEntity<?>>();

		if (mailServices == null || mailServices.size() == 0) {
			InvalidInputException invalidInputException = new InvalidInputException("Invalid provider name provided, no providers found!");
			invalidInputException.printStackTrace();
			throw invalidInputException;
		} else {
			for (MailService mailService : mailServices) {
				ResponseEntity<?> response = mailService.sendMessage(mailContent);
				responses.add(response);
				//Break loop if any of the mail provider succeed
				if(response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED)
					break;
			}
		}

		return new ResponseEntity<Object>(responses, HttpStatus.OK);
	}
}
