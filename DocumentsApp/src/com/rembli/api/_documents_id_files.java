package com.rembli.api;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.*;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.glassfish.jersey.media.multipart.*;

import com.rembli.api.resources.*;
import com.rembli.api.security.AuthenticationFilter;
import com.rembli.api.security.Secured;
import com.rembli.dms.*;
import com.rembli.ums.*;

@Api(value = "DOC - DOCUMENTS")
@Secured
@Path("/documents/{idDocument}/files")
public class _documents_id_files {
	@Context HttpServletRequest httpRequest;	

    // ########################################################################	
	@ApiOperation(value = "Hinzuf�gen einer Datei", notes = "Einem Dokument k�nnen mehrere Dateien hinzugef�gt werden. Die Datei darf eine bestimmte Gr��e nicht �berschreiten.")		
	@ApiResponses(value = { 
			  @ApiResponse(code = 200, message = "Liefert die ID der hochgeladenen Datei zur�ck als Text.", response = String.class),
	})	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response attachFile(
			@PathParam("idDocument") int idDocument, 
			@FormDataParam("RemoteFile") InputStream uploadedInputStream,
			@FormDataParam("RemoteFile") FormDataContentDisposition fileInfo,
			@FormDataParam("RemoteFile") FormDataBodyPart fileBody) 
		throws Exception {
		
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		DocumentManagementSystem dms = new DocumentManagementSystem (accessToken);
	
		String fileName = "New upload";
		String fileType = "application/octet-stream";
		if (fileInfo != null) fileName = fileInfo.getFileName();
		if (fileBody != null) fileType = fileBody.getMediaType().getType()+"/"+fileBody.getMediaType().getSubtype();
		long idfile = dms.attachFile(idDocument, fileName, fileType, uploadedInputStream);
		
		return Response.status(200).entity(""+idfile).build();		
	}
	
    // ########################################################################	
	@ApiOperation(value = "Anzeige aller Dateien", notes = "Anzeige aller einem bestimmte Dokument hinzugef�gten Dateien.")	
	@ApiResponses(value = { 
			  @ApiResponse(code = 200, message = "Liefert eine Liste an FileInfos zur�ck.", response = FileInfoRessource[].class),
	})	
	@GET
	@Produces ({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public FileInfoRessource[] getFileInfos (@PathParam("idDocument") int idDocument) throws Exception {
		
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		DocumentManagementSystem dms = new DocumentManagementSystem (accessToken);
		FileInfo[] fileInfos = dms.getFileInfos(idDocument);

		// Typumwandlung, damit _self zur Verf�gung steht
		FileInfoRessource[] fileInfoRessources = new FileInfoRessource[fileInfos.length];
		for (int i=0; i<fileInfos.length; i++) fileInfoRessources[i] = new FileInfoRessource(fileInfos[i]);
		return fileInfoRessources;
	  }  	
}
