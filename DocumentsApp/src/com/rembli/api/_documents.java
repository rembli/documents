package com.rembli.api;

import java.io.InputStream;
import io.swagger.annotations.*;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.glassfish.jersey.media.multipart.*;

import com.rembli.api.resources.DocumentRessource;
import com.rembli.api.security.AuthenticationFilter;
import com.rembli.api.security.Secured;
import com.rembli.dms.*;
import com.rembli.ums.*;

@Api(value = "DOC - DOCUMENTS")
@Secured
@Path("/documents")
public class _documents {
	@Context HttpServletRequest httpRequest;	
	
	@ApiOperation(value = "Hinzufügen einer Datei", notes = "Ein neues Dokument per Upload anlegen")	
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "Es wird die ID des neu angelegten Dokuments zurück gegeben.", response = String.class)
	})		
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createDocument (
			@FormDataParam("RemoteFile") InputStream fileInputStream,
			@FormDataParam("RemoteFile") FormDataContentDisposition fileInfo,
			@FormDataParam("RemoteFile") FormDataBodyPart fileBody) 
		throws Exception {

		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		DocumentManagementSystem dms = new DocumentManagementSystem (accessToken);

		String fileName = "New upload";
		String fileType = "application/octet-stream";
		if (fileInfo != null) fileName = fileInfo.getFileName();
		if (fileBody != null) fileType = fileBody.getMediaType().getType()+"/"+fileBody.getMediaType().getSubtype();
		long iddocument = dms.createDocument(fileName, fileType , fileInputStream);

		return Response.status(200).entity(""+iddocument).build();	
	} 	
	
    @ApiOperation(value = "Liste der Dokumente", notes = "Zeigt eine Liste der Dokumente an")
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message = "Es wird die Liste der gefundenen Dokumente zurück gegeben.", response = DocumentRessource[].class)
      })	    
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 
	public DocumentRessource[] getDocuments () throws Exception {
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		DocumentManagementSystem dms = new DocumentManagementSystem (accessToken);
		Document[] documents = dms.getDocuments();
		DocumentRessource[] documentRessources = new DocumentRessource[documents.length];
		for (int i=0; i<documents.length;i++) documentRessources[i] = new DocumentRessource(documents[i]);
		return documentRessources;
	}
  

    
} 
