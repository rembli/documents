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
@Path("/mailbox")
public class _mailbox {
	@Context HttpServletRequest httpRequest;	
	
	// ########################################################################	
	@ApiOperation(value = "Mails importieren", notes = "Auslesen des Mail-Postfachs des Users und Mails als Belege importieren")	
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "Es wird die Liste der importierten Mails ausgegeben", response = MailMessage[].class)
	})		
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 	
	public ArrayList<MailMessage> importMails () throws Exception {
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		MailManagementSystem mms = new MailManagementSystem (accessToken);
		ArrayList<MailMessage> messages = mms.getMails();
		return messages;
	} 	
} 
