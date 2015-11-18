package com.rembli.api.resources;
import javax.xml.bind.annotation.XmlRootElement;
import com.rembli.ums.*;

@XmlRootElement
public class UserInfoRessource extends UserInfo {
	private Links links = new Links();
	
	public UserInfoRessource (UserInfo u) {
		this.setEmail(u.getEmail());
		this.setUsername(u.getEmail());
		setLinks ();
	}
	
	public void setLinks () {
		links.setSelf("/documents/api/userInfo");;
	}
	
	public Links getLinks () {
		return links;
	}	
}
