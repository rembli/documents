package com.rembli.mail;

import java.io.*;
import java.util.*;
import javax.ws.rs.NotAuthorizedException;
import javax.mail.*;
import com.rembli.dms.DocumentManagementSystem;
import com.rembli.log.*;
import com.rembli.ums.*;

public class MailManagementSystem {
	private boolean isAuthenticated = false;
	private String username = null;
	private DocumentManagementSystem dms = null;

	public MailManagementSystem (String accessToken) throws Exception {
		UserManagementSystem ums = new UserManagementSystem ();
		isAuthenticated = ums.isAuthenticated (accessToken);
		if (!isAuthenticated) throw new NotAuthorizedException("Valid access token must be provided");
		username = ums.getUsername(accessToken);
		dms = new DocumentManagementSystem (accessToken);
	}
	
	public static String getMailDomain () throws IOException {
		String mailServerPropertiesPath = (new MailServerPropertiesPath()).getPath();
		FileInputStream fis = new FileInputStream(mailServerPropertiesPath);
		Properties props = new Properties();
		props.load(fis);
		return props.getProperty("MAIL.DOMAIN");		
	}
	
	public ArrayList<MailMessage> getMails () throws Exception {
		
		String mailServerPropertiesPath = (new MailServerPropertiesPath()).getPath();
		FileInputStream fis = new FileInputStream(mailServerPropertiesPath);
		Properties props = new Properties();
		props.load(fis);

		// get credentials to access mail
		String mail_imap = props.getProperty("MAIL.IMAP");
		String mail_domain = props.getProperty("MAIL.DOMAIN");		
		String mail_username = props.getProperty("MAIL.USERNAME");
		String mail_password = props.getProperty("MAIL.PASSWORD");
		
		// connect to pop-inbox
	    Properties properties = System.getProperties();
	    Session session = Session.getDefaultInstance(properties);
	    Store store = session.getStore("imaps");
	    store.connect(mail_imap, mail_username, mail_password);
	    Folder inbox = store.getFolder("Inbox");
	    inbox.open(Folder.READ_ONLY);

	    // get the list of inbox messages
	    Message[] messages = inbox.getMessages();
	    ArrayList<MailMessage> mailMessageList = new ArrayList<MailMessage>();

	    for (int i = 0; i < messages.length; i++) {
	      String to = messages[i].getAllRecipients()[0].toString();
	      if (to.equalsIgnoreCase(username+"@"+mail_domain)) {
	    	 
	    	  Message msg = messages[i];
	    	  
	  	      MailMessage mailMessage = new MailMessage();
	    	  mailMessage = new MailMessage();
	    	  
	    	  /*
	    	  Enumeration e = msg.getAllHeaders();
	    	  while (e.hasMoreElements()) {
	    		  javax.mail.Header h =  (Header) e.nextElement();
	    		  String name = h.getName();
	    		  String value = h.getValue();
	    		  System.out.println (name+": "+value);
	    	  }
	    	  */
	    	  
	    	  mailMessage.setFrom (msg.getFrom()[0].toString());
	    	  mailMessage.setTo (to);
	    	  mailMessage.setSubject (msg.getSubject()); 
	    	  mailMessage.setSendDate (msg.getSentDate().toLocaleString());
	    	  mailMessageList.add (mailMessage);
	      }
	    }
	    
	    inbox.close(true);
	    store.close();			
		
		return mailMessageList;
	}

	public void exportToDocs () throws Exception {
		
		String mailServerPropertiesPath = (new MailServerPropertiesPath()).getPath();
		FileInputStream fis = new FileInputStream(mailServerPropertiesPath);
		Properties props = new Properties();
		props.load(fis);

		// get credentials to access mail
		String mail_imap = props.getProperty("MAIL.IMAP");
		String mail_domain = props.getProperty("MAIL.DOMAIN");		
		String mail_username = props.getProperty("MAIL.USERNAME");
		String mail_password = props.getProperty("MAIL.PASSWORD");
		
		// connect to pop-inbox
	    Properties properties = System.getProperties();
	    Session session = Session.getDefaultInstance(properties);
	    Store store = session.getStore("imaps");
	    store.connect(mail_imap, mail_username, mail_password);
	    Folder inbox = store.getFolder("Inbox");
	    inbox.open(Folder.READ_WRITE);

	    // get the list of inbox messages
	    Message[] messages = inbox.getMessages();
	    
	    for (int i = 0; i < messages.length; i++) {
	      String to = messages[i].getAllRecipients()[0].toString();
	      if (to.equalsIgnoreCase(username+"@"+mail_domain)) {
	    	  try {
		    	  // get message meta data
		    	  Message message = messages[i];

	   			  // delete message from inbox
	   			  message.setFlag(Flags.Flag.DELETED, true);
	   			  
	   			  // import message
		    	  String fileName = message.getSubject();
		    	  long idDocument = dms.createDocumentFromMessage (fileName, message);
	       	   	  LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, idDocument+"", LogEntry.ACTION.CREATE, "New document "+idDocument+" imported from mail by user "+ username);   			
	    	  }
	    	  catch (Exception ignored) {
	    		  ignored.printStackTrace();
	    	  }
	      }
	    }
	    
	    inbox.close(true);
	    store.close();			
	}
	
}
