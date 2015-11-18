package com.rembli.util.db;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class ConnectionPool {

	static Sql2o sql2o = null;

	static {
		try { 
			String datasourcePropertiesPath = (new DataSourcePropertiesPath()).getPath();
			sql2o = new Sql2o(DataSourceFactory.getDataSource(datasourcePropertiesPath));
		} 	
		catch (Exception ignored) {
			System.out.println ("FATAL: "+ignored.getMessage());
		}
	}
	
	static public Connection getConnection () {
		return sql2o.open();
	}
	
}
