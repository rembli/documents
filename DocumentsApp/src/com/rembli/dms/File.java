package com.rembli.dms;
import lombok.Data;

@Data
public class File {
	protected long idfile;
	protected String fileName;
	protected String fileType;
	protected byte[] data;
}
