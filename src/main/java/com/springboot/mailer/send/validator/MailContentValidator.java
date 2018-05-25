package com.springboot.mailer.send.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.springboot.mailer.send.MailContent;
import com.springboot.mailer.send.providers.SendGridService;

//Bug DATAREST-524: Validator auto discovery not working for Spring Data Rest
//https://jira.spring.io/browse/DATAREST-524

@Component("beforeSendMessageValidator")
public class MailContentValidator implements Validator {
	
	private static final Logger log = LoggerFactory.getLogger(SendGridService.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return MailContent.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		log.info("validate called.......");
		MailContent mailContent = (MailContent) obj;
		
        if (checkInputString(mailContent.getText())) {
            errors.rejectValue("text", "text.empty");
        }
        
        if(mailContent.getTo() == null || mailContent.getTo().length==0) {
        	 errors.rejectValue("to", "to.empty");
        }               
    
	}
	
	private boolean checkInputString(String input) {
        return (input == null || input.trim().length() == 0);
    }

}
