package com.rembli.ums;
import lombok.*;
import javax.xml.bind.annotation.*;

@Data
@XmlRootElement
public class AccessToken {
	private String username;
	private java.util.Date expiresOn;

	public AccessToken () {
		
	}
	
	public AccessToken (String username, int secondsToLive) {
		this.username = username;
		
		java.util.Date now = new java.util.Date ();
		now.setTime(now.getTime() + (secondsToLive * 1000));
		this.expiresOn = now;
	}
}
