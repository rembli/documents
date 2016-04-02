package com.rembli.api;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;

import com.rembli.api.security.AuthenticationFilter;
import com.rembli.api.security.Secured;
import com.rembli.ums.*;

@Api(value = "UM - USER MANAGEMENT")
@Secured
@Path("/logout")
public class _logout {
	@Context HttpServletRequest httpRequest;	
	@Context ContainerRequestContext requestContext;
	  
    // ########################################################################	
    @ApiOperation(value = "Abmeldung", notes = "Setzt das Authentication-Token auf invalide, so dass dar�ber kein Zugriff mehr erfolgen kann")
    @ApiResponses(value = { 
    		@ApiResponse(code = 204, message = "Es wird nichts zur�ckgegeben.")
      })  
    @POST
    public Response logout () {
    	// hier wird das Token zur�ck gesetz
    	// ohne Token kann der Anwender nicht mehr zugreifen

    	// dazu holen wir uns das token aus dem Request ...
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
    	
    	// .. setzen es zur�ck ...
    	UserManagementSystem ums = new UserManagementSystem ();
    	ums.revokeAccessToken (accessToken);
    	
    	// und melden OK
    	return Response.status(204).entity("").build();
    }
}		
	
