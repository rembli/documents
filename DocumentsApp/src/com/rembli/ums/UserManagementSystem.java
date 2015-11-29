package com.rembli.ums;
import java.util.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.sql2o.Connection;
import org.sql2o.data.Table;

import com.owlike.genson.Genson;
import com.rembli.log.*;
import com.rembli.util.db.*;

public class UserManagementSystem {
	// die Tokens werden Anwendungs�bergreifend in einer Hashmap abgespeichert, d.h. pro VM bzw. App-Server
	// Alternative: in der Datenbank
	private static HashMap<String,Token> tokens = new HashMap<String,Token>();
    
    public String login (String username, String password) throws Exception {
    	try (Connection con = ConnectionPool.getConnection()) {
			
    		String sql = SqlStatements.get ("UMS.AUTHENTICATE");
    		Table t = con.createQuery(sql)
				.addParameter("username", username)
				.addParameter("password", password)
				.executeAndFetchTable();
    		
    		if (t.rows().size()>0) {
    			String token = issueToken (username);
    			LogManagementSystem.log(username, LogEntry.ENTITY.USER, username, LogEntry.ACTION.CHECK, "Successful login for user "+ username);   			
    			return token;
    		}
    		else {
    			LogManagementSystem.log(username, LogEntry.ENTITY.USER, username, LogEntry.ACTION.CHECK, "Login failed for user "+ username);			
    			System.out.println ("Login failed");
    			return null;
    		}
		}
    }

    public String login (String ssoTicket) throws Exception {    
	
    	// 1. Bei Facebook das AccessToken pr�fen
	
    	Client client = ClientBuilder.newClient();
    	WebTarget webTarget = client.target("https://graph.facebook.com/me?access_token="+ssoTicket);
    	Response response = webTarget.request().get();  
	  	String userJSON = response.readEntity(String.class);
	  
	  	Genson genson = new Genson();
	  	Map<Integer, String> user = genson.deserialize(userJSON, Map.class);
	  	
	  	// 2. User ggf. anlegen (und pr�fen, ob er nicht schon angelegt ist)
	  	String username = user.get("name") + "("+user.get("id")+")";
    	
	  	Random random = new SecureRandom();
        String password = new BigInteger(130, random).toString(32);	  	
	  	long userId = createUserInfo(username, username, password);
	  	if (userId==0) {
	  		System.out.println("User war schon angelegt");
	  	}
	  	
	  	return (issueToken(username));
    }
    
    private String issueToken (String username) throws Exception {
    	// Es kann ein beliebiges Token erzeugt werden, z.B. auch ein JWT. Hier nur eine Zufallszahl
    	// Vorteil von JWT: Ein System, welches den �ffentlichen Schl�ssel hat, kann beim JWT
    	// verifizieren, von wem es erzeugt wurde und dann - wenn es dem Aussteller vertraut - ein SSO durchf�hren
    	
    	Random random = new SecureRandom();
        String tokenSignature = "Bearer " + new BigInteger(130, random).toString(32);
        int secondsToLive = new Integer(getProperties ("SEC.TOKEN_VALIDTY_IN_SECONDS")).intValue(); // G�ltigkeit des Tokens in Sekunden
        Token token = new Token (username, secondsToLive);
        tokens.put(tokenSignature, token);

		return tokenSignature;
    }	
    
    public java.util.Date getTokenExipration (String tokenSignature) throws Exception {
    	Token token = tokens.get(tokenSignature);
    	return token.getExpiresOn();
    }    
        
    public void refreshToken (String tokenSignature) {
		try {    	
	        int secondsToLive = new Integer(getProperties ("SEC.TOKEN_VALIDTY_IN_SECONDS")).intValue();    	
	    	Token token = tokens.get (tokenSignature);
			java.util.Date now = new java.util.Date ();
			now.setTime(now.getTime() + (secondsToLive * 1000));    
			token.setExpiresOn(now);
	    	tokens.put(tokenSignature, token);

		} catch (IOException ignored) {
			ignored.printStackTrace();
		}
    }
    
    public void revokeToken (String tokenSignature) {
		tokens.remove(tokenSignature);
    }
    
    public boolean isAuthenticated (String tokenSignature) {
    	Token token = tokens.get(tokenSignature);
    	java.util.Date now = new java.util.Date();
    	if (token.getExpiresOn().getTime()>now.getTime())
    		return true;
    	else 
    		return false;
    }    

    public String getUsername (String tokenSignature) {
    	Token token = tokens.get(tokenSignature);
    	return token.getUsername();
    }
    
	
	public long createUserInfo (String username, String email, String password) throws Exception {
		System.out.println("Try to create new user: "+username+"/"+email+"/"+password);
		try (Connection con = ConnectionPool.getConnection()) {
			// Pr�fen, ob es den username schon gibt
    		String sql = SqlStatements.get ("UMS.CHECK_USERINFO");
    		Table t = con.createQuery(sql)
				.addParameter("username", username)
				.executeAndFetchTable();
    		
    		if (t.rows().size()>0) return 0;			
			
    		// wenn nicht, dann entsprechend anlegen
			sql = SqlStatements.get ("UMS.CREATE_USERINFO");
		    Long id = con.createQuery(sql, true)
		    		.addParameter("username", username)
		    		.addParameter("email", email)
		    		.addParameter("password", password)		    		
				    .executeUpdate()
				    .getKey(Long.class);
		    LogManagementSystem.log(username, LogEntry.ENTITY.USER, username, LogEntry.ACTION.CREATE, "User "+ username+" created");
		    
		    return id.longValue();
		}
	}    
	
    public UserInfo getUserInfo (String tokenSignature) throws Exception {
    	Token token = tokens.get(tokenSignature);
    	System.out.println ("Username (Token): "+token.getUsername());

		try (Connection con = ConnectionPool.getConnection()) {    	
	   		String sql = SqlStatements.get ("UMS.GET_USERINFO");
	   		return con.createQuery(sql)
	   				.addParameter("username", token.getUsername())
	   				.executeAndFetch(UserInfo.class)
	   				.get(0);  
		}
    }	
	
	public void changePassword (String tokenSignature, String password) throws Exception {
    	Token token = tokens.get(tokenSignature);
    	if (token==null) throw new Exception ("UNAUTHORIZED");

		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("UMS.UPDATE_PASSWORD");
		    con.createQuery(sql,true)
    				.addParameter("username", token.getUsername())		    
		    		.addParameter("password", password)
				    .executeUpdate();
		}		
	}  	
    

	private String getProperties (String statement) throws IOException {
		Properties prop = new Properties();
	    prop.load(SqlStatements.class.getClassLoader().getResourceAsStream("sec.properties"));
	    return prop.getProperty(statement);
	}    
}
