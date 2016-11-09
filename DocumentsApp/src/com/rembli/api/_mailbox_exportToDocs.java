package com.rembli.api;

import io.swagger.annotations.*;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.ArrayList;

import javax.mail.*;

import com.rembli.api.security.AuthenticationFilter;
import com.rembli.api.security.Secured;
import com.rembli.mail.MailManagementSystem;
import com.rembli.mail.MailMessage;

@Api(value = "MAIL - MAILBOX")
@Secured
@Path("/mailbox/exportToDocs")
public class _mailbox_exportToDocs {
	@Context HttpServletRequest httpRequest;	
	
	// ########################################################################	
	@ApiOperation(value = "Mails importieren", notes = "Auslesen des Mail-Postfachs des Users und Mails als Belege importieren")	
	@ApiResponses(value = { 
		@ApiResponse(code = 204, message = "Import der Mails war erfolgreich")
	})		
	@POST
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 	
	public Response importMails () throws Exception {
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		MailManagementSystem mms = new MailManagementSystem (accessToken);
		mms.exportToDocs();
		return Response.status(204).build();
	} 	
} 
