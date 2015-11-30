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
    	  
    	  String access_token = "CAAXP8ro1wSgBAI2IiepMBSZAKYrqLjdkES5ZB6ZBNYk0TfFffhUksAvWcaZBnQdsZAVnpsGZCX3VjhhfWYrMCb5bmYEnZBZC49psBHcT95elBZCH90ixLcY491EjZA8pVMDmQouA9hcoETt3uJZCaZBudkXZBiDVfSzaZBNzd5XuyKHgIcER8S0OIS7ida93M89N4E0McWnfCexKHQuKVB9QlZCsgWs";
    	  
    	  webTarget = client.target("https://graph.facebook.com/debug_token?input_token="+access_token+"&access_token="+access_token);
    	  response = webTarget.request().get();    	  
    	  System.out.println (response.readEntity(String.class));

    	  webTarget = client.target("https://graph.facebook.com/me?access_token="+access_token);
    	  response = webTarget.request().get();  
    	  String userJSON = response.readEntity(String.class);
    	  
    	  Genson genson = new Genson();
    	  Map<String, Object> user = genson.deserialize(userJSON, Map.class);
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