package com.rembli.util.db;

import java.io.*;
import java.net.*;

public class DataSourcePropertiesPath {
	 
	public String getPath () throws UnsupportedEncodingException {
		
		String path = this.getClass().getClassLoader().getResource("").getPath();
		String fullPath = URLDecoder.decode(path, "UTF-8");
		String propsPath = fullPath + "db-active.properties";
		
		return propsPath;
	}	
}
