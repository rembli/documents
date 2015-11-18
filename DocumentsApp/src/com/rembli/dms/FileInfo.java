package com.rembli.dms;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
public class FileInfo {
	protected int idFile;
	protected int idDocument;
	protected String fileName;
	protected String fileType;
}
