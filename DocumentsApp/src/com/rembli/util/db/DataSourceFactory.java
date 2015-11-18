package com.rembli.util.db;

import java.io.*;
import java.util.*;
import javax.sql.*;
import com.zaxxer.hikari.*;

public class DataSourceFactory {
		 
	public static DataSource getDataSource(String propsPath) throws IOException {
			
		FileInputStream fis = new FileInputStream(propsPath);

		Properties props = new Properties();
		props.load(fis);

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(props.getProperty("SQL.DB_DRIVER_CLASS"));
		config.setJdbcUrl(props.getProperty("SQL.DB_URL"));
		config.setUsername(props.getProperty("SQL.DB_USERNAME"));
		config.setPassword(props.getProperty("SQL.DB_PASSWORD"));
		config.setMaximumPoolSize(new Integer(props.getProperty("SQL.MAX_POOLSIZE")).intValue());
		
		return new HikariDataSource(config);
	}	
}
