package com.rembli.dms;

import java.io.*;
import java.util.*;

import javax.ws.rs.NotAuthorizedException;

import com.rembli.log.*;
import com.rembli.ums.*;
import com.rembli.util.db.*;
import com.rembli.util.mimeparser.*;
import org.sql2o.*;

import org.apache.commons.io.FileUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.jpedal.examples.images.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;

import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import static java.nio.charset.StandardCharsets.*;

public class DocumentManagementSystem {

	private boolean isAuthenticated = false;
	private String username = null;

	public DocumentManagementSystem (String accessToken) throws Exception {
		UserManagementSystem ums = new UserManagementSystem ();
		isAuthenticated = ums.isAuthenticated (accessToken);
		if (!isAuthenticated) throw new NotAuthorizedException("Valid access token must be provided");
		username = ums.getUsername(accessToken);
	}
	
	public long createDocument (String note) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.CREATE_DOCUMENT");
		    Long id = con.createQuery(sql,true)
		    		.addParameter("username", username)
		    		.addParameter("note", note)
				    .executeUpdate()
				    .getKey(Long.class);

		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, id.toString(), LogEntry.ACTION.CREATE, "New document "+id.toString()+" created by user "+ username);   			
		    return id.longValue();
		}
	}
	
	public long createDocument (String fileName, String fileType, InputStream inputStream) throws Exception {
		long idDocument = 0;
		
		if (fileType.equalsIgnoreCase("message/rfc822")) {
			MimeMessage message = new MimeMessage (null, inputStream);	
			byte ptext[] = fileName.getBytes(ISO_8859_1); 
			fileName = new String(ptext, UTF_8);  
			idDocument = createDocumentFromMessage (fileName, message);
		}
		else {
			idDocument = createDocument (fileName);
			attachFile (idDocument, fileName, fileType, inputStream);			
		}

	    return idDocument;	    		
	}	

	public long createDocumentFromMessage (String fileName, Message message) throws Exception {
	
	  long idDocument = createDocument(fileName);
   	  
	  // convert and attach pdf
      org.w3c.dom.Document document = MimeMessageConverter.convertToXHTML(message);
      ByteArrayOutputStream out_pdf = new ByteArrayOutputStream ();
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocument(document, null);
      renderer.layout();
      renderer.createPDF(out_pdf) ;
      byte[] data = out_pdf.toByteArray();
      out_pdf.close();      
      ByteArrayInputStream in_pdf = new ByteArrayInputStream(data);
	  attachFile(idDocument, "Preview.pdf", "application/pdf", in_pdf);    
   	  
   	  // attach eml
   	  ByteArrayOutputStream out_eml = new ByteArrayOutputStream();	    	
   	  try {
   		message.writeTo(out_eml);
   	  }
   	  finally {
   		  if (out_eml != null) { out_eml.flush(); out_eml.close(); }
   	  }	    	  
   	  data = out_eml.toByteArray();  
	  ByteArrayInputStream in_eml = new ByteArrayInputStream(data);
   	  attachFile(idDocument, fileName, "message/rfc822", in_eml);
   	        	   	  
   	  // save attachments
	  List<Part> attachmentParts = MimeMessageParser.getAttachments(message);
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
			attachFile(idDocument, attachmentFilename, attachmentFileType, part.getInputStream());
		}
	    return idDocument;	    		
	}	
	
	public void updateDocument (int idDocument, String note) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.UPDATE_DOCUMENT");
		    con.createQuery(sql,true)
    				.addParameter("username", username)		    
		    		.addParameter("idDocument", idDocument)
		    		.addParameter("note", note)
				    .executeUpdate();
		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, idDocument+"", LogEntry.ACTION.UPDATE, "Document "+idDocument+" updated by user "+ username);   		
		}
	}	
	
	public long attachFile (long idDocument, String fileName, String fileType, InputStream inputStream) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.ATTACH_FILE");
		    long idFile = con.createQuery(sql,true)
		    		.addParameter("inputstream", inputStream)
				    .executeUpdate()
				    .getKey(Long.class);
		    
			sql = SqlStatements.get ("DMS.ATTACH_FILE_PROPS");
		    con.createQuery(sql)
    				.addParameter("username", username)
    				.addParameter("idFile", idFile)
		    		.addParameter("idDocument", idDocument)
		    		.addParameter("fileName", fileName)
		    		.addParameter("fileType", fileType)
				    .executeUpdate();

		    updateThumbnail (idDocument, idFile);
		    
		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, idDocument+"", LogEntry.ACTION.UPDATE, "File attached to Document "+idDocument+" by user "+ username);   		
		    return idFile;
		    }		
	}	
	
	public void updateThumbnail (long idDocument, long idFile) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			try {
				// 1. Read file from DB 
				String sql = SqlStatements.get ("DMS.GET_FILE");
				com.rembli.dms.File file = con.createQuery(sql)
						.addParameter("username", username)
						.addParameter("idFile", idFile)
						.executeAndFetchFirst (com.rembli.dms.File.class);
				
				// 2. Convert file to thumbnail, wenn dieses schon verfügbar ist
				if (file != null && file.data != null && 
					(	file.getFileType().startsWith("image") ||
						file.getFileType().equalsIgnoreCase("application/pdf")	
					)) 
				{

					ByteArrayInputStream bisT = null;
					
					// Thumbnail for images
					if (file.getFileType().startsWith("image")) {
						ByteArrayInputStream bis = new ByteArrayInputStream(file.data);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						
						Thumbnails.of(bis)
				    	.size(200, 100)
				    	.outputFormat("png")
				    	.toOutputStream (bos);

						bisT = new ByteArrayInputStream(bos.toByteArray());
					}

					// Thumbnail for PDF
					else if (file.getFileType().equalsIgnoreCase("application/pdf")) {
						ConvertPagesToImages converter = new ConvertPagesToImages(file.data);
						converter.setPageScaling(0.25f);
						if (converter.openPDFFile()) {
							BufferedImage bufImage = converter.getPageAsImage(1, true);

						    java.io.File tmpImage = java.io.File.createTempFile ("tmp", ".png", new java.io.File(System.getProperty("java.io.tmpdir")));
						    tmpImage.deleteOnExit();
					        ImageIO.write(bufImage, "png", tmpImage);
					        bisT = new ByteArrayInputStream(FileUtils.readFileToByteArray(tmpImage));
						}
						converter.closePDFfile();
					}
					
					// 3. Write thumbnail to DB
		
					sql = SqlStatements.get ("DMS.ATTACH_THUMBNAIL");
				    con.createQuery(sql,true)
		    			.addParameter("username", username)
		    			.addParameter("idFile", idFile)
			    		.addParameter("inputstream", bisT)
					    .executeUpdate();
				}				
			} catch (net.coobird.thumbnailator.tasks.UnsupportedFormatException ignored) {
				// System.out.println ("Kann kein Thumbnail für dieses Format erzeugen");
			} 
		}			
	}
	
	public Document[] getDocuments () throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_DOCUMENTS");
			List<Document> documentList = con.createQuery(sql)
					.addParameter("username", username)
					.executeAndFetch(Document.class);
			int size = documentList.size();
			return documentList.toArray(new Document[size]);
		}
	}

	public Document getDocument (int idDocument) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_DOCUMENT");
			return con.createQuery(sql)
					.addParameter("username", username)
					.addParameter("idDocument", idDocument)
					.executeAndFetchFirst(Document.class);
		}
	}	
	
	public FileInfo[] getFileInfos (long idDocument) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_FILES");
			List<FileInfo> fileInfos = con.createQuery(sql)
					.addParameter("username", username)
					.addParameter("idDocument", idDocument)
					.executeAndFetch(FileInfo.class);
			int size = fileInfos.size();
			return fileInfos.toArray(new FileInfo[size]);	
		}
	}
	
	public com.rembli.dms.File getFile (long idFile) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_FILE");
			com.rembli.dms.File file = con.createQuery(sql)
					.addParameter("username", username)
					.addParameter("idFile", idFile)
					.executeAndFetchFirst (com.rembli.dms.File.class);
	
			return file;
		}
	}	
	
	public byte[] getThumbnail (long idFile) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_THUMBNAIL");
			return con.createQuery(sql)
					.addParameter("username", username)
					.addParameter("idFile", idFile)
					.executeAndFetchFirst (byte[].class);
		}
	}	
	
	public byte[] getThumbnailForDocument (long idDocument) throws Exception {
		FileInfo[] fi = this.getFileInfos(idDocument);
		if (fi.length>0) {
			long idFile = fi[0].idFile;
			return this.getThumbnail(idFile);
		}
		return null;
	}		
	
	public void deleteDocument (long idDocument) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.DELETE_DOCUMENT");
		    con.createQuery(sql,true)
    				.addParameter("username", username)		    
		    		.addParameter("idDocument", idDocument)
				    .executeUpdate();

		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, idDocument+"", LogEntry.ACTION.DELETE, "Document "+idDocument+" deleted by user "+ username);		    
		}
	}	
	
	public void deleteFile (long idDocument, int idFile) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.DELETE_FILE");
		    con.createQuery(sql,true)
    				.addParameter("username", username)		    
		    		.addParameter("idDocument", idDocument)
		    		.addParameter("idFile", idFile)		
				    .executeUpdate();

		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, idDocument+"", LogEntry.ACTION.DELETE, "File "+idFile+" deleted from Document "+idDocument+"  by user "+ username);		    
		}
	}	
}
