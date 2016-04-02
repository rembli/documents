package com.rembli.api;
import com.rembli.api.resources.*;
import com.rembli.api.security.AuthenticationFilter;
import com.rembli.api.security.Secured;

import io.swagger.annotations.*;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.rembli.log.*;

@Api(value = "LOG - ALLONGE")
@Secured
@Path("/log")
public class _log {
	@Context HttpServletRequest httpRequest;	
	
    // ########################################################################	
    @ApiOperation(value = "Allonge", notes = "Zeigt die Allonge zu einer Entität an")	
	@ApiResponses(value = { 
			  @ApiResponse(code = 200, message = "Logfiles werden zurück gegeben für die angefragte Entität.", response = LogEntryRessource[].class),
		      @ApiResponse(code = 204, message = "Benutzer hat keine Berechtigung auf die angefragten Logfiles zuzugreifen.") })    
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 
	public LogEntryRessource[] getAllonge (			
			@QueryParam("entity") String entity,
			@QueryParam("entityid") String entityid) throws Exception {
		
		String accessToken = AuthenticationFilter.getAccessTokenFromRequest (httpRequest);
		LogManagementSystem audit = new LogManagementSystem (accessToken);
		try {
			LogEntry[] logEntries = audit.getAllonge(entity, entityid);
			LogEntryRessource[] logEntryRessources =  new LogEntryRessource[logEntries.length];
			for (int i=0; i<logEntries.length;i++) logEntryRessources[i] = new LogEntryRessource(logEntries[i]);
			return logEntryRessources;
		}
		catch (Exception e) {
			return null;
		}
	}
} 
