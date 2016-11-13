package com.rembli.mail;

import java.io.*;
import java.util.*;
import javax.ws.rs.NotAuthorizedException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.activation.DataHandler;
import javax.mail.*;

import com.google.common.io.ByteStreams;
import com.rembli.dms.DocumentManagementSystem;
import com.rembli.ums.*;
import com.rembli.util.mimeparser.*;



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
		String mail_domain = props.getProperty("MAIL.DOMAIN");		
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
	       	   	  String fileName = message.getSubject();
	       	   	  dms.createDocumentFromMessage (fileName, message);
	       	   	  
	   			  // delete message from inbox
	   			  message.setFlag(Flags.Flag.DELETED, true);
	    	  }
	    	  catch (Exception ignored) {
	    		  ignored.printStackTrace();
	    	  }
	      }
	    }
	    
	    inbox.close(true);
	    store.close();			
		// LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, id.toString(), LogEntry.ACTION.CREATE, "New document "+id.toString()+" imported from mail by user "+ username);   			
	}
	
	 private void saveMailToDisk (javax.mail.Message msg, String subject) throws MessagingException, IOException
	 {
	   String whereToSave = "c:/temp/" + com.rembli.util.text.TextTools.sanitizeString(subject) + ".eml";
	   OutputStream out = new FileOutputStream(new File(whereToSave));
	   try {
	       msg.writeTo(out);
	   }
	   finally {
	       if (out != null) { out.flush(); out.close(); }
	   }
	 }	
	

	
}
