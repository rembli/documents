// URL = http://localhost:8080/8-SimpleREST_WebApp/api/documents/{id}
package com.rembli.api;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.rembli.api.resources.DocumentRessource;
import com.rembli.dms.*;
import com.rembli.ums.*;

@Api(value = "DOC - DOCUMENTS")
@Secured
@Path("/documents/{idDocument}")
public class _documents_id {
  @Context HttpServletRequest httpRequest;	

  @ApiOperation(value = "Lesen eines Dokuments", notes = "Lesen eines bestimmten Dokuments, welches durch die 'iddocument' angegeben wurde")
  @ApiResponses(value = { 
		  @ApiResponse(code = 200, message = "Liefert die Meta-Daten des Dokuments zurück.", response = DocumentRessource.class)
  })  
  @GET
  @Produces ({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public DocumentRessource getDocument (@PathParam("idDocument") int idDocument) throws Exception {
	String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
	DocumentManagementSystem dms = new DocumentManagementSystem (accessToken);
	Document document =  dms.getDocument(idDocument);
	DocumentRessource documentRessource = new DocumentRessource (document);
	return documentRessource;
  }  
  
  @ApiOperation(value = "Ändern eines Dokuments", notes = "Ändern der Notiz eines Dokuments") 
  @ApiResponses(value = { 
		@ApiResponse(code = 204, message = "Es wird nichts zurückgegeben, wenn das Dokument geändert wurde.")
  })	
  @PUT
  @Consumes (MediaType.APPLICATION_FORM_URLENCODED)
  @Produces (MediaType.TEXT_HTML)  
  public Response updateDocument (@PathParam("idDocument") int idDocument, @FormParam("note") String note) throws Exception {
	String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
	DocumentManagementSystem dms = new DocumentManagementSystem (accessToken);
	dms.updateDocument(idDocument, note);
	return Response.status(204).entity("OK").build();
  }    

  @ApiOperation(value = "Löschen eines Dokuments", notes = "Löschen des mit 'iddocument' spezifierten Dokuments")  
  @ApiResponses(value = { 
		@ApiResponse(code = 204, message = "Es wird nichts zurückgegeben, wenn das Dokument gelöscht wurde.")
  })	
  @DELETE
  public Response deleteDocument (@PathParam("idDocument") int idDocument) throws Exception {
	String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
	DocumentManagementSystem dms = new DocumentManagementSystem (accessToken);
	dms.deleteDocument(idDocument);
	return Response.status(204).entity("OK").build();
  }
} 
