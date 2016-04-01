package com.rembli.ums;
import javax.annotation.*;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)

//für die Security implementieren wir einen Filter
public class AuthenticationFilter implements ContainerRequestFilter {
	@Context HttpServletRequest httpRequest;
    
    @Override
    public void filter(ContainerRequestContext requestContext) {

    	try {
            UserManagementSystem ums = new UserManagementSystem ();
            String accessTokenSignature = getAccessTokenFromRequest (httpRequest);
            // wenn der Anwender nicht angemeldet ist (oder sonst ein Fehler aufgetreten ist) werfen wir eine Exception
            if (!ums.isAuthenticated(accessTokenSignature))
                throw new NotAuthorizedException("Valid access token must be provided");
            else
                // wenn der Anwender weiter Request schickt und die Anmeldung noch gültig war, wird die Gültigkeit verlängert
                ums.refreshAccessToken(accessTokenSignature);

    	}
        catch (Exception e) {
        	// wenn eine Exception geworfen wurde lassen wir den Request nicht weiter
        	requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
    
    public static String getAccessTokenFromRequest (HttpServletRequest httpRequest) {
        // Get the HTTP Authorization header from the request
        String accessTokenSignature = null;
        
        // stateless authentifizierungs-infos sollte im header stehen
        accessTokenSignature = httpRequest.getHeader("Authorization");
        
        // Zusätzlich noch in der Session nachschauen
        // das ist notwendig, damit die REST-Services auch im Browser bedient werden können
        if (accessTokenSignature == null) {
    		HttpSession session = httpRequest.getSession();
    		accessTokenSignature = (String) session.getAttribute("accessToken");
        }
        
        // Zusätzlich noch in den Query-Parametern nachschauen
        if (accessTokenSignature == null) {
        	accessTokenSignature = httpRequest.getParameter("accessToken");
        }        

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (accessTokenSignature == null || !accessTokenSignature.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Access token must be provided");
        }

        // Extract the token from the HTTP Authorization header
        return accessTokenSignature;
    }
}
