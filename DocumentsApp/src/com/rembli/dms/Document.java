package com.rembli.dms;
import java.util.Date;
import lombok.*;
import javax.xml.bind.annotation.*;

@Data
@XmlRootElement
public class Document {
	protected long idDocument;
	protected String note;
	protected String createdBy;
	protected Date createdOn;
	protected Date changedOn;	
}
