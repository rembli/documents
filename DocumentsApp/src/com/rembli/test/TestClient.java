package com.rembli.test;
import java.util.Random;
import com.rembli.dms.*;
import com.rembli.ums.*;

public class TestClient {

	public static void main(String[] args) throws Exception {
		UserManagementSystem ums = null;
		DocumentManagementSystem dms = null;
		
		System.out.println ("\n#0: User anlegen");		

		ums = new UserManagementSystem ();
		Random r1 = new Random ();
		String username = "Test-"+r1.nextInt(1000);	
		String email = username+"@rembli.com";
		String password ="123";
		ums.createUserInfo(username, email, password);		

		System.out.println ("\n#1: Anmelden mit falschem Access token");
		try { 
			dms = new DocumentManagementSystem("das hier geht nicht");
		}
		catch (Exception e) {
			System.out.println ("--> EXCEPTION: "+e.getMessage());
		}
		
		System.out.println ("\n#2: Zugriff ohne Anmeldung");		
		try { 
			dms.createDocument("jetzt krachts");
		}
		catch (Exception e) {
			System.out.println ("--> EXCEPTION: "+e.getMessage());
		}
		
		System.out.println ("\n#3: Anmelden mit falschem username/password");		
		try { 
			String accessTokenSignature = ums.login("testClient", "123");
		}
		catch (Exception e) {
			System.out.println ("--> EXCEPTION: "+e.getMessage());
		}		
		
		System.out.println ("\n#4: Anmelden mit richtigem username/password");				
		try { 
			String accessTokenSignature = ums.login(username, password);
		
			System.out.println ("Access Token: "+accessTokenSignature);
			UserInfo ui = ums.getUserInfo(accessTokenSignature);
			System.out.println ("UserInfo: "+ui);
			System.out.println ("ExpDate1: "+ums.getAccessTokenExipration(accessTokenSignature));
			System.out.println ("3 Sekunden warten und dann die Laufzeit des Tokens verlängern");
		    
			Thread.sleep(3000);  
			
			ums.refreshAccessToken(accessTokenSignature);
			System.out.println ("ExpDate2: "+ums.getAccessTokenExipration(accessTokenSignature));
			
			dms = new DocumentManagementSystem(accessTokenSignature);
			Random r2 = new Random ();
			String note = "note-"+r2.nextInt(1000);
			long id = dms.createDocument(note);
			System.out.println ("new document #"+id);
			
			for (int cnt=0;cnt<1; cnt++) {
				Document documents[] = dms.getDocuments();
				for (int cnt2=0;cnt2<documents.length; cnt2++)
				System.out.println (documents[cnt2].getNote());
			}
		}
		catch (Exception ignored) {
			ignored.printStackTrace();
		}
		System.out.println ("--> DONE!");
	}
}
