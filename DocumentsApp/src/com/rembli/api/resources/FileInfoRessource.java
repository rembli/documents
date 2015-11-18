package com.rembli.api.resources;
import com.rembli.dms.*;
import javax.xml.bind.annotation.XmlRootElement;

// Hier haben wir f�r die Ressource nach au�en einen eigenen Datentyp definiert
// um einen _self Link mitzugeben
// Dann muss aber in der API eine �bersetzung des internen in den externen Datentyps stattfinden

@XmlRootElement
public class FileInfoRessource extends FileInfo {
	private Links links = new Links();
	
	public FileInfoRessource (FileInfo fileInfo) {
		this.setIdDocument(fileInfo.getIdDocument());
		this.setIdFile(fileInfo.getIdFile());
		this.setFileName(fileInfo.getFileName());
		this.setFileType(fileInfo.getFileType());
		setLinks ();
	}
	
	public void setLinks () {
		links.setSelf("/documents/api/documents/"+idDocument+"/files/"+idFile);
	}
	
	public Links getLinks () {
		return links;
	}	
}
