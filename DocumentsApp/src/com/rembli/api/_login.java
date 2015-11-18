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

@Path("/login")
public class _login {
	@Context HttpServletRequest httpRequest;

    @ApiOperation(value = "Anmeldung", notes = "Erzeugt ein Authentication-Token, welches f�r die weitere Bearbeitung notwendig ist")
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message = "Es wird das Authentication-Token als Text zur�ckgegeben.", response = String.class),
    		@ApiResponse(code = 401, message = "Das Login war nicht erfolgreich.")
      })	    
    @POST
    @Produces("text/html")
    @Consumes("application/x-www-form-urlencoded")
    public Response login (@FormParam("username") String username, @FormParam("password") String password) throws Exception {

    	UserManagementSystem ums = new UserManagementSystem ();
    	// mit Username und Passwort anmelden, d.h. ein Token erzeugen, dass wir an den Browser zur�ck geben
    	// und zus�tzlich noch in der Session abspeichern, falls von einem Browser auf die API zugegriffen werden soll
    	String token = ums.login (username, password);

    	if (token!=null) {
    		HttpSession session = httpRequest.getSession();
    		session.setAttribute("AuthenticationToken", token);
    		return Response.ok(token).build();
    	}
    	else
        	return Response.status(Response.Status.UNAUTHORIZED).build();
    }

}		
	
