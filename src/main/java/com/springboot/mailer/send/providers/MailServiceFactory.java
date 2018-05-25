package com.springboot.mailer.send.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailServiceFactory {
	
	@Autowired
    private List<MailService> mailServices;
	
	private static final Map<String, MailService> myServiceCache = new HashMap<>();
	private static final Logger log = LoggerFactory.getLogger(MailServiceFactory.class);
	
	@PostConstruct
    public void initMyServiceCache() {
        for(MailService service : mailServices) {
            myServiceCache.put(service.getProviderName(),service);
        }
    }
		
	public static List<MailService> getMailServices(String providerName) {
		log.info("providerName: "+providerName);
		List<MailService> mailServices = new ArrayList<MailService>();
		if(providerName != null) {
			if(myServiceCache.get(providerName) != null)
				mailServices.add(myServiceCache.get(providerName));
		}else {
			for (Map.Entry<String, MailService> entry : myServiceCache.entrySet())
				mailServices.add(entry.getValue());
		}
		log.info("mailServices: "+mailServices);
		return mailServices;
	}
}
