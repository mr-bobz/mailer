package com.springboot;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.springboot.mailer.MailServiceApp;
import com.springboot.mailer.send.MailContent;

//BUILD ERROR: Type mismatch: cannot convert from Class<SpringRunner> to Class<? extends Runner>
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailServiceApp.class)
@WebAppConfiguration
public class MailServiceSendMailControllerTests {
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
	
	private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private MailContent mailContent;
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);
        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }
    
    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }


	@Test
	public void noToAddress() throws Exception{
		mockMvc.perform(post("/mail/send/")
                .content(this.json(new MailContent()))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void noContent() throws Exception{
		MailContent mailContent = new MailContent();
		String[] to = {"bobbyj79@gmail.com"};
		mailContent.setTo(to);
		mockMvc.perform(post("/mail/send/")
                .content(this.json(mailContent))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void noProvider() throws Exception{
		MailContent mailContent = new MailContent();
		mailContent.setProviderName("outlook");
		mockMvc.perform(post("/mail/send/")
				.content(this.json(mailContent))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void sendsMail() throws Exception{
		MailContent mailContent = new MailContent();
		String[] to = {"bobbyj79@gmail.com"};
		mailContent.setTo(to);
		mailContent.setSubject("Unit Test Email");
		mailContent.setText("Testing 1... 2.... 3.....");
		
		mockMvc.perform(post("/mail/send/")
				.content(this.json(mailContent))
                .contentType(contentType))
                .andExpect(status().isOk());
	}
	
	@Test
	public void multipleTo() throws Exception{
		MailContent mailContent = new MailContent();
		String[] to = {"bobbyj79@gmail.com", "bobbyjoseph@gmail.com"};
		mailContent.setTo(to);
		mailContent.setSubject("Unit Test Email");
		mailContent.setText("Testing 1... 2.... 3.....");
		
		mockMvc.perform(post("/mail/send/")
				.content(this.json(mailContent))
                .contentType(contentType))
                .andExpect(status().isOk());
	}
	
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
	

}
