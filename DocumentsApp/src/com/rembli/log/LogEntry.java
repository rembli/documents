package com.rembli.log;
import java.util.Date;
import lombok.*;
import javax.xml.bind.annotation.*;

@Data
@XmlRootElement
public class LogEntry {
	protected long id;
	protected Date timestamp;
	protected String username;
	protected String action;
	protected String comment;	
	protected String entity;
	protected String entityId;
	
	public static class ACTION {
		public static String CREATE = "CREATE";
		public static String READ = "READ";
		public static String UPDATE = "UPDATE";		
		public static String DELETE = "DELETE";
		public static String CHECK = "CHECK";		
	}

	public static class ENTITY {
		public static String DOCUMENT = "DOCUMENT";
		public static String USER = "USER";		
	}		
}
