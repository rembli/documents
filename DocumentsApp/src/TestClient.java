import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import com.rembli.dms.*;
import com.rembli.ums.UserInfo;
import com.rembli.ums.UserManagementSystem;

public class TestClient {

	public static void main(String[] args) throws Exception {
		System.out.println ("Let the games begin ...");

		UserManagementSystem ums;
		DocumentManagementSystem dms;
		
		System.out.println ("USER ANLEGEN -------------- ");		

		ums = new UserManagementSystem ();
		Random r1 = new Random ();
		String username = "Test-"+r1.nextInt(1000);	
		String email = username+"@rembli.com";
		String password ="123";
		ums.createUserInfo(username, email, password);		

		System.out.println ("ERSTER VERSUCH -------------- ");
		try { // Ohne Authentifizierung 2
			dms = new DocumentManagementSystem("das hier geht nicht");
			dms.createDocument("jetzt krachts");
		}
		catch (Exception e) {
			System.out.println ("Es hat gekracht: "+e.getMessage());
		}
		
		System.out.println ("ZWEITER VERSUCH -------------- ");		
		try { // Mit Authentifizierung
			
			String tokenSignature = ums.login("gboegerl", "123");
			if (tokenSignature==null) {
				System.out.println ("FATAL: Authorization failed!");
				return;
			}
			System.out.println ("Access Token: "+tokenSignature);
			UserInfo ui = ums.getUserInfo(tokenSignature);
			System.out.println ("UserInfo: "+ui);
			System.out.println ("ExpDate1: "+ums.getTokenExipration(tokenSignature));
			System.out.println ("3 Sekunden warten und dann die Laufzeit des Tokens verlängern");
		    Thread.sleep(3000);  
			ums.refreshToken(tokenSignature);
			System.out.println ("ExpDate2: "+ums.getTokenExipration(tokenSignature));
			
			// Jetzt hat es geklappt und wir können mit dem Token was machen
			dms = new DocumentManagementSystem(tokenSignature);

			Random r2 = new Random ();
			String note = "note-"+r2.nextInt(1000);
			int id = dms.createDocument(note);
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
		System.out.println ("... done!");
	}
}
