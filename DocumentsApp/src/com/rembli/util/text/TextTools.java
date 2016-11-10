package com.rembli.util.text;

public class TextTools {
	
	public static String sanitizeString(String str) {
		   return str.replaceAll("[:\\\\/*?|<> \"]", "_");
	}	

}
