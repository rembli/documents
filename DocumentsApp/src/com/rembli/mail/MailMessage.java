package com.rembli.mail;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
public class MailMessage {
	String messageID;
	String from;
	String to;
	String subject;
	String sendDate;
		
}
