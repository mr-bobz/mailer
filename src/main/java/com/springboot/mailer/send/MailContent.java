package com.springboot.mailer.send;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailContent {
	private String subject;
	private String from;
	private String[] to;
	private String[] cc;
	private String[] bcc;
	private String text;
	private String providerName;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{"
					+ "subject:" + subject + ", "
					+ "from:" + from + ", "
					+ "to:" + Arrays.toString(to) + ", "
					+ "cc:" + Arrays.toString(cc) + ", "
					+ "bcc:" + Arrays.toString(bcc) + ", "
				    + "text:" + text + ", " 
				    + "providerName:" + providerName + 
			    "}";
	}	
	
}
