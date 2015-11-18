package com.rembli.dms;
import java.util.Date;
import lombok.*;
import javax.xml.bind.annotation.*;

// Durch die JAXB-Annotation "@XmlRootElement" werden bei JAX-RS später automatisch XML-Repräsentationen erzeugt
// Durch die Lombok-Annotation "@Data" kann man sich die Ausformulierung der ganzen Getter und Setter Methoden sparen

@Data
@XmlRootElement
public class Document {
	protected int idDocument;
	protected String note;
	protected String createdBy;
	protected Date createdOn;
	protected Date changedOn;	
}
