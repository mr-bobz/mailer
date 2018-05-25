package com.springboot.mailer.send.providers;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.springboot.mailer.error.InvalidInputException;
import com.springboot.mailer.send.MailContent;

import java.io.IOException;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Component
public class MailGunService implements MailService{
	
	/*
	//Sample from MailGun
	//https://documentation.mailgun.com/en/latest/api-sending.html#sending
	public static ClientResponse SendSimpleMessage() {
	    Client client = Client.create();
	    client.addFilter(new HTTPBasicAuthFilter("api", "YOUR_KEY"));
	    WebResource webResource = client.resource("https://api.mailgun.net/v3/sandbox731e93904f9b4a77b58a9622d7fd20ed.mailgun.org/messages");
	    MultivaluedMapImpl formData = new MultivaluedMapImpl();
	    formData.add("from", "Mailgun Sandbox <postmaster@sandbox731e93904f9b4a77b58a9622d7fd20ed.mailgun.org>");
	    formData.add("to", "Bobby <bobbyj79@gmail.com>");
	    formData.add("subject", "Hello Bobby");
	    formData.add("text", "Congratulations Bobby, you just sent an email with Mailgun!  You are truly awesome!");
	    return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
	                                        post(ClientResponse.class, formData);
	}
	*/
	
	private String url;
	private String apiKey;
	private static final Logger log = LoggerFactory.getLogger(MailGunService.class);
	private static final String providerName = "MailGun";
	private static final String providerId = "MG";
	
	private final RestTemplate restTemplate;

	public MailGunService(RestTemplateBuilder restTemplateBuilder) throws IOException {
		Properties config = this.loadConfig();
		this.url = config.getProperty(providerId+"_URL");
		this.apiKey = config.getProperty(providerId+"_KEY");
		this.restTemplate = restTemplateBuilder
								//.customizers(new LoggingCustomizer())
								.basicAuthorization("api", apiKey)
								.build();
		
	}

	@Override
	public String getProviderName() {
		return providerName;
	}

	@Override
	public ResponseEntity<?> sendMessage(MailContent mailContent) throws InvalidInputException {

		log.info("sendMessage called...");
		log.info(mailContent.toString());		
		
		
		this.validate(mailContent);
		
		String toList = "";
		String ccList = "";
		String bccList = "";
		
		if (mailContent.getTo() != null && mailContent.getTo().length>0)
			toList =  Stream.of(mailContent.getTo()).collect(Collectors.joining(", "));
		if (mailContent.getCc() != null && mailContent.getCc().length>0)
			ccList = Stream.of(mailContent.getCc()).collect(Collectors.joining(", "));
		if (mailContent.getBcc() != null && mailContent.getBcc().length>0)
			bccList = Stream.of(mailContent.getBcc()).collect(Collectors.joining(", "));
		
		ResponseEntity<?> response = null;
		HttpResponse<JsonNode> request = null;
		
		if(ccList != "" && bccList != "") {
			try{
				request = Unirest.post(url)
		                .basicAuth("api", apiKey)
			            .queryString("from", "Mailgun Sandbox <postmaster@sandbox731e93904f9b4a77b58a9622d7fd20ed.mailgun.org>")
			            .queryString("to", toList)
			            .queryString("cc", ccList)
			            .queryString("bcc", bccList)
			            .queryString("subject", mailContent.getSubject())
			            .queryString("text", mailContent.getText())
			            .asJson();
			}catch(UnirestException ex) {
				ex.printStackTrace();
				response = ResponseEntity.badRequest().body(request.getBody());
			}
		}else if (ccList != "") {
			try{
				request = Unirest.post(url)
		                .basicAuth("api", apiKey)
			            .queryString("from", "Mailgun Sandbox <postmaster@sandbox731e93904f9b4a77b58a9622d7fd20ed.mailgun.org>")
			            .queryString("to", toList)
			            .queryString("cc", ccList)
			            .queryString("subject", mailContent.getSubject())
			            .queryString("text", mailContent.getText())
			            .asJson();
			}catch(UnirestException ex) {
				ex.printStackTrace();
				response = ResponseEntity.badRequest().body(request.getBody());
			}
		}else if (bccList != "") {
			try{
				request = Unirest.post(url)
		                .basicAuth("api", apiKey)
			            .queryString("from", "Mailgun Sandbox <postmaster@sandbox731e93904f9b4a77b58a9622d7fd20ed.mailgun.org>")
			            .queryString("to", toList)
			            .queryString("bcc", bccList)
			            .queryString("subject", mailContent.getSubject())
			            .queryString("text", mailContent.getText())
			            .asJson();
			}catch(UnirestException ex) {
				ex.printStackTrace();
				response = ResponseEntity.badRequest().body(request.getBody());
			}
		}else{
			try{
				request = Unirest.post(url)
		                .basicAuth("api", apiKey)
			            .queryString("from", "Mailgun Sandbox <postmaster@sandbox731e93904f9b4a77b58a9622d7fd20ed.mailgun.org>")
			            .queryString("to", toList)
			            .queryString("subject", mailContent.getSubject())
			            .queryString("text", mailContent.getText())
			            .asJson();
			}catch(UnirestException ex) {
				ex.printStackTrace();
				response = ResponseEntity.badRequest().body(request.getBody());
			}
		}
		
		
		
		
	
		log.info("request:"+ request.toString());		
		log.info("response:"+ request.getBody());
		
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		if(response == null)
			/*try {
				response = new ResponseEntity<Object>(om.readValue(request.getBody().toString(), JSONObject.class), HttpStatus.valueOf(request.getStatus()));
			} catch (Exception e) {
				e.printStackTrace();
				response = new ResponseEntity<Object>( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}*/
			response = new ResponseEntity<Object>(request.getBody().toString(), HttpStatus.valueOf(request.getStatus()));

		return response;

	}

}

