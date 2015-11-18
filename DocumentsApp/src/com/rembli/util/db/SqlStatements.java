package com.rembli.util.db;
import java.io.*;
import java.util.Properties;

public class SqlStatements {

	public static String get (String statement) throws IOException {
		Properties prop = new Properties();
	    prop.load(SqlStatements.class.getClassLoader().getResourceAsStream("sql.properties"));
	    return prop.getProperty(statement);
	}
	
}
