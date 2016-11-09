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
	
	public ArrayList<MailMessage> getMails () throws Exception {
		
		String mailServerPropertiesPath = (new MailServerPropertiesPath()).getPath();
		FileInputStream fis = new FileInputStream(mailServerPropertiesPath);
		Properties props = new Properties();
		props.load(fis);

		// get credentials to access mail
		String mail_pop = props.getProperty("MAIL.POP");
		String mail_host = props.getProperty("MAIL.HOST");		
		String mail_username = props.getProperty("MAIL.USERNAME");
		String mail_password = props.getProperty("MAIL.PASSWORD");
		
		// connect to pop-inbox
	    Properties properties = System.getProperties();
	    Session session = Session.getDefaultInstance(properties);
	    Store store = session.getStore("pop3");
	    store.connect(mail_pop, mail_username, mail_password);
	    Folder inbox = store.getFolder("Inbox");
	    inbox.open(Folder.READ_ONLY);

	    // get the list of inbox messages
	    Message[] messages = inbox.getMessages();
	    ArrayList<MailMessage> mailMessageList = new ArrayList<MailMessage>();

	    for (int i = 0; i < messages.length; i++) {
	      String to = messages[i].getAllRecipients()[0].toString();
	      if (to.equalsIgnoreCase(username+"@"+mail_host)) {
	    	 
	    	  Message msg = messages[i];
	    	  
	  	      MailMessage mailMessage = new MailMessage();
	    	  mailMessage = new MailMessage();
	    	  mailMessage.setFrom(msg.getFrom()[0].toString());
	    	  mailMessage.setTo(to);
	    	  mailMessage.setSubject(msg.getSubject()); 
	    	  mailMessage.setSendDate(msg.getSentDate().toLocaleString());
	    	  mailMessageList.add(mailMessage);
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
		String mail_pop = props.getProperty("MAIL.POP");
		String mail_host = props.getProperty("MAIL.HOST");		
		String mail_username = props.getProperty("MAIL.USERNAME");
		String mail_password = props.getProperty("MAIL.PASSWORD");
		
		// connect to pop-inbox
	    Properties properties = System.getProperties();
	    Session session = Session.getDefaultInstance(properties);
	    Store store = session.getStore("pop3");
	    store.connect(mail_pop, mail_username, mail_password);
	    Folder inbox = store.getFolder("Inbox");
	    inbox.open(Folder.READ_WRITE);

	    // get the list of inbox messages
	    Message[] messages = inbox.getMessages();

	    for (int i = 0; i < messages.length; i++) {
	      String to = messages[i].getAllRecipients()[0].toString();
	      if (to.equalsIgnoreCase(username+"@"+mail_host)) {
	    	 
	    	  Message msg = messages[i];
	    	  msg.setFlag(Flags.Flag.DELETED, true);
       	   	  String fileName = sanitizeFilename(msg.getSubject())+".eml";
	    	  String fileType = "message/rfc822";
	    	  
			  ByteArrayOutputStream out = new ByteArrayOutputStream();	    	
			   try {
			       msg.writeTo(out);
			   }
			   finally {
			       if (out != null) { out.flush(); out.close(); }
			   }	    	  
			   byte[] data = out.toByteArray();
			   ByteArrayInputStream in = new ByteArrayInputStream(data);
	    	  
	    	   dms.createDocument(fileName, fileType, in);
	      }
	    }
	    
	    inbox.close(true);
	    store.close();			
		
		// LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, id.toString(), LogEntry.ACTION.CREATE, "New document "+id.toString()+" imported from mail by user "+ username);   			
	}
	
	
	 private void saveMailToDisk (javax.mail.Message msg, String subject) throws MessagingException, IOException
	 {
	   String whereToSave = "c:/temp/" + sanitizeFilename(subject) + ".eml";
	   OutputStream out = new FileOutputStream(new File(whereToSave));
	   try {
	       msg.writeTo(out);
	   }
	   finally {
	       if (out != null) { out.flush(); out.close(); }
	   }
	 }	
	
	private static String sanitizeFilename(String name) {
		   return name.replaceAll("[:\\\\/*?|<> \"]", "_");
	}	
	
}
