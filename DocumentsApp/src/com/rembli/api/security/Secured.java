// Damit kann eine neue Annotation "Secured" verwendet werden, um einzelne REST-Endpunkte abzusichern

package com.rembli.api.security;
import java.lang.annotation.*;
import javax.ws.rs.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@NameBinding
public @interface Secured { 
	
}
