package com.rembli.test;

import java.io.FileInputStream;
import java.util.Properties;
import com.rembli.mail.*;
import javax.mail.*;

public class TestMail {

	public static void main(String[] args) throws Exception {
		
		String mailServerPropertiesPath = (new MailServerPropertiesPath()).getPath();
		FileInputStream fis = new FileInputStream(mailServerPropertiesPath);
		Properties props = new Properties();
		props.load(fis);

		// get credentials to access mail
		String pop = props.getProperty("MAIL.POP");
		String smtp = props.getProperty("MAIL.SMTP");
		String username = props.getProperty("MAIL.USERNAME");
		String password = props.getProperty("MAIL.PASSWORD");
		
		// connect to pop-inbox
	    Properties properties = System.getProperties();
	    Session session = Session.getDefaultInstance(properties);
	    Store store = session.getStore("pop3");
	    store.connect(pop, username, password);
	    Folder inbox = store.getFolder("Inbox");
	    inbox.open(Folder.READ_ONLY);

	    // get the list of inbox messages
	    Message[] messages = inbox.getMessages();

	    if (messages.length == 0) System.out.println("No messages found.");

	    for (int i = 0; i < messages.length; i++) {
	      // stop after listing ten messages
	      if (i > 10) {
	        System.exit(0);
	      }

	      System.out.println("Message " + (i + 1));
	      System.out.println("From : " + messages[i].getFrom()[0]);
	      System.out.println("To : " + messages[i].getAllRecipients()[0]);
	      System.out.println("Subject : " + messages[i].getSubject());
	      System.out.println("Sent Date : " + messages[i].getSentDate());
	      System.out.println();
	    }
	    
	    inbox.close(true);
	    store.close();		
	}
}

