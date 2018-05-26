package com.springboot.mailer.send.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.springboot.mailer.error.InvalidInputException;
import com.springboot.mailer.send.MailContent;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@PropertySource("classpath:config.properties")
@Component
public class SendGridService implements MailService {
	
	/*
	//Sample from SendGrid
	//https://sendgrid.com/docs/API_Reference/Web_API_v3/Mail/index.html
	curl --request POST \
	  --url https://api.sendgrid.com/v3/mail/send \
	  --header 'Authorization: Bearer YOUR_API_KEY' \
	  --header 'Content-Type: application/json' \
	  --data '{"personalizations": [{"to": [{"email": "example@example.com"}]}],"from": {"email": "example@example.com"},"subject": "Hello, World!","content": [{"type": "text/plain", "value": "Heya!"}]}'
	}
	*/
	
	
	private static final Logger log = LoggerFactory.getLogger(SendGridService.class);
	private static final String providerName = "SendGrid";
	private static final String providerId = "SG";
	@Value("${SG_URL}")
	private String url;
	@Value("${SG_KEY}")
	private String accessToken;
	
	/*public SendGridService(RestTemplateBuilder restTemplateBuilder) throws IOException {
		Properties config = this.loadConfig();
		this.url = config.getProperty(providerId+"_URL");
		this.accessToken = config.getProperty(providerId+"_KEY");
	}*/
	
	@Override
	public String getProviderName() {
		return providerName;
	}
		
	@Override
	public ResponseEntity<?> sendMessage(MailContent mailContent) throws InvalidInputException {
			
		log.info("sendMessage called...");
		log.info(mailContent.toString());
		
		this.validate(mailContent);
		
		RestTemplate restTemplate = new RestTemplate();
				
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);	
		
		JSONObject mailJSON = new JSONObject();
		
		try {		
			
			JSONObject fromJSON = new JSONObject()
									.put("name", "SendGrid Sandbox")
									.put("email", "bobbyj79@gmail.com");
			mailJSON.put("from", fromJSON);
			
			JSONObject contentJSON = new JSONObject()
										.put("type", "text/plain")
										.put("value", mailContent.getText());
			mailJSON.put("content", new JSONArray().put(contentJSON));			
			mailJSON.put("subject", mailContent.getSubject());
		
			
			JSONArray toArray = new JSONArray();
			JSONArray ccArray = new JSONArray();
			JSONArray bccArray = new JSONArray();
			
			if (mailContent.getTo() != null && mailContent.getTo().length>0)
				Arrays.stream(mailContent.getTo()).forEach(to -> {
					try {
						JSONObject toJSON = new JSONObject();
						toJSON.put("email", to);
						toArray.put(toJSON);
					}catch(JSONException jsonException) {
						jsonException.printStackTrace();
					}
				});
			if (mailContent.getCc() != null && mailContent.getCc().length>0)
				Arrays.stream(mailContent.getCc()).forEach(cc -> {
					try {
						JSONObject ccJSON = new JSONObject();
						ccJSON.put("email", cc);
						ccArray.put(ccJSON);
					}catch(JSONException jsonException) {
						jsonException.printStackTrace();
					}
				});
			if (mailContent.getBcc() != null && mailContent.getBcc().length>0)
				Arrays.stream(mailContent.getBcc()).forEach(bcc -> {
					try {
						JSONObject bccJSON = new JSONObject();
						bccJSON.put("email", bcc);
						bccArray.put(bccJSON);
					}catch(JSONException jsonException) {
						jsonException.printStackTrace();
					}
				});
			
			
			JSONArray personalizations = new JSONArray();
			JSONObject personalizationJSON = new JSONObject();
	
			if(toArray.length()>0)
				personalizationJSON.put("to", toArray);
			if(ccArray.length()>0)
				personalizationJSON.put("cc", ccArray);
			if(bccArray.length()>0)
				personalizationJSON.put("bcc", bccArray);
			
			personalizations.put(personalizationJSON);
			mailJSON.put("personalizations", personalizations);
			
			log.info("mailJSON:"+mailJSON);
			
		}catch(JSONException jsonException) {
			jsonException.printStackTrace();
		}
		
		
		HttpEntity<String> request = new HttpEntity<>(mailJSON.toString(), headers);	
		log.info("request:"+request.toString());		
		ResponseEntity<?> response = restTemplate.exchange( url, HttpMethod.POST, request , JSONObject.class );
		log.info("response:"+response.toString());			
		
		return response;
		
	}

}

