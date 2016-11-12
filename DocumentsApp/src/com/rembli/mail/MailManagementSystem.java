package com.rembli.mail;

import java.io.*;
import java.util.*;
import javax.ws.rs.NotAuthorizedException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.activation.DataHandler;
import javax.mail.*;

import com.google.common.io.ByteStreams;
import com.rembli.dms.DocumentManagementSystem;
import com.rembli.dms.mimeparser.*;
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
	    inbox.open(Folder.READ_WRITE);

	    // get the list of inbox messages
	    Message[] messages = inbox.getMessages();
	    
	    for (int i = 0; i < messages.length; i++) {
	      String to = messages[i].getAllRecipients()[0].toString();
	      if (to.equalsIgnoreCase(username+"@"+mail_domain)) {
	    	  try {
	    		  
	    	  // get message meta data
	    	  Message msg = messages[i];
       	   	  String fileName = msg.getSubject();
       	   	  String fileNameS = com.rembli.util.text.TextTools.sanitizeString(fileName);
       	   	  long idDocument = dms.createDocument(fileName);

			  // convert and attach pdf
		      org.w3c.dom.Document document = MimeMessageConverter.convertToXHTML(msg);
		      ByteArrayOutputStream out_pdf = new ByteArrayOutputStream ();
			  ITextRenderer renderer = new ITextRenderer();
			  renderer.setDocument(document, null);
			  renderer.layout();
			  renderer.createPDF(out_pdf) ;
			  byte[] data = out_pdf.toByteArray();
			  out_pdf.close();		    
			  ByteArrayInputStream in_pdf = new ByteArrayInputStream(data);			    
			  dms.attachFile(idDocument, "Preview.pdf", "application/pdf", in_pdf);    
   	   	  
       	   	  // attach eml
       	   	  ByteArrayOutputStream out_eml = new ByteArrayOutputStream();	    	
       	   	  try {
       	   		  msg.writeTo(out_eml);
       	   	  }
       	   	  finally {
       	   		  if (out_eml != null) { out_eml.flush(); out_eml.close(); }
       	   	  }	    	  
       	   	  data = out_eml.toByteArray();  
    		  ByteArrayInputStream in_eml = new ByteArrayInputStream(data);
       	   	  dms.attachFile(idDocument, fileNameS+".eml", "message/rfc822", in_eml);
       	   	        	   	  
       	   	  // save attachments
     		  List<Part> attachmentParts = MimeMessageParser.getAttachments(msg);
   			  for (int j = 0; j < attachmentParts.size(); j++) {
      				Part part = attachmentParts.get(j);
      				String attachmentFilename = null;
      				String attachmentFileType = null;
      				try {
      					attachmentFilename = part.getFileName();
      					attachmentFileType = part.getContentType();
      				} catch (Exception e) {
      					// ignore this error
      				}
      				dms.attachFile(idDocument, attachmentFilename, attachmentFileType, part.getInputStream());
      			}
   			  
   			  	// delete message from inbox
   			  	msg.setFlag(Flags.Flag.DELETED, true);
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
