package com.rembli.api;
import com.rembli.api.resources.*;
import com.rembli.api.security.AuthenticationFilter;
import com.rembli.api.security.Secured;

import io.swagger.annotations.*;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.rembli.ums.*;

@Api(value = "UM - USER MANAGEMENT")
@Path("/userInfo")
public class _userInfo {
	@Context HttpServletRequest httpRequest;	

	@Secured
	@ApiOperation(value = "Anwenderinformation", notes = "Anzeigen der Details zum aktuell angemeldeten Anwender.")	
	@ApiResponses(value = { 
			  @ApiResponse(code = 200, message = "UserInfo wird angezeigt.", response = UserInfoRessource.class)
	})	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 
	public UserInfoRessource getUserInfo () throws Exception {
		// Zuerst das Token aus dem Request holen, den der User nach der Anmeldung bekommen hat
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		UserManagementSystem ums = new UserManagementSystem ();
		// für den angemeldeten User sein Infos zurück geben
		UserInfo userInfo = ums.getUserInfo (accessToken);
		UserInfoRessource userInfoRessource = new UserInfoRessource (userInfo);
		return userInfoRessource;
	  }	
	
	@ApiOperation(value = "Anlegen eines Benutzerkontos", notes = "Anlegen eines Benutzerkontos")		
	@ApiResponses(value = { 
			  @ApiResponse(code = 200, message = "Benutzer erfogreich angelegt.", response = String.class),
		      @ApiResponse(code = 409, message = "Benutzer gibt es schon.", response = String.class) })
	@POST
	@Consumes (MediaType.APPLICATION_FORM_URLENCODED)
	@Produces (MediaType.TEXT_HTML)  
	public Response createUserInfo (
			@FormParam("username") String username,
			@FormParam("email") String email,
			@FormParam("password") String password)		 
		throws Exception {

		UserManagementSystem ums = new UserManagementSystem ();
		long iduser = ums.createUserInfo(username, email, password);
		
		if (iduser==0)
			return Response.status(409).entity("This user already exists").build();
		else
			return Response.status(200).entity(""+iduser).build();	
	} 		
	
	@Path("/changePassword")	
	@Secured
	@ApiOperation(value = "Passwort ändern", notes = "Ändern des Passworts für den aktiven Benutzer")		
	@ApiResponses(value = { 
			  @ApiResponse(code = 204, message = "Nach erfolgreicher Änderung des Passworts kommt keine Antwort.")
	})		
	@POST
	@Consumes (MediaType.APPLICATION_FORM_URLENCODED)
	@Produces (MediaType.TEXT_HTML)  
	public Response changePassword (
			@FormParam("password") String password)		 
		throws Exception {
		
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		UserManagementSystem ums = new UserManagementSystem ();
		ums.changePassword(accessToken, password);

		return Response.status(204).entity("").build();	
	} 		
	
	
}
