package com.rembli.test;

import java.io.File;

import net.coobird.thumbnailator.Thumbnails;

public class TestThumbnails {

	public static void main(String[] args) throws Exception {
		
		Thumbnails.of(new File("./testData/TestPic.jpg"))
        	.size(30, 30)
        	.outputFormat("png")
        	.toFile(new File("./testData/TestThumbnail.png"));

	}

}
