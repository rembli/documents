
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

@Path("/loginWithAccessToken")
public class _loginWithAccessToken {
	@Context HttpServletRequest httpRequest;

    @ApiOperation(value = "Anmeldung", notes = "Erzeugt ein Authentication-Token auf Basis eines von einem fremden IdentityProvider erzeugten AccessTokens. Aktuell ist nur FACEBOOK m�glich.")
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message = "Es wird das Authentication-Token als Text zur�ckgegeben.", response = String.class),
    		@ApiResponse(code = 401, message = "Das Login war nicht erfolgreich.")
      })	    
    @POST
    @Produces("text/html")
    @Consumes("application/x-www-form-urlencoded")
    public Response login (@FormParam("identityProvider") String identityProvider, @FormParam("accessToken") String thirdPartyAccessToken) throws Exception {

    	UserManagementSystem ums = new UserManagementSystem ();
    	String accessToken = ums.loginWithAccessToken(identityProvider, thirdPartyAccessToken);
    	if (accessToken != null) {
    		HttpSession session = httpRequest.getSession();
    		session.setAttribute("accessToken", accessToken);
    		return Response.ok(accessToken).build();
    	}
    	else
    		return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}	
