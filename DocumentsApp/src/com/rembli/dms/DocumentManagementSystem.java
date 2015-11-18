package com.rembli.dms;
import java.io.*;
import java.util.*;
import org.sql2o.Connection;
import com.rembli.log.*;
import com.rembli.ums.UserManagementSystem;
import com.rembli.util.db.*;

public class DocumentManagementSystem {
	private boolean isAuthenticated = false;
	private String username = null;

	public DocumentManagementSystem (String token) throws Exception {
		UserManagementSystem ums = new UserManagementSystem ();
		isAuthenticated = ums.isAuthenticated (token);
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		username = ums.getUsername(token);
	}
	
	public int createDocument (String note) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.CREATE_DOCUMENT");
		    Integer id = con.createQuery(sql,true)
		    		.addParameter("username", username)
		    		.addParameter("note", note)
				    .executeUpdate()
				    .getKey(Integer.class);

		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, id.toString(), LogEntry.ACTION.CREATE, "New document "+id.toString()+" created by user "+ username);   			
		    return id.intValue();
		}
	}
	
	public int createDocument (String fileName, String fileType, InputStream uploadedInputStream) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		int iddocument = createDocument (fileName);
   		attachFile (iddocument, fileName, fileType, uploadedInputStream);
	    return iddocument;	    		
	}	
	
	public void updateDocument (int iddocument, String note) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");

		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.UPDATE_DOCUMENT");
		    con.createQuery(sql,true)
    				.addParameter("username", username)		    
		    		.addParameter("iddocument", iddocument)
		    		.addParameter("note", note)
				    .executeUpdate();
		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, iddocument+"", LogEntry.ACTION.UPDATE, "Document "+iddocument+" updated by user "+ username);   		
		}
	}	
	
	public int attachFile (int iddocument, String fileName, String fileType, InputStream uploadedInputStream) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		
		/* Alternative 1: Save to directory
		String fileLocation = "D:\\"+fileName;
		System.out.println ("Save file to: "+fileLocation);
        FileOutputStream out = new FileOutputStream(new File(fileLocation));  
        int read = 0; 
	    int size = 0;
	    byte[] bytes = new byte[1024];  
	    out = new FileOutputStream(new File(fileLocation));  
	    while ((read = uploadedInputStream.read(bytes)) != -1) {  
	        out.write(bytes, 0, read);
	        size += read; 
	    }  
	    out.flush();  
	    out.close();		
	    System.out.println ("File uploaded with size (kb): "+(size/1024));
		*/
	
		// Save to DB / besser erst den input stream verarbeiten und dann die anderen Daten
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.ATTACH_FILE");
		    Integer idfile = con.createQuery(sql,true)
		    		.addParameter("inputstream", uploadedInputStream)
				    .executeUpdate()
				    .getKey(Integer.class);
		    
			sql = SqlStatements.get ("DMS.ATTACH_FILE_PROPS");
		    con.createQuery(sql)
    				.addParameter("username", username)
    				.addParameter("idfile", idfile)
		    		.addParameter("iddocument", iddocument)
		    		.addParameter("fileName", fileName)
		    		.addParameter("fileType", fileType)
				    .executeUpdate();

		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, iddocument+"", LogEntry.ACTION.UPDATE, "File attached to Document "+iddocument+" by user "+ username);   		
		    
		    return idfile.intValue();
		    }		
	}	
	
	public Document[] getDocuments () throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_DOCUMENTS");
			List<Document> documentList = con.createQuery(sql)
					.addParameter("username", username)
					.executeAndFetch(Document.class);
			int size = documentList.size();
			return documentList.toArray(new Document[size]);
		}
	}

	public Document getDocument (int iddocument) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_DOCUMENT");
			return con.createQuery(sql)
					.addParameter("username", username)
					.addParameter("iddocument", iddocument)
					.executeAndFetchFirst(Document.class);
		}
	}	
	
	public FileInfo[] getFileInfos (int iddocument) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_FILES");
			List<FileInfo> fileInfos = con.createQuery(sql)
					.addParameter("username", username)
					.addParameter("iddocument", iddocument)
					.executeAndFetch(FileInfo.class);
			int size = fileInfos.size();
			return fileInfos.toArray(new FileInfo[size]);	
		}
	}
	
	public com.rembli.dms.File getFile (int idfile) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.GET_FILE");
			com.rembli.dms.File file = con.createQuery(sql)
					.addParameter("username", username)
					.addParameter("idfile", idfile)
					.executeAndFetchFirst (com.rembli.dms.File.class);
	
			return file;
		}
	}	
	
	public void deleteDocument (int iddocument) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.DELETE_DOCUMENT");
		    con.createQuery(sql,true)
    				.addParameter("username", username)		    
		    		.addParameter("iddocument", iddocument)
				    .executeUpdate();

		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, iddocument+"", LogEntry.ACTION.DELETE, "Document "+iddocument+" deleted by user "+ username);		    
		}
	}	
	
	public void deleteFile (int iddocument, int idfile) throws Exception {
		if (!isAuthenticated) throw new Exception ("UNAUTHORIZED");
		
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("DMS.DELETE_FILE");
		    con.createQuery(sql,true)
    				.addParameter("username", username)		    
		    		.addParameter("iddocument", iddocument)
		    		.addParameter("idfile", idfile)		
				    .executeUpdate();

		    LogManagementSystem.log(username, LogEntry.ENTITY.DOCUMENT, iddocument+"", LogEntry.ACTION.DELETE, "File "+idfile+" deleted from Document "+iddocument+"  by user "+ username);		    
		}
	}	
}
