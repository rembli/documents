package com.rembli.api;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;

import com.rembli.dms.*;
import com.rembli.ums.*;

@Api(value = "DOC - DOCUMENTS")
@Secured
@Path("/documents/{idDocument}/files/{idFile}")
public class _documents_id_files_id {
	@Context HttpServletRequest httpRequest;	

	@ApiOperation(value = "Download einer Datei", notes = "Herunterladen einer bestimmten Datei.")	
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "Liefert die Datei bin�r zur�ck.")
	})	
	@GET
	public Response getFile (@PathParam("idDocument") int idDocument, @PathParam("idFile") int idFile) throws Exception {
		String token = AuthenticationFilter.getTokenFromRequest (httpRequest);
		DocumentManagementSystem dms = new DocumentManagementSystem (token);
	    
		com.rembli.dms.File file = dms.getFile(idFile);
		
        ResponseBuilder response = Response.ok(file.getData());
        response.type(file.getFileType());
        //response.header("Content-Disposition", "attachment; filename=\""+file.getFileName()+"\"");
        response.header("Content-Disposition", "inline;filename=\""+file.getFileName()+"\"");
        return response.build();		
	  }  	
	
	@ApiOperation(value = "L�schen einer Datei", notes = "L�schen des mit 'iddocument' und 'idfile' spezifierten Dokuments")  
	@ApiResponses(value = { 
		@ApiResponse(code = 204, message = "Es wird nichts zur�ckgegeben, wenn die Datei gel�scht wurde.")
	})		
	@DELETE
	public Response deleteDocument (@PathParam("idDocument") int idDocument, @PathParam("idFile") int idFile) throws Exception {
		String token = AuthenticationFilter.getTokenFromRequest (httpRequest);
		DocumentManagementSystem dms = new DocumentManagementSystem (token);
		dms.deleteFile(idDocument, idFile);
		return Response.status(204).build();
	}	
}