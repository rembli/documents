package com.rembli.test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import com.owlike.genson.Genson;
import java.util.Map;

public class TestLoginFacebook {

	public static void main(String[] args) {
		  // GET graph.facebook.com/debug_token?input_token={token-to-inspect}&access_token={app-token-or-admin-token}
		
    	  Client client = ClientBuilder.newClient();
    	  WebTarget webTarget;
    	  Response response;
    	  
    	  String access_token = "CAAXP8ro1wSgBAJ6r1v2JZB9cFIrBRYOBzdiyw60vHnZBrhhZAwAdQmA3fL2YJyYVPjKuBwnw2ZBFZARXHljZAMAZCg4YUvpmOmR4px1lBhphGcZBgiVobiTIP6cY7bPzZCHYPSjqvKm7EIXsZAxX0IZAfUyDkMx1ZAY8J3ZBEkorq0saPVVMTbSgO3T5UsZCsOuhqoSTjrCjGDZBegJ3UbdTF9kAqVk";
    	  
    	  webTarget = client.target("https://graph.facebook.com/debug_token?input_token="+access_token+"&access_token="+access_token);
    	  response = webTarget.request().get();    	  
    	  System.out.println (response.readEntity(String.class));

    	  webTarget = client.target("https://graph.facebook.com/me?access_token="+access_token);
    	  response = webTarget.request().get();  
    	  String userJSON = response.readEntity(String.class);
    	  
    	  Genson genson = new Genson();
    	  Map<Integer, String> user = genson.deserialize(userJSON, Map.class);
    	  System.out.println (user.get("id"));
    	  System.out.println (user.get("name"));
    	  
    	  /*
    	  // BEISPIEL FÜR GET
    	  
    	  webTarget = client.target("http://www.google.de?q=test");
    	  response = webTarget.request().get();    	
    	   
    	  // BEISPIEL FÜR POST    	   
    	   
    	  webTarget = client.target("http://graph.facebook.com/debug_token");
    	  MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    	  formData.add("input_token", access_token);
    	  formData.add("access_token", access_token);
    	  response = webTarget.request().post(Entity.form(formData));
    	  
    	  */
    	  

      } 
}