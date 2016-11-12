package com.rembli.test;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.rembli.dms.mimeparser.MimeMessageConverter;

public class TestEml2Pdf {

	public static void main(String[] args) {

	    Properties props = new Properties();
	    props.setProperty("mail.store.protocol", "imaps");
	    try {
	        Session session = Session.getInstance(props, null);
	        Store store = session.getStore();
	        //read your latest email
	        store.connect("imap.udag.de", "info@rembli.com", "rembliMail2016");
	        
	        Folder inbox = store.getFolder("INBOX");
	        inbox.open(Folder.READ_ONLY);
	        Message msg = inbox.getMessage(inbox.getMessageCount());
			
			// convert eml to html
	        org.w3c.dom.Document document = MimeMessageConverter.convertToXHTML(msg);
	        System.out.println(getStringFromDocument(document));
	        
	        //save it as pdf
		    OutputStream pdf_out = new FileOutputStream("Test.pdf");
		    ITextRenderer renderer = new ITextRenderer();
		    renderer.setDocument(document, null);
		    renderer.layout();
		    renderer.createPDF(pdf_out) ;
		    pdf_out.close();		    
	        
	    } catch (Exception mex) {
	        mex.printStackTrace();
	    }
	}

	public static String getStringFromDocument(org.w3c.dom.Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }
	} 
	
}


