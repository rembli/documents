package com.rembli.api;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;

import com.rembli.api.security.AuthenticationFilter;
import com.rembli.api.security.Secured;
import com.rembli.dms.*;
import com.rembli.ums.*;

@Api(value = "DOC - DOCUMENTS")
@Secured
@Path("/documents/{idDocument}/thumbnail")
public class _documents_id_thumbnail {
	@Context HttpServletRequest httpRequest;	

	@ApiOperation(value = "Thumbnail", notes = "Anzeige der Thumbnail.")	
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "Liefert die Thumbnail binär zurück."),
		@ApiResponse(code = 204, message = "Es wird nichts zurückgegeben, wenn kein Thumbnail vorliegt")		
	})	
	@GET
	public Response getThumbnail (@PathParam("idDocument") int idDocument) throws Exception {
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		DocumentManagementSystem dms = new DocumentManagementSystem (accessToken);
	    
		byte[] thumbnail = dms.getThumbnailForDocument (idDocument);
		if (thumbnail != null) {
			ResponseBuilder response = Response.ok(thumbnail);
			response.type("image/png");
			response.header("Content-Disposition", "inline;filename=thumb.png");
			return response.build();
		}
		else
			return Response.status(204).build();
	  }  	
}