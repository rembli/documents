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

	public static class IDENTIY_PROVIDER {
		public static String FACEBOOK = "FACEBOOK";
	}	
	
	// die Tokens werden Anwendungsübergreifend in einer Hashmap abgespeichert, d.h. pro VM bzw. App-Server
	// Alternative: in der Datenbank
	private static HashMap<String,AccessToken> accessTokens = new HashMap<String,AccessToken>();
    
    public String login (String username, String password) throws Exception {
    	try (Connection con = ConnectionPool.getConnection()) {
			
    		String sql = SqlStatements.get ("UMS.AUTHENTICATE");
    		Table t = con.createQuery(sql)
				.addParameter("username", username)
				.addParameter("password", password)
				.executeAndFetchTable();
    		
    		if (t.rows().size()>0) {
    			String token = issueAccessToken (username);
    			LogManagementSystem.log(username, LogEntry.ENTITY.USER, username, LogEntry.ACTION.CHECK, "Successful login for user "+ username);   			
    			return token;
    		}
    		else {
    			LogManagementSystem.log(username, LogEntry.ENTITY.USER, username, LogEntry.ACTION.CHECK, "Login failed for user "+ username);	
    			return null;
    		}
		}
    }

    public String loginWithAccessToken (String identityProvider, String thirdPartyAccessToken) throws Exception {    
    	System.out.println ("Trying to login with "+identityProvider);
    	
    	if (identityProvider.equalsIgnoreCase(IDENTIY_PROVIDER.FACEBOOK)) {
    		
	    	// 1. Bei Facebook das AccessToken prüfen
		
	    	Client client = ClientBuilder.newClient();
	    	WebTarget webTarget = client.target("https://graph.facebook.com/me?access_token="+thirdPartyAccessToken);
	    	Response response = webTarget.request().get();  
		  	String userJSON = response.readEntity(String.class);
		  	System.out.println("FACEBOOK: "+userJSON);
		  	
		  	Genson genson = new Genson();
		  	Map<String, Object> user = genson.deserialize(userJSON, Map.class);
		  	
		  	// 2. Prüfen, ob AccessToken noch gültig ist (d.h. Werte zurück liefert)
		  	if (user.get("id")!=null) {
		  		
			  	// 3. User ggf. anlegen (und prüfen, ob er nicht schon angelegt ist); wenn kein User angelegt wurde, liefert createUserInfo 0 zurück
			  	String username = user.get("name")+"-"+user.get("id"); 
			  	String email = ""; // leider steht die E-Mail Adresse bei FB im Standard nicht zur Verfügung
			  	Random random = new SecureRandom();
		        String password = new BigInteger(130, random).toString(10);	  	
			  	createUserInfo(username, email, password);
	
			  	// 4. Token für User erzeugen
			  	String accessToken = issueAccessToken (username);
			  	LogManagementSystem.log(username, LogEntry.ENTITY.USER, username, LogEntry.ACTION.CHECK, "Successful login for user "+ username +" with FACEBOOK");
			  	return accessToken;
		  	}
		  	else {
		  		LogManagementSystem.log("SYSTEM", LogEntry.ENTITY.USER, "", LogEntry.ACTION.CHECK, "Login with FACEBOOK failed");
		  		return null;
		  	}
    	}
    	else
    		return null;
    }
    
    private String issueAccessToken (String username) throws Exception {
    	// Es kann ein beliebiges Token erzeugt werden, z.B. auch ein JWT. Hier nur eine Zufallszahl
    	// Vorteil von JWT: Ein System, welches den öffentlichen Schlüssel hat, kann beim JWT
    	// verifizieren, von wem es erzeugt wurde und dann - wenn es dem Aussteller vertraut - ein SSO durchführen
    	// siehe auch JWS - JSON Web Signature
    	
    	Random random = new SecureRandom();
        String accessTtokenSignature = "Bearer " + new BigInteger(130, random).toString(32);
        int secondsToLive = new Integer(getProperties ("SEC.TOKEN_VALIDTY_IN_SECONDS")).intValue(); // Gültigkeit des Tokens in Sekunden
        AccessToken accessToken = new AccessToken (username, secondsToLive);
        accessTokens.put(accessTtokenSignature, accessToken);

		return accessTtokenSignature;
    }	
    
    public java.util.Date getAccessTokenExipration (String accessTokenSignature) throws Exception {
    	AccessToken accessToken = accessTokens.get(accessTokenSignature);
    	return accessToken.getExpiresOn();
    }    
        
    public void refreshAccessToken (String accessTokenSignature) {
		try {    	
	        int secondsToLive = new Integer(getProperties ("SEC.TOKEN_VALIDTY_IN_SECONDS")).intValue();    	
	    	AccessToken accessToken = accessTokens.get (accessTokenSignature);
			java.util.Date now = new java.util.Date ();
			now.setTime(now.getTime() + (secondsToLive * 1000));    
			accessToken.setExpiresOn(now);
	    	accessTokens.put(accessTokenSignature, accessToken);

		} catch (IOException ignored) {
			ignored.printStackTrace();
		}
    }
    
    public void revokeToken (String accessTokenSignature) {
    	accessTokens.remove(accessTokenSignature);
    }
    
    public boolean isAuthenticated (String accessTokenSignature) {
    	AccessToken accessToken = accessTokens.get(accessTokenSignature);
    	java.util.Date now = new java.util.Date();
    	if (accessToken.getExpiresOn().getTime()>now.getTime())
    		return true;
    	else 
    		return false;
    }    

    public String getUsername (String accessTokenSignature) {
    	AccessToken accessToken = accessTokens.get(accessTokenSignature);
    	return accessToken.getUsername();
    }
	
	public long createUserInfo (String username, String email, String password) throws Exception {
		System.out.println("Try to create new user: "+username);
		try (Connection con = ConnectionPool.getConnection()) {
			// Prüfen, ob es den username schon gibt
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
	
    public UserInfo getUserInfo (String accessTokenSignature) throws Exception {
    	AccessToken accessToken = accessTokens.get(accessTokenSignature);
    	System.out.println ("Username (Token): "+accessToken.getUsername());

		try (Connection con = ConnectionPool.getConnection()) {    	
	   		String sql = SqlStatements.get ("UMS.GET_USERINFO");
	   		return con.createQuery(sql)
	   				.addParameter("username", accessToken.getUsername())
	   				.executeAndFetch(UserInfo.class)
	   				.get(0);  
		}
    }	
	
	public void changePassword (String accessTokenSignature, String password) throws Exception {
    	AccessToken accessToken = accessTokens.get(accessTokenSignature);
    	if (accessToken==null) throw new Exception ("UNAUTHORIZED");

		try (Connection con = ConnectionPool.getConnection()) {
			String sql = SqlStatements.get ("UMS.UPDATE_PASSWORD");
		    con.createQuery(sql,true)
    				.addParameter("username", accessToken.getUsername())		    
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
