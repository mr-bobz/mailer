/**
 * 
 */
package com.springboot.mailer.send.providers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.http.ResponseEntity;

import com.springboot.mailer.error.InvalidInputException;
import com.springboot.mailer.send.MailContent;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * All mail service providers should implement this interface
 *
 */
public interface MailService {

	public ResponseEntity<?> sendMessage(MailContent mailContent) throws InvalidInputException, IOException;

	public String getProviderName();

	// load config
	default public Properties loadConfig() throws IOException {
		Properties prop = new Properties();
		InputStream input = MailService.class.getResourceAsStream("config.properties");
		prop.load(input);
		input.close();
		return prop;
	}

	// Adding some basic validations
	default public void validate(MailContent mailContent) throws InvalidInputException {
		InvalidInputException invalidInputException = null;

		// Moving input validation to Spring Validator
		// Issue with Spring Validator, reverting to manual validation

		// Is there To address?
		if (mailContent.getTo() == null || mailContent.getTo().length == 0) {
			invalidInputException = new InvalidInputException("There should be atleast one To address!");
		}
		// Is there Text content?
		else if (mailContent.getText() == null || mailContent.getText().length() == 0) {
			invalidInputException = new InvalidInputException("Empty email, pls set text!");
		}
		// Are the emails valid?
		else if (mailContent.getTo().length != 0) {
			if(!isValidEmails(mailContent.getTo())) 
				invalidInputException = new InvalidInputException("Invalid email provided for To");
		}else if (mailContent.getCc().length != 0) {
			if(!isValidEmails(mailContent.getCc())) 
				invalidInputException = new InvalidInputException("Invalid email provided for CC");
		}else if (mailContent.getBcc().length != 0) {
			if(!isValidEmails(mailContent.getBcc())) 
				invalidInputException = new InvalidInputException("Invalid email provided for BCC");
		}

		if (invalidInputException != null) {
			invalidInputException.printStackTrace();
			throw invalidInputException;
		}
	}

	default public boolean isValidEmails(String[] emails) {
		boolean result = true;
		for (String email : emails) {
			if (!isValidEmail(email)) {
				result = false;
				break;
			}
		}
		return result;
	}

	default public boolean isValidEmail(String email) {
		if(email.contains("<") && email.contains(">"))
			email = email.substring(email.indexOf('<')+1, email.indexOf('>')-1);
		boolean result = EmailValidator.getInstance().isValid(email);
		return result;
	}

	// Bug DATAREST-524: Validator auto discovery not working for Spring Data Rest
	// https://jira.spring.io/browse/DATAREST-524
	// java.lang.NoClassDefFoundError: net/minidev/json/writer/JsonReaderI
	/*
	 * @Bean default public MailContentValidator beforesendMessageValidator() {
	 * return new MailContentValidator(); }
	 */

}
