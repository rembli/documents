package com.rembli.log;
import com.rembli.ums.UserManagementSystem;
import com.rembli.util.db.*;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;

import org.sql2o.Connection;
import org.sql2o.data.Table;

public class LogManagementSystem {

	// ### PUBLIC AVAILABLE METHODS FOR WRITING LOGS #####

    public static void log (String username, String entity, String entityid, String action, String comment) throws Exception {
    	
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("AUDIT.LOG");
		    con.createQuery(sql, true)
		    		.addParameter("username", username)
		    		.addParameter("entity", entity)
		    		.addParameter("entityid", entityid)
		    		.addParameter("action", action)
		    		.addParameter("comment", comment)
				    .executeUpdate();
		}
    }
    
	// ### METHODS TO READ LOGS ONLY AVAILABLE AFTER AUTHENTICATION #####    
	
    private boolean isAuthenticated = false;
	private String username = null;	
    
	public LogManagementSystem (String accessToken) throws Exception {
		UserManagementSystem ums = new UserManagementSystem ();
		isAuthenticated = ums.isAuthenticated (accessToken);
		if (!isAuthenticated) throw new NotAuthorizedException ("Access token not valid");
		username = ums.getUsername(accessToken);		
	}        
    
	public LogEntry[] getAllonge (String entity, String entityid) throws Exception {
		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("AUDIT.CHECK_AUTHORIZATION_"+LogEntry.ENTITY.DOCUMENT);
	    	Table t = con.createQuery(sql)
					.addParameter("username", username)
					.addParameter("id", entityid)
					.executeAndFetchTable();
	    	if (t.rows().size() == 0) throw new NotAuthorizedException ("You are not authorized to view this audit trail.");		
			
			sql = SqlStatements.get ("AUDIT.ALLONGE");
			List<LogEntry> allonge = con.createQuery(sql)
					.addParameter("entity", entity)
					.addParameter("entityid", entityid)
					.executeAndFetch(LogEntry.class);
			int size = allonge.size();
			return allonge.toArray(new LogEntry[size]);
		}
	}    
}
