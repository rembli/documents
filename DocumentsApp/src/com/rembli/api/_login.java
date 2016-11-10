package com.rembli.api;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;


import com.rembli.ums.*;

@Api(value = "UM - USER MANAGEMENT")

@Path("/loginWithEMail")
public class _login {
	@Context HttpServletRequest httpRequest;

    // ########################################################################	
    @ApiOperation(value = "Anmeldung", notes = "Erzeugt ein Access-Token, welches f�r die weitere Bearbeitung notwendig ist.")
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message = "Es wird das Access-Token als Text zur�ckgegeben.", response = String.class),
    		@ApiResponse(code = 401, message = "Das Login war nicht erfolgreich.")
      })	    
    @POST
    @Produces("text/html")
    @Consumes("application/x-www-form-urlencoded")
    public Response login (@FormParam("email") String email, @FormParam("password") String password) throws Exception {

    	// mit Username und Passwort anmelden, d.h. ein Token erzeugen, dass wir an den Browser zur�ck geben
    	// und zus�tzlich noch in der Session abspeichern, falls von einem Browser auf die API zugegriffen werden soll
   		try {
   	    	UserManagementSystem ums = new UserManagementSystem ();
   			String accessToken = ums.loginWithEMail(email, password);
    		HttpSession session = httpRequest.getSession();
    		session.setAttribute("accessToken", accessToken);
    		return Response.ok(accessToken).build();
    	}
   		catch (Exception e) {
    			return Response.status(Response.Status.UNAUTHORIZED).build();
   		}
    }   
}		

	
	
