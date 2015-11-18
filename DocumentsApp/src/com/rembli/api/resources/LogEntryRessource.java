package com.rembli.api.resources;
import com.rembli.log.*;
import javax.xml.bind.annotation.XmlRootElement;

// Hier haben wir für die Ressource nach außen einen eigenen Datentyp definiert
// um einen _self Link mitzugeben
// Dann muss aber in der API eine Übersetzung des internen in den externen Datentyps stattfinden

@XmlRootElement
public class LogEntryRessource extends LogEntry {
	private Links links = new Links();
	
	public LogEntryRessource (LogEntry logEntry) {
		this.setId(logEntry.getId());
		this.setTimestamp(logEntry.getTimestamp());
		this.setUsername(logEntry.getUsername());
		this.setAction(logEntry.getAction());
		this.setEntity(logEntry.getEntity());
		this.setEntityId(logEntry.getEntityId());
		this.setComment(logEntry.getComment());
		setLinks ();
	}
	
	public void setLinks () {
		links.setSelf("/log?entity="+this.getEntity()+"&entityid="+this.getEntityId());
	}
	
	public Links getLinks () {
		return links;
	}	
}
