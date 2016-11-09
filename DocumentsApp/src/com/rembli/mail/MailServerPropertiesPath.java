package com.rembli.mail;

import java.io.*;
import java.net.*;

public class MailServerPropertiesPath {
	 
	public String getPath () throws UnsupportedEncodingException {
		
		String path = this.getClass().getClassLoader().getResource("").getPath();
		String fullPath = URLDecoder.decode(path, "UTF-8");
		String propsPath = fullPath + "mail.properties";
		
		return propsPath;
	}	
}
