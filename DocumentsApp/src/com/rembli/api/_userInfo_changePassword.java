package com.rembli.api;

import com.rembli.api.security.*;
import com.rembli.ums.*;
import io.swagger.annotations.*;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;


@Api(value = "UM - USER MANAGEMENT")
@Secured
@Path("/userInfo/changePassword")
public class _userInfo_changePassword {
	@Context HttpServletRequest httpRequest;	

    // ########################################################################	
	@ApiOperation(value = "Passwort ändern", notes = "Ändern des Passworts für den aktiven Benutzer")		
	@ApiResponses(value = { 
			  @ApiResponse(code = 204, message = "Nach erfolgreicher Änderung des Passworts kommt keine Antwort.")
	})		
	@POST
	@Consumes (MediaType.APPLICATION_FORM_URLENCODED)
	@Produces (MediaType.TEXT_HTML)  
	public Response changePassword (@FormParam("password") String password)	throws Exception {
		
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		UserManagementSystem ums = new UserManagementSystem ();
		ums.changePassword (accessToken, password);

		return Response.status(204).entity("").build();	
	} 		
}
