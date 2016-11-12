/*
 * Copyright 2016 Nick Russler
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rembli.dms.mimeparser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.commons.io.IOUtils;
import org.w3c.tidy.Tidy;
import com.google.common.base.*;
import com.google.common.html.*;
import com.google.common.io.*;
import com.rembli.util.logger.*;

/**
 * Converts eml files into pdf files.
 * @author Nick Russler
 */
public class MimeMessageConverter {
	/**
	 * Set System parameters to alleviate Java's built in Mime Parser strictness.
	 */
	static {
		System.setProperty("mail.mime.address.strict", "false");
		System.setProperty("mail.mime.decodetext.strict", "false");
		System.setProperty("mail.mime.decodefilename", "true");
		System.setProperty("mail.mime.decodeparameters", "true");
		System.setProperty("mail.mime.multipart.ignoremissingendboundary", "true");
		System.setProperty("mail.mime.multipart.ignoremissingboundaryparameter", "true");

		System.setProperty("mail.mime.parameters.strict", "false");
		System.setProperty("mail.mime.applefilenames", "true");
		System.setProperty("mail.mime.ignoreunknownencoding", "true");
		System.setProperty("mail.mime.uudecode.ignoremissingbeginend", "true");
		System.setProperty("mail.mime.multipart.allowempty", "true");
		System.setProperty("mail.mime.multipart.ignoreexistingboundaryparameter", "true");

		System.setProperty("mail.mime.base64.ignoreerrors", "true");

		// set own cleaner class to handle broken contentTypes
		System.setProperty("mail.mime.contenttypehandler", "mimeparser.ContentTypeCleaner");
	}

	// html wrapper template for text/plain messages
	private static final String HEADER_FIELD_TEMPLATE = "<tr><td class=\"header-name\">%s</td><td class=\"header-value\">%s</td></tr>";
	private static final Pattern IMG_CID_REGEX = Pattern.compile("cid:(.*?)\"", Pattern.DOTALL);
	private static final Pattern IMG_CID_PLAIN_REGEX = Pattern.compile("\\[cid:(.*?)\\]", Pattern.DOTALL);
	
	public static org.w3c.dom.Document convertToXHTML (Message msg) throws Exception {
		
		/* ######### Convert to Mime Message Format ################ */
		
		ByteArrayOutputStream html_out = new ByteArrayOutputStream();
		try {
		     msg.writeTo(html_out);
		}
		finally {
		    if (html_out != null) { html_out.flush(); html_out.close(); }
		}	    	  
		byte[] data = html_out.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(data);	
		MimeMessage message = new MimeMessage(null, in);
		
		/* ######### Parse Header Fields ######### */
		
		Logger.debug("Read and decode header fields");
		String subject = message.getSubject();

		String from = message.getHeader("From", null);
		if (from == null) {
			from = message.getHeader("Sender", null);
		}

		try {
			from = MimeUtility.decodeText(MimeUtility.unfold(from));
		} catch (Exception e) {
			// ignore this error
		}
		
		String[] recipients = new String[0];
		String recipientsRaw = message.getHeader("To", null);
		if (!Strings.isNullOrEmpty(recipientsRaw)) {
			try {
				recipientsRaw = MimeUtility.unfold(recipientsRaw);
				recipients = recipientsRaw.split(",");
				for (int i = 0; i < recipients.length; i++) {
					recipients[i] = MimeUtility.decodeText(recipients[i]);
				}
			} catch (Exception e) {
				// ignore this error
			}
		}
		
		String sentDateStr = message.getHeader("date", null);
		
		/* ######### Parse the mime structure ######### */

		Logger.debug("Find the main message body");
		MimeObjectEntry<String> bodyEntry = MimeMessageParser.findBodyPart(message);
		String charsetName = bodyEntry.getContentType().getParameter("charset");

		Logger.info("Extract the inline images");
		final HashMap<String, MimeObjectEntry<String>> inlineImageMap = MimeMessageParser.getInlineImageMap(message);
		
		/* ######### Embed images in the html ######### */
		
		String htmlBody = bodyEntry.getEntry();
		
		if (bodyEntry.getContentType().match("text/html")) {
			if (inlineImageMap.size() > 0) {
				Logger.debug("Embed the referenced images (cid) using <img src=\"data:image ...> syntax");

				// find embedded images and embed them in html using <img src="data:image ...> syntax
				htmlBody = StringReplacer.replace(htmlBody, IMG_CID_REGEX, new StringReplacerCallback() {
					@Override
					public String replace(Matcher m) throws Exception {
						MimeObjectEntry<String> base64Entry = inlineImageMap.get("<" + m.group(1) + ">");

						// found no image for this cid, just return the matches string as it is
						if (base64Entry == null) {
							return m.group();
						}

						return "data:" + base64Entry.getContentType().getBaseType() + ";base64," + base64Entry.getEntry() + "\"";
					}
				});
			}
		} else {
			Logger.debug("No html message body could be found, fall back to text/plain and embed it into a html document");

			// replace \n line breaks with <br>
			htmlBody = htmlBody.replace("\n", "<br>").replace("\r", "");

			// replace whitespace with &nbsp;
			htmlBody = htmlBody.replace(" ", "&nbsp;");

			//htmlBody = String.format(HTML_WRAPPER_TEMPLATE, charsetName, htmlBody);
			if (inlineImageMap.size() > 0) {
				Logger.debug("Embed the referenced images (cid) using <img src=\"data:image ...> syntax");

				// find embedded images and embed them in html using <img src="data:image ...> syntax
				htmlBody = StringReplacer.replace(htmlBody, IMG_CID_PLAIN_REGEX, new StringReplacerCallback() {
					@Override
					public String replace(Matcher m) throws Exception {
						MimeObjectEntry<String> base64Entry = inlineImageMap.get("<" + m.group(1) + ">");

						// found no image for this cid, just return the matches string
						if (base64Entry == null) {
							return m.group();
						}

						return "<img src=\"data:" + base64Entry.getContentType().getBaseType() + ";base64," + base64Entry.getEntry() + "\" />";
					}
				});
			}
		}
	
		String htmlTemplate = Resources.toString(Resources.getResource("resources/header.html"), StandardCharsets.UTF_8);
		String headers = "";
		
		if (!Strings.isNullOrEmpty(from)) {
			headers += String.format(HEADER_FIELD_TEMPLATE, "From", HtmlEscapers.htmlEscaper().escape(from));
		}
		
		if (!Strings.isNullOrEmpty(subject)) {
			headers += String.format(HEADER_FIELD_TEMPLATE, "Subject", "<b>" + HtmlEscapers.htmlEscaper().escape(subject) + "<b>");
		}
		
		if (recipients.length > 0) {
			headers += String.format(HEADER_FIELD_TEMPLATE, "To", HtmlEscapers.htmlEscaper().escape(Joiner.on(", ").join(recipients)));
		}
		
		if (!Strings.isNullOrEmpty(sentDateStr)) {
			headers += String.format(HEADER_FIELD_TEMPLATE, "Date", HtmlEscapers.htmlEscaper().escape(sentDateStr));
		}
		
		String html = String.format(htmlTemplate, headers, htmlBody);
		
        //use jtidy to clean up the html 
        ByteArrayOutputStream xhtml_out = new ByteArrayOutputStream();
        InputStream html_in = IOUtils.toInputStream(html, StandardCharsets.UTF_8);
        
	    final Tidy tidy = new Tidy();
	    tidy.setQuiet(false);
	    tidy.setXHTML(true);
	    tidy.setMakeClean(true);
	    tidy.setForceOutput(true);
	    tidy.setOutputEncoding("UTF-8");
	    org.w3c.dom.Document document = tidy.parseDOM(html_in, xhtml_out);	 

		return document;
	}
}