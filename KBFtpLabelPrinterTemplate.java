package com.kb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import com.thingworx.metadata.annotations.ThingworxBaseTemplateDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.things.Thing;
import com.thingworx.webservices.context.ThreadLocalContext;

@SuppressWarnings("serial")
@ThingworxPropertyDefinitions(properties = {
		@ThingworxPropertyDefinition(name = "IP", description = "IP Adress", baseType = "STRING", category = "FTP", aspects = {
				"isPersistent:true" }),
		@ThingworxPropertyDefinition(name = "RepositoryPrefix", description = "", baseType = "STRING", category = "FTP", aspects = {
				"isPersistent:true", "defaultValue:F:/ThingworxStorage/repository/KB_LabelRepository/" }) })

@ThingworxBaseTemplateDefinition(name = "GenericThing")
public class KBFtpLabelPrinterTemplate extends Thing {

	public KBFtpLabelPrinterTemplate() {
		// TODO Auto-generated constructor stub
	}

	@ThingworxServiceDefinition(name = "SendFileToPrinter", description = "", category = "FTP", isAllowOverride = false, aspects = {
			"isAsync:false" })
	// @ThingworxServiceResult(name = "Result", description = "", baseType =
	// "NOTHING", aspects = {})
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String SendFileToPrinter(
			@ThingworxServiceParameter(name = "FileName", description = "File Name to send (located in repository)", baseType = "STRING", aspects = {
					"isRequired:true" }) String fileName)
			throws Exception {

		// Retrieves the &quot;me&quot; context for the current thread.
		Object me = ThreadLocalContext.getMeContext();

		String ip = "";
		String repositoryPrefix = "";
		String result = "";

		// Ensure we have a Thing object before attempting to cast the result to Thing.
		if (me instanceof Thing) {
			Thing meThing = (Thing) me;
			// Do further operations requiring the &quot;me&quot; context using the meThing
			// object.

			ip = meThing.GetStringPropertyValue("IP");
			repositoryPrefix = meThing.GetStringPropertyValue("RepositoryPrefix");

			FTPClient ftp = new FTPClient();
			FTPClientConfig config = new FTPClientConfig();
			ftp.configure(config);

			try {
				int reply;
				ftp.connect(ip);

				result = "Connected to " + ip + ": " + ftp.getReplyString();

				reply = ftp.getReplyCode();

				if (!FTPReply.isPositiveCompletion(reply)) {
					ftp.disconnect();
					result += "FTP server refused connection.";
				} else {

					result = "Start uploading file.";
	
					File localFile = new File(repositoryPrefix + fileName);
	
					ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					ftp.storeFile(fileName, new FileInputStream(localFile));
					result += " --> " + ftp.getReplyCode();
					ftp.sendNoOp();
	
					ftp.logout();
				}
			} catch (IOException e) {
				result += e.toString();
			} finally {
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException ioe) {
						// do nothing
					}
				}
			}
		}
		return result;
	}
}
