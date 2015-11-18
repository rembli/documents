package com.rembli.api.resources;
import javax.xml.bind.annotation.XmlRootElement;
import com.rembli.dms.Document;

@XmlRootElement
public class DocumentRessource extends Document {
	private Links links = new Links();
	
	public DocumentRessource (Document doc) {
		this.setIdDocument(doc.getIdDocument());
		this.setCreatedBy(doc.getCreatedBy());
		this.setCreatedOn(doc.getCreatedOn());
		this.setChangedOn(doc.getChangedOn());
		this.setNote(doc.getNote());
		setLinks();
	}
	
	public void setLinks () {
		links.setSelf("/documents/api/documents/"+idDocument);
	}
	
	public Links getLinks () {
		return links;
	}
}
