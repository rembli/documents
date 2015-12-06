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

//f�r die Security implementieren wir einen Filter
public class AuthenticationFilter implements ContainerRequestFilter {
	@Context HttpServletRequest httpRequest;
    
    @Override
    public void filter(ContainerRequestContext requestContext) {

    	try {
            UserManagementSystem ums = new UserManagementSystem ();
            String tokenSignature = getTokenFromRequest (httpRequest);
            // wenn der Anwender nicht angemeldet ist (oder sonst ein Fehler aufgetreten ist) werfen wir eine Exception
            if (!ums.isAuthenticated(tokenSignature))
                throw new NotAuthorizedException("Authentication token must be provided");
            else
                // wenn der Anwender weiter Request schickt und die Anmeldung noch g�ltig war, wird die G�ltigkeit verl�ngert
                ums.refreshToken(tokenSignature);

    	}
        catch (Exception e) {
        	// wenn eine Exception geworfen wurde lassen wir den Request nicht weiter
        	requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
    
    public static String getTokenFromRequest (HttpServletRequest httpRequest) {
        // Get the HTTP Authorization header from the request
        String authenticationToken = null;
        
        // stateless authentifizierungs-infos sollte im header stehen
        authenticationToken = httpRequest.getHeader("Authorization");
        
        // Zus�tzlich noch in der Session nachschauen
        // das ist notwendig, damit die REST-Services auch im Browser bedient werden k�nnen
        if (authenticationToken == null) {
    		HttpSession session = httpRequest.getSession();
    		authenticationToken = (String) session.getAttribute("authenticationToken");
        }
        
        // Zus�tzlich noch in den Query-Parametern nachschauen
        if (authenticationToken == null) {
        	authenticationToken = httpRequest.getParameter("authenticationToken");
        }        

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authenticationToken == null || !authenticationToken.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authentication token must be provided");
        }

        // Extract the token from the HTTP Authorization header
        return authenticationToken;
    }
}
