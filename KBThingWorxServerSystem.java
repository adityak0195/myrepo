package com.kb;

import com.thingworx.entities.utils.ThingUtilities;
import com.thingworx.entities.utils.UserUtilities;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.entities.utils.GroupUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.security.groups.Group;
import com.thingworx.security.users.User;
import com.thingworx.things.Thing;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.PasswordPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

public class KBThingWorxServerSystem extends Resource {

	private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(KBThingWorxServerSystem.class);

	public KBThingWorxServerSystem() {
		// TODO Auto-generated constructor stub
	}

	@ThingworxServiceDefinition(name = "SendMail", description = "Sends Mail via KB Gateway")
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING")
	public String SendMail(
			@ThingworxServiceParameter(name = "mailAddress", description = "Target Mail Address", baseType = "STRING", aspects = {
					"isRequired:true" }) String mailAddress,
			@ThingworxServiceParameter(name = "mailSubject", description = "Mail subject", baseType = "STRING", aspects = {
					"isRequired:true" }) String mailSubject,
			@ThingworxServiceParameter(name = "mailText", description = "Mail text", baseType = "STRING", aspects = {
					"isRequired:false", "-" }) String mailText)
			throws Exception {

		String from = "Do not reply to Thingworx <thingworx_mail_user.SVCMUC@knorr-bremse.com>";
		Properties mailProperties = new Properties();

		mailProperties.setProperty("mail.transport.protocol", "smtp");
		mailProperties.setProperty("mail.smtp.host", "smtp-relay.corp.knorr-bremse.com");
		mailProperties.setProperty("mail.smtp.port", "25");
		mailProperties.setProperty("mail.smtp.auth", "true");
		mailProperties.setProperty("mail.smtp.user", "svc.MUC.thingworx_ma");
		mailProperties.setProperty("mail.smtp.password", "V34oYXw7qg65unyJXQCJwpnoPY0mj7");
		mailProperties.setProperty("mail.smtp.starttls.enable", "true");

		// Check for user name and password
		String userName = "";
		String userPassword = "";
		String prot = mailProperties.getProperty("mail.transport.protocol", null);
		if (null != prot) {
			userName = mailProperties.getProperty("mail." + prot + ".user", null);
			userPassword = mailProperties.getProperty("mail." + prot + ".password", null);
		}

		// Create session
		Session mailSession = null;
		if (null != userName && null != userPassword) {
			final String name = userName;
			final String pw = userPassword;

			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(name, pw);
				}
			};
			mailSession = Session.getInstance(mailProperties, auth);
		} else {
			mailSession = Session.getInstance(mailProperties);
		}

		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(mailSession);
		String res = "OK";

		// Set "from" address
		try {
			message.setFrom(new InternetAddress(from));

			// Set "to" address
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress));

			// Subject
			message.setSubject(mailSubject);

			// Body
			message.setText("Do not reply to this mail!! \n\n\n" + mailText);

			// Send message
			Transport.send(message);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			res = "AddressException: " + e.toString();
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			res = "MessagingException: " + e.toString();
			e.printStackTrace();
		}
		return res;
	}
	
	@ThingworxServiceDefinition(name = "CheckAndFixIndexFileIssue", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String CheckAndFixIndexFileIssue() {
		_logger.trace("Entering Service: CheckAndFixIndexFileIssue");
		File filePath1 = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\Thingworx\\Builder\\index.html");
		File filePath2 = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\Thingworx\\Runtime\\index.html");
		String htmlContent1 = GetBuilderIndexContent();
		String htmlContent2 = GetRuntimeIndexContent();

						
		try {
			
			if (!filePath1.exists()) {
				_logger.warn(filePath1+" was missing -> fix started");
				 try (FileWriter writer1 = new FileWriter(filePath1)) {
	                    writer1.write(htmlContent1);
	                }
			}
			if (!filePath2.exists()) {
				_logger.warn(filePath2+" was missing -> fix started");
				 try (FileWriter writer2 = new FileWriter(filePath2)) {
	                    writer2.write(htmlContent2);
	                }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@ThingworxServiceDefinition(name = "GetBuilderIndexContent", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetBuilderIndexContent() {
		_logger.trace("Entering Service: GetBuilderIndexContent");

		String htmlContent1 = "<!-- *** MANAGED SECTION DO NOT EDIT *** --> \n" +
			"<!-- BEGIN_OPTIMIZED_CSS --> \n" +
				"<script> \n" +
				"var COMBINED_EXT_CSS='css/CombinedExtensions.20241115_095516.css';\n" +
				"</script>\n" +
				"\n" +
			"<!-- END_OPTIMIZED_CSS -->\n" +
			"\n" +
			"<!-- *** MANAGED SECTION DO NOT EDIT *** -->\n" +
			"<!-- BEGIN_EXTENSION_CSS -->\n" +
			"<!-- END_EXTENSION_CSS -->\n" +
			"\n" +

			"<!-- *** MANAGED SECTION DO NOT EDIT *** -->\n" +
			"<!-- BEGIN_OPTIMIZED_JS -->\n" +
		"		<script>\n" +
		"		var COMBINED_EXT_JS='js/CombinedExtensions.20241115_095515.js';\n" +
		"		</script>\n" +
		"		\n" +
		"	<!-- END_OPTIMIZED_JS -->\n" +

		"	<!-- *** MANAGED SECTION DO NOT EDIT *** -->\n" +
		"	<!-- BEGIN_EXTENSION_SCRIPTS -->\n" +
		"	<!-- END_EXTENSION_SCRIPTS -->\n" ;
		
		return htmlContent1;
	}
	
	
	@ThingworxServiceDefinition(name = "GetRuntimeIndexContent", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetRuntimeIndexContent() {
		_logger.trace("Entering Service: GetRuntimeIndexContent");

		String htmlContent2 = "	<!DOCTYPE html>\n" +
		"	<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
		"	<head>\n" +
		"		<meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate\"/>\n" +
		"		<meta http-equiv=\"Pragma\" content=\"no-cache\"/>\n" +
		"		<meta http-equiv=\"Expires\" content=\"0\"/>\n" +
		"		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n" +
		"		<title>Thingworx Mashup Runtime</title>\n" +
		"		\n" +
		"		<!-- TW-59145 -->\n" +
		"		<script>\n" +
		"			if (window.location.hash === '#.') {\n" +
		"				window.location.hash = '';\n" +
		"			}\n" +
		"		</script>\n" +
		"		\n" +
		"		<link rel=\"shortcut icon\" href=\"images/favicon.ico\"/>\n" +
		"		<style id=\"widget-instance-styles\" type=\"text/css\"></style>\n" +
		"				\n" +
		"		<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/mashup-runtime.css?_v=9.3.6\"/>\n" +
		"				\n" +
		"		<script>\n" +
		"			function manageSSOAjaxRedirect()\n" +
		"			{\n" +
		"				if (!TW || !TW.Runtime || !TW.Runtime.showStatusText) {\n" +
		"					setTimeout(manageSSOAjaxRedirect, 100);\n" +
		"				} else {\n" +
		"					TW.Runtime.showStatusText('info', TW.Runtime.convertLocalizableString('[[sso-session-timeout-message]]'), true);\n" +
		"					var statusMsgBox = $('.tw-status-msg-box');\n" +
		"					statusMsgBox.find('.close-sticky').off('click').click(function(e) {\n" +
		"						TW.openUrl('href', TW.prepareSSORedirectUrl(), null, null, true);\n" +
		"					});\n" +
		"				}\n" +
		"				return false;\n" +
		"			}\n" +
		"		</script>\n" +
		"		\n" +
		"		<!-- *** MANAGED SECTION DO NOT EDIT *** -->\n" +
		"		<!-- BEGIN_EXTENSION_CSS -->\n" +
		"		<!-- END_EXTENSION_CSS -->\n" +
		"		\n" +
		"		<!-- *** MANAGED SECTION DO NOT EDIT *** -->\n" +
		"		<!-- BEGIN_OPTIMIZED_CSS -->\n" +
		"		<link type = \"text/css\" rel = \"stylesheet\" href = \"css/CombinedExtensions.20241115_095516.css\" />\n" +
		"		<!-- END_OPTIMIZED_CSS -->\n" +
		"	</head>\n" +
		"	<body id=\"runtime\">\n" +
		"	<div class=\"runtime-wrapper\">\n" +
		"		<div id=\"log-btn\"><a id=\"show-hide-log\" style=\"display:none;\">Show Log</a></div>\n" +
		"		<div id=\"tw-page-log\" style=\"display:none;\"></div>\n" +
		"		<div id=\"runtime-workspace\">\n" +
		"		</div>\n" +
		"		<div id=\"trace-btn\"><a id=\"show-hide-trace\" style=\"display:none;\">Show Trace</a></div>\n" +
		"		<div id=\"trace\" style=\"display:none;\"></div>\n" +
		"		<div class=\"tw-status-msg-box\" style=\"display:none;\">\n" +
		"			<div class=\"status-label\">Status Message</div>\n" +
		"			<div class=\"close-sticky\">\n" +
		"				<span class=\"close-sticky-btn\">Dismiss</span>\n" +
		"			</div>\n" +
		"			<div class=\"status-msg-container\">\n" +
		"				<div class=\"status-msg\">\n" +
		"					<div id=\"status-msg-text\"></div>\n" +
		"				</div>\n" +
		"			</div>\n" +
		"		</div>\n" +
		"	</div>\n" +
		"	<div id=\"mashup-toolbar-resolution\" style=\"z-index:20001;\" class=\"hide-runtime-debug-bar\">\n" +
		"		<ul class=\"mashup-toolbar-items\">\n" +
		"			<li class=\"mashup-toolbar-item\">\n" +
		"				<button id=\"runtime-log\" class=\"mashup-toolbar-button\" title=\"Show/Hide Log\">Show/Hide Log</button>\n" +
		"			</li>\n" +
		"			<li class=\"divider\"></li>\n" +
		"			<li class=\"mashup-toolbar-item\">\n" +
		"				<button id=\"show-bindings\" class=\"mashup-toolbar-button\" title=\"Show/Hide Debug Info\">Show/Hide Debug Info</button>\n" +
		"			</li>\n" +
		"			<li class=\"divider\"></li>\n" +
		"			<li class=\"mashup-toolbar-item\">\n" +
		"				<button id=\"refresh-mashup\" class=\"mashup-toolbar-button\" title=\"Reload Mashup\"><span class=\"reload-icon\">Reload</span></button>\n" +
		"			</li>\n" +
		"			<li class=\"divider\"></li>\n" +
		"			<li class=\"mashup-toolbar-item\">\n" +
		"				<select class=\"language-picker\">\n" +
		"					<option value=\"Default\">Default</option>\n" +
		"					<option value=\"de\">de</option>\n" +
		"					<option value=\"show-tokens\">Show [[Tokens]]</option>\n" +
		"					<!--<option value=\"refresh\">&lt;Refresh Languages&gt;</option>-->\n" +
		"				</select>\n" +
		"			</li>\n" +
		"			<li class=\"divider\"></li>\n" +
		"			<li class=\"mashup-toolbar-item\">\n" +
		"				<select class=\"resolution-picker\">\n" +
		"					<option value=\"Fullscreen\">Fullscreen</option>\n" +
		"					<option value=\"1024x768\">1024x768</option>\n" +
		"					<option value=\"1280x1024\">1280x1024</option>\n" +
		"					<option value=\"1366x768\">1366x768</option>\n" +
		"					<option value=\"HD 1280x720\">HD 1280x720</option>\n" +
		"					<option value=\"FHD 1920x1080\">FHD 1920x1080</option>\n" +
		"					<!--<option>iPhone Landscape</option>-->\n" +
		"					<!--<option>iPhone Portrait</option>-->\n" +
		"					<option value=\"iPad Landscape\">iPad Landscape</option>\n" +
		"					<option value=\"iPad Portrait\">iPad Portrait</option>\n" +
		"					<!--<option>Custom</option>-->\n" +
		"				</select>\n" +
		"			</li>\n" +
		"		</ul>\n" +
		"	</div>\n" +
		"	\n" +
		"	<div id=\"generic-entity-picker\" style=\"display:none;\">\n" +
		"		<div id=\"generic-entity-picker-list\">\n" +
		"		</div>\n" +
		"	</div>\n" +
		"	\n" +
		"	<script type=\"text/javascript\" src=\"./mashup-vendor-runtime.js?_v=9.3.6\"></script>\n" +
		"			\n" +
		"	<script type=\"text/javascript\">\n" +
		"		// this definition should be done before rest of JS files will be loaded\n" +
			"	// otherwise possible situation when ajax calls will fail before hook is ready-to-use\n" +
		"		// (in case index.html was taken from the cache)\n" +
		"		$(document).ajaxComplete(function(event, xhr, options) {\n" +
		"			var sessionTimeOutHeaderRefreshUrl = xhr.getResponseHeader(\"TWX_SSO_SESSION_TIME_OUT_REFRESH_URL\");\n" +
		"			if (sessionTimeOutHeaderRefreshUrl != null) {\n" +
		"				return manageSSOAjaxRedirect();\n" +
		"			}\n" +
		"		});\n" +
		"	</script>\n" +
		"	\n" +
		"	<script type=\"text/javascript\" src=\"./advanced-widgets-runtime-pre.js?_v=9.3.6\"></script>\n" +
		"	<script type=\"text/javascript\" src=\"../Common/mashup-vendor-shared.js?_v=9.3.6\"></script>\n" +
		"	<script type=\"text/javascript\" src=\"./advanced-widgets-vendor-bundle.js?_v=-9.3.6\"></script>\n" +
		"	<script type=\"text/javascript\" src=\"./advanced-widgets-bundle.js?_v=9.3.6\"></script>\n" +
		"	<script type=\"text/javascript\" src=\"./advanced-widgets-runtime-post.js?_v=9.3.6\"></script>\n" +
		"			\n" +
		"	<script type=\"text/javascript\" src=\"./mashup-common-general-runtime.js?_v=9.3.6\"></script>\n" +
		"	<script type=\"text/javascript\" src=\"./mashup-runtime.js?_v=9.3.6\"></script>\n" +
		"			\n" +
		"	<script type=\"text/javascript\" src=\"../Common/mashup-common-widgets-shared.js?_v=9.3.6\"></script>\n" +
		"	<script type=\"text/javascript\" src=\"./mashup-common-widgets-runtime.js?_v=9.3.6\"></script>\n" +
		"	<script type=\"module\" src=\"../Common/wc/lib/polymer.bundle.js\"></script>\n" +
		"	<script type=\"module\" src=\"../Common/wc/lib/ptcswidgets.bundle.js\"></script>\n" +
		"			\n" +
		"	<script type=\"text/javascript\">\n" +
		"		// Note: this is needed for Widgets that implement custom dialog API\n" +
		"		var TW = TW || {};\n" +
		"		TW.IDE = TW.IDE || {};\n" +
		"		TW.IDE.Dialogs = TW.IDE.Dialogs || {};\n" +
		"		window.appVersion = '9.3.6';\n" +
		"		\n" +
		"		$(document).ready(function() {\n" +
		"			TW.restoreHashForSSORedirect();\n" +
		"			\n" +
		"			// add onclick handler on body. if click was inside of <a href=...> element - we'll keep hash info of it for SSO needs\n" +
		"			// note onclick fired BEFORE actual url replaces. so we always will catch it\n" +
		"			$('body').click(function(e) {\n" +
		"				let aEl = $(e.target).closest('a');\n" +
		"				if (aEl.size() > 0) {\n" +
		"					let url = aEl[0].href;\n" +
		"					if (url) {\n" +
		"						let target = aEl[0].target;\n" +
		"						let bRequireReload = target && target !== '_self';\n" +
		"						TW.keepHashForSSORedirect(url, bRequireReload);\n" +
		"					}\n" +
		"				}\n" +
		"			});\n" +
		"			$(window).trigger('tw.app.loaded');\n" +
		"		});\n" +
		"	</script>\n" +
		"	<script type=\"text/javascript\">\n" +
		"		window.defineBackup = window.defineBackup || window.define;\n" +
		"		window.define = undefined;\n" +
		"	</script>\n" +
		"	<!-- *** MANAGED SECTION DO NOT EDIT *** -->\n" +
		"	<!-- BEGIN_OPTIMIZED_JS -->\n" +
		"		<script type = \"text/javascript\"  charset = \"UTF-8\" src = \"js/CombinedExtensions.20241115_095516.js\"></script>\n" +
		"	<!-- END_OPTIMIZED_JS -->\n" +
		"	\n" +
		"	<!-- *** MANAGED SECTION DO NOT EDIT *** -->\n" +
		"	<!-- BEGIN_EXTENSION_SCRIPTS -->\n" +
		"	<!-- END_EXTENSION_SCRIPTS -->\n" +
		"	<script type=\"text/javascript\">\n" +
		"		window.define = window.defineBackup;\n" +
		"	</script>\n" +
		"	</body>\n" +
		"	</html>\n";
		
		return htmlContent2;
	}
	
	
	@ThingworxServiceDefinition(name = "RestartWindows", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String RestartWindows() {
		_logger.trace("Entering Service: RestartWindows");

		String shutdownCmd = "shutdown -r -f -c \"KBThingWorxServerSystem\"";
		try {
			Runtime.getRuntime().exec(shutdownCmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}

	@ThingworxServiceDefinition(name = "CreateCertificateContainer", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String CreateCertificateContainer() {
		_logger.trace("Entering Service: CreateCertificateContainer");

		return KBCertCreator.createCertContainer();
	}

	@ThingworxServiceDefinition(name = "GetStagingIdentifier", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetStagingIdentifier() {
		_logger.trace("Entering Service: GetStagingIdentifier");
		ValueCollection params = new ValueCollection();
		params.put("Host", new StringPrimitive(GetHostName()));

		Thing serverDB = ThingUtilities.findThing("KBThingWorxServerSystemDataTable");
		InfoTable result = null;
		try {
			result = serverDB.processServiceRequest("GetStagingSystem", params);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return result.getRow(0).getStringValue("result");
	}

	@ThingworxServiceDefinition(name = "GetCommonHostName", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetCommonHostName() {
		_logger.trace("Entering Service: GetCommonHostName");

		String result = "smartproduction.corp.knorr-bremse.com";

		try {
			String sid = GetStagingIdentifier();
			String loc = GetLocationIdentifier();

			if (sid.equals("PROD")) {
				sid = "";
			}
			result = "smartproduction" + loc + sid + ".corp.knorr-bremse.com";
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return result;
	}

	@ThingworxServiceDefinition(name = "GetLocationIdentifier", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetLocationIdentifier() {
		_logger.trace("Entering Service: GetLocationIdentifier");
		ValueCollection params = new ValueCollection();
		params.put("Host", new StringPrimitive(GetHostName()));

		Thing serverDB = ThingUtilities.findThing("KBThingWorxServerSystemDataTable");
		InfoTable result = null;
		try {
			result = serverDB.processServiceRequest("GetLocation", params);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return result.getRow(0).getStringValue("result");
	}

	@ThingworxServiceDefinition(name = "GetGlobalLocalIdentifier", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetGlobalLocalIdentifier() {
		_logger.trace("Entering Service: GetGlobalLocalIdentifier");

		if (GetHostName().equals("MUC")) {
			_logger.trace("Exiting Service: GetGlobalLocalIdentifier --> global");
			return "Global";
		} else {
			_logger.trace("Exiting Service: GetGlobalLocalIdentifier --> local");
			return "Local";
		}
	}

	@ThingworxServiceDefinition(name = "GetHostName", description = "", category = "ThingWorxSystem", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetHostName() {
		_logger.trace("Entering Service: GetHostName");
		try {
			return InetAddress.getLocalHost().getHostName().toUpperCase();
		} catch (UnknownHostException e) {
			_logger.error("KBRateOfQualityShape/GetHostName: Unable to get HostName " + e.toString());
			return "Error";
		}
	}

	@ThingworxServiceDefinition(name = "GetKPIThingWorxDatabaseThing", description = "", category = "ThingWorxDatabase", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetKPIThingWorxDatabaseThing() {
		_logger.trace("Entering Service: GetKPIThingWorxDatabaseThing");
		ValueCollection params = new ValueCollection();
		params.put("Host", new StringPrimitive(GetHostName()));

		Thing serverDB = ThingUtilities.findThing("KBThingWorxServerSystemDataTable");
		InfoTable result = null;
		try {
			result = serverDB.processServiceRequest("GetKPIThingWorxDatabaseThing", params);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return result.getRow(0).getStringValue("result");
	}

	@ThingworxServiceDefinition(name = "GetKPIGlobalThingWorxDatabaseThing", description = "", category = "ThingWorxDatabase", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetKPIGlobalThingWorxDatabaseThing() {
		_logger.trace("Entering Service: GetKPIGlobalThingWorxDatabaseThing");
		ValueCollection params = new ValueCollection();
		params.put("Host", new StringPrimitive(GetHostName()));

		Thing serverDB = ThingUtilities.findThing("KBThingWorxServerSystemDataTable");
		InfoTable result = null;
		try {
			result = serverDB.processServiceRequest("GetKPIGlobalThingWorxDatabaseThing", params);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return result.getRow(0).getStringValue("result");
	}

	@ThingworxServiceDefinition(name = "GetKPILocalThingWorxDatabaseThing", description = "", category = "ThingWorxDatabase", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetKPILocalThingWorxDatabaseThing() {
		_logger.trace("Entering Service: GetKPILocalThingWorxDatabaseThing");
		ValueCollection params = new ValueCollection();
		params.put("Host", new StringPrimitive(GetHostName()));

		Thing serverDB = ThingUtilities.findThing("KBThingWorxServerSystemDataTable");
		InfoTable result = null;
		try {
			result = serverDB.processServiceRequest("GetKPILocalThingWorxDatabaseThing", params);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return result.getRow(0).getStringValue("result");
	}

	@ThingworxServiceDefinition(name = "GetSAPThingWorxDatabaseThing", description = "", category = "ThingWorxDatabase", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetSAPThingWorxDatabaseThing() {
		_logger.trace("Entering Service: GetSAPThingWorxDatabaseThing");
		ValueCollection params = new ValueCollection();
		params.put("Host", new StringPrimitive(GetHostName()));

		Thing serverDB = ThingUtilities.findThing("KBThingWorxServerSystemDataTable");
		InfoTable result = null;
		try {
			result = serverDB.processServiceRequest("GetSAPThingWorxDatabaseThing", params);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		return result.getRow(0).getStringValue("result");
	}

	@ThingworxServiceDefinition(name = "GetAvailableTimeZones", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = {
			"dataShape:KBAvailableTimeZonesDataShape" })
	public InfoTable GetAvailableTimeZones() throws Exception {

		InfoTable fileInfotable = InfoTableInstanceFactory
				.createInfoTableFromDataShape("KBAvailableTimeZonesDataShape");
		ValueCollection vc;

		String[] zones = TimeZone.getAvailableIDs();
		for (String zone : zones) {
			vc = new ValueCollection();
			vc.put("TimeZoneId", new StringPrimitive(zone));

			fileInfotable.AddRow(vc.toJSON());
		}

		return fileInfotable;
	}

	@ThingworxServiceDefinition(name = "GetUTCServerOffsetInMinutes", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
	public int GetUTCServerOffsetInMinutes() {
		_logger.trace("Entering Service: GetUTCServerOffsetInMinutes");
		TimeZone tz = Calendar.getInstance().getTimeZone();
		// System.out.println("Time Zone Display Name "+tz.getDisplayName()); // (i.e.
		// Moscow Standard Time)
		// System.out.println("Time Zone ID "+tz.getID());
		// System.out.println("Offset in Minutes "+tz.getOffset((new
		// Date()).getTime())/1000/60);

		return tz.getOffset((new Date()).getTime()) / 1000 / 60;
	}

	@ThingworxServiceDefinition(name = "GetTimeZoneOffsetInMinutes", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
	public int GetTimeZoneOffsetInMinutes(
			@ThingworxServiceParameter(name = "timeZoneId", description = "Time zone Id", baseType = "STRING", aspects = {
					"isRequired:true" }) String timeZoneId) {
		_logger.trace("Entering Service: GetTimeZoneOffsetInMinutes");
		TimeZone tz = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).getTimeZone();
		return tz.getOffset((new Date()).getTime()) / 1000 / 60;
	}

	/*
	 * Removed this service as it always ready platform-settings! KBTimeHelperThing
	 * holds the time zone setting in ServerTimeZone property
	 * 
	 * @ThingworxServiceDefinition(name = "GetServerTimeZoneOffsetInMinutes",
	 * description = "", category = "UTC", isAllowOverride = false, aspects = {
	 * "isAsync:false" })
	 * 
	 * @ThingworxServiceResult(name = "Result", description = "", baseType =
	 * "INTEGER", aspects = {}) public int GetServerTimeZoneOffsetInMinutes() {
	 * _logger.trace("Entering Service: GetTimeZoneOffsetInMinutes"); TimeZone tz =
	 * Calendar.getInstance(TimeZone.getTimeZone(GetServerTimeZone())).getTimeZone()
	 * ; return tz.getOffset((new Date()).getTime()) / 1000 / 60; }
	 */
	@ThingworxServiceDefinition(name = "GetTimeZoneOffsetInMinutesWithDate", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
	public int GetTimeZoneOffsetInMinutesWithDate(
			@ThingworxServiceParameter(name = "timeZoneId", description = "Time zone Id", baseType = "STRING", aspects = {
					"isRequired:true" }) String timeZoneId,
			@ThingworxServiceParameter(name = "dateTime", description = "Time", baseType = "DATETIME", aspects = {
					"isRequired:true" }) DateTime dateTime) {
		_logger.trace("Entering Service: GetTimeZoneOffsetInMinutes");
		TimeZone tz = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).getTimeZone();
		return tz.getOffset(dateTime.toDate().getTime()) / 1000 / 60;
	}

	/*
	 * Removed this service as it always ready platform-settings! KBTimeHelperThing
	 * holds the time zone setting in ServerTimeZone property
	 * 
	 * @ThingworxServiceDefinition(name =
	 * "GetServerTimeZoneOffsetInMinutesWithDate", description = "", category =
	 * "UTC", isAllowOverride = false, aspects = { "isAsync:false" })
	 * 
	 * @ThingworxServiceResult(name = "Result", description = "", baseType =
	 * "INTEGER", aspects = {}) public int
	 * GetServerTimeZoneOffsetInMinutesWithDate(@ThingworxServiceParameter(name =
	 * "dateTime", description = "Time", baseType = "DATETIME", aspects = {
	 * "isRequired:true" }) DateTime dateTime) {
	 * _logger.trace("Entering Service: GetTimeZoneOffsetInMinutes"); TimeZone tz =
	 * Calendar.getInstance(TimeZone.getTimeZone(GetServerTimeZone())).getTimeZone()
	 * ; return tz.getOffset(dateTime.toDate().getTime()) / 1000 / 60; }
	 */
	@ThingworxServiceDefinition(name = "GetServerTimeZoneId", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetServerTimeZoneId() {
		_logger.trace("Entering Service: GetServerTimeZoneId");
		TimeZone tz = Calendar.getInstance().getTimeZone();
		// System.out.println("Time Zone Display Name "+tz.getDisplayName()); // (i.e.
		// Moscow Standard Time)
		// System.out.println("Time Zone ID "+tz.getID());
		// System.out.println("Offset in Minutes "+tz.getOffset((new
		// Date()).getTime())/1000/60);

		return tz.getID();
	}

	@ThingworxServiceDefinition(name = "GetServerTimeZoneDisplayName", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetServerTimeZoneDisplayName() {
		_logger.trace("Entering Service: GetServerTimeZoneDisplayName");
		TimeZone tz = Calendar.getInstance().getTimeZone();
		// System.out.println("Time Zone Display Name "+tz.getDisplayName()); // (i.e.
		// Moscow Standard Time)
		// System.out.println("Time Zone ID "+tz.getID());
		// System.out.println("Offset in Minutes "+tz.getOffset((new
		// Date()).getTime())/1000/60);

		return tz.getDisplayName();
	}

	@ThingworxServiceDefinition(name = "GetTimeZoneDisplayName", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String GetTimeZoneDisplayName(
			@ThingworxServiceParameter(name = "timeZoneId", description = "Time zone Id", baseType = "STRING", aspects = {
					"isRequired:true" }) String timeZoneId) {
		_logger.trace("Entering Service: GetTimeZoneDisplayName");
		TimeZone tz = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).getTimeZone();
		return tz.getDisplayName();
	}

	@ThingworxServiceDefinition(name = "GetThingWorxExternalDatabaseSettings", description = "", category = "ThingWorxDatabase", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public InfoTable GetThingWorxExternalDatabaseSettings(
			@ThingworxServiceParameter(name = "providerPackage", description = "e.g. MssqlPersistenceProviderPackage", baseType = "STRING", aspects = {
					"isRequired:true" }) String providerPackage)
			throws Exception {

		JSONObject obj = GetPlatformSettings();
		String driverClass = obj.getJSONObject("KBPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("driverClass");
		String jdbcUrl = obj.getJSONObject("KBPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("jdbcUrl");
		String password = obj.getJSONObject("KBPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("password");
		String username = obj.getJSONObject("KBPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("username");

		String connectionValidationString;
		Integer maxConnections;

		try {
			connectionValidationString = obj.getJSONObject("KBPersistenceProviderPackageConfigs")
					.getJSONObject(providerPackage).getJSONObject("ConnectionInformation")
					.getString("connectionValidationString");
		} catch (Exception ex) {
			connectionValidationString = "select GETDATE()";
		}

		try {
			maxConnections = obj.getJSONObject("KBPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
					.getJSONObject("ConnectionInformation").getInt("maxConnections");
		} catch (Exception ex) {
			maxConnections = 10;
		}

		InfoTable connectionInfo = InfoTableInstanceFactory.createInfoTableFromDataShape("KBConnectionInfoDataShape");
		ValueCollection vc;

		vc = new ValueCollection();
		vc.put("connectionValidationString", new StringPrimitive(connectionValidationString));
		vc.put("jDBCConnectionURL", new StringPrimitive(jdbcUrl));
		vc.put("jDBCDriverClass", new StringPrimitive(driverClass));
		vc.put("maxConnections", new NumberPrimitive(maxConnections));
		vc.put("password", new PasswordPrimitive(password));
		vc.put("userName", new StringPrimitive(username));

		connectionInfo.AddRow(vc.toJSON());

		return connectionInfo;
	}

	@ThingworxServiceDefinition(name = "GetThingWorxCAPSDatabaseSettings", description = "", category = "ThingWorxDatabase", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public InfoTable GetThingWorxCAPSDatabaseSettings(
			@ThingworxServiceParameter(name = "providerPackage", description = "e.g. MssqlPersistenceProviderPackage", baseType = "STRING", aspects = {
					"isRequired:true" }) String providerPackage)
			throws Exception {

		JSONObject obj = GetPlatformSettings();
		String driverClass = obj.getJSONObject("KBCAPSPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("driverClass");
		String jdbcUrl = obj.getJSONObject("KBCAPSPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("jdbcUrl");
		String password = obj.getJSONObject("KBCAPSPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("password");
		String username = obj.getJSONObject("KBCAPSPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("username");

		String connectionValidationString;
		Integer maxConnections;

		try {
			connectionValidationString = obj.getJSONObject("KBCAPSPersistenceProviderPackageConfigs")
					.getJSONObject(providerPackage).getJSONObject("ConnectionInformation")
					.getString("connectionValidationString");
		} catch (Exception ex) {
			connectionValidationString = "select GETDATE()";
		}

		try {
			maxConnections = obj.getJSONObject("KBCAPSPersistenceProviderPackageConfigs").getJSONObject(providerPackage)
					.getJSONObject("ConnectionInformation").getInt("maxConnections");
		} catch (Exception ex) {
			maxConnections = 10;
		}

		InfoTable connectionInfo = InfoTableInstanceFactory.createInfoTableFromDataShape("KBConnectionInfoDataShape");
		ValueCollection vc;

		vc = new ValueCollection();
		vc.put("connectionValidationString", new StringPrimitive(connectionValidationString));
		vc.put("jDBCConnectionURL", new StringPrimitive(jdbcUrl));
		vc.put("jDBCDriverClass", new StringPrimitive(driverClass));
		vc.put("maxConnections", new NumberPrimitive(maxConnections));
		vc.put("password", new PasswordPrimitive(password));
		vc.put("userName", new StringPrimitive(username));

		connectionInfo.AddRow(vc.toJSON());

		return connectionInfo;
	}

@ThingworxServiceDefinition(name = "GetThingWorxInternalDatabaseSettings", description = "", category = "ThingWorxDatabase", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public InfoTable GetThingWorxInternalDatabaseSettings(
			@ThingworxServiceParameter(name = "providerPackage", description = "e.g. MssqlPersistenceProviderPackage", baseType = "STRING", aspects = {
					"isRequired:true" }) String providerPackage)
			throws Exception {

		JSONObject obj = GetPlatformSettings();
		String driverClass = obj.getJSONObject("PersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("driverClass");
		String jdbcUrl = obj.getJSONObject("PersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("jdbcUrl");
		String password = obj.getJSONObject("PersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("password");
		String username = obj.getJSONObject("PersistenceProviderPackageConfigs").getJSONObject(providerPackage)
				.getJSONObject("ConnectionInformation").getString("username");

		String connectionValidationString;
		Integer maxConnections;

		try {
			connectionValidationString = obj.getJSONObject("PersistenceProviderPackageConfigs")
					.getJSONObject(providerPackage).getJSONObject("ConnectionInformation")
					.getString("connectionValidationString");
		} catch (Exception ex) {
			connectionValidationString = "select GETDATE()";
		}

		try {
			maxConnections = obj.getJSONObject("PersistenceProviderPackageConfigs").getJSONObject(providerPackage)
					.getJSONObject("ConnectionInformation").getInt("maxConnections");
		} catch (Exception ex) {
			maxConnections = 10;
		}

		InfoTable connectionInfo = InfoTableInstanceFactory.createInfoTableFromDataShape("KBConnectionInfoDataShape");
		ValueCollection vc;

		vc = new ValueCollection();
		vc.put("connectionValidationString", new StringPrimitive(connectionValidationString));
		vc.put("jDBCConnectionURL", new StringPrimitive(jdbcUrl));
		vc.put("jDBCDriverClass", new StringPrimitive(driverClass));
		vc.put("maxConnections", new NumberPrimitive(maxConnections));
		vc.put("password", new PasswordPrimitive(password));
		vc.put("userName", new StringPrimitive(username));

		connectionInfo.AddRow(vc.toJSON());

		return connectionInfo;
	}

	@Deprecated
	@ThingworxServiceDefinition(name = "GetServerTimeZone", description = "", category = "KBServerConfig", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public String GetServerTimeZone() {

		try {
			JSONObject obj = GetPlatformSettings();
			return obj.getJSONObject("KBServerConfigs").getString("TimeZone");
		} catch (Exception ex) {
			return "Europe/Berlin";
		}
	}

	@ThingworxServiceDefinition(name = "GetKBServerConfig", description = "", category = "KBServerConfig", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public String GetKBServerConfig(
			@ThingworxServiceParameter(name = "settingsName", description = "e.g. TimeZone", baseType = "STRING", aspects = {
					"isRequired:true" }) String settingsName) {

		try {
			JSONObject obj = GetPlatformSettings();
			return obj.getJSONObject("KBServerConfigs").getString(settingsName);
		} catch (Exception ex) {
			if (settingsName == "TimeZone") {
				return "Europe/Berlin";
			} else if (settingsName == "TimeZoneDB") {
				return "Central European Standard Time";
			} else {
				return "";
			}
		}
	}

	@ThingworxServiceDefinition(name = "GetUTCYear", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public int GetUTCYear() {

		Instant instant = Instant.now();
		return instant.atZone(ZoneOffset.UTC).getYear();
	}

	@ThingworxServiceDefinition(name = "GetUTCMonth", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public int GetUTCMonth() {

		Instant instant = Instant.now();
		return instant.atZone(ZoneOffset.UTC).getMonthValue();
	}

	@ThingworxServiceDefinition(name = "GetUTCDay", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public int GetUTCDay() {

		Instant instant = Instant.now();
		return instant.atZone(ZoneOffset.UTC).getDayOfMonth();
	}

	@ThingworxServiceDefinition(name = "GetUTCHour", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public int GetUTCHour() {

		Instant instant = Instant.now();
		return instant.atZone(ZoneOffset.UTC).getHour();
	}

	@ThingworxServiceDefinition(name = "GetUTCMinute", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public int GetUTCMinute() {

		Instant instant = Instant.now();
		return instant.atZone(ZoneOffset.UTC).getMinute();
	}

	@ThingworxServiceDefinition(name = "GetUTCSecond", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public int GetUTCSecond() {

		Instant instant = Instant.now();
		return instant.atZone(ZoneOffset.UTC).getSecond();
	}

	@ThingworxServiceDefinition(name = "GetUTCDayOfWeek", description = "", category = "UTC", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {
			"dataShape:KBConnectionInfoDataShape" })
	public int GetUTCDayOfWeek() throws Exception {

		Instant instant = Instant.now();
		DayOfWeek dayOfWeek = instant.atZone(ZoneOffset.UTC).getDayOfWeek();

		if (dayOfWeek == DayOfWeek.MONDAY) {
			return 0;
		} else if (dayOfWeek == DayOfWeek.TUESDAY) {
			return 1;
		} else if (dayOfWeek == DayOfWeek.WEDNESDAY) {
			return 2;
		} else if (dayOfWeek == DayOfWeek.THURSDAY) {
			return 3;
		} else if (dayOfWeek == DayOfWeek.FRIDAY) {
			return 4;
		} else if (dayOfWeek == DayOfWeek.SATURDAY) {
			return 5;
		} else if (dayOfWeek == DayOfWeek.SUNDAY) {
			return 6;
		} else {
			throw new Exception("No Weekday");
		}
	}

	private JSONObject GetPlatformSettings() throws JSONException {
		String platformSettings = System.getenv("THINGWORX_PLATFORM_SETTINGS");
		String settings = null;
		if (platformSettings == null) {
			platformSettings = "C:\\ThingworxPlatform";
		}
		if (!platformSettings.endsWith("\\")) {
			platformSettings += "\\";
		}
		platformSettings += "platform-settings.json";

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(platformSettings));
			StringBuilder sb = new StringBuilder();
			String line;
			line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			settings = sb.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error GetThingWorxExternalDatabaseSettings (FileNotFoundException): " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error GetThingWorxExternalDatabaseSettings (IOException): " + e.toString());
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(
							"Error GetThingWorxExternalDatabaseSettings (IOException during close): " + e.toString());
					e.printStackTrace();
				}
			}
		}

		return new JSONObject(settings);
	}

	@ThingworxServiceDefinition(name = "WriteSnowflakeKeyFile", description = "", category = "System", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String WriteSnowflakeKeyFile() {
		String result = "Started...";
		try {
			String path = "C:\\KBTWX";
			File pathAsFile = new File(path);

			if (!Files.exists(Paths.get(path))) {
				pathAsFile.mkdir();
			}
			String file = path + "\\rsa_key.p8";
			File myObj = new File(file);
			myObj.createNewFile();
			result = "File created: " + file;
			FileWriter fw = new FileWriter(myObj);

			fw.write("-----BEGIN PRIVATE KEY-----" + System.getProperty("line.separator"));
			fw.write("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDJG0VuFDf7mykw" + System.getProperty("line.separator"));
			fw.write("LH38JUv64qJfOPlOvnK1c0ySzkmlZ7yK4+WEP1P9p7AE7kVx3atZievvF56l/SO3" + System.getProperty("line.separator"));
			fw.write("TJjCWhVDXQ/AjXmGe/LofvC/o+HLmL7VdydAUnH1N02aNBGfEznlIrs6TbBTkEQp" + System.getProperty("line.separator"));
			fw.write("yN0dUsXMZ3uvau+dePK880zEReumS6vn8vlWBfo5vVHhb7FHDZklrlH8sK6hPpru" + System.getProperty("line.separator"));
			fw.write("UDLE6RcdIF34nuKFVuX0Yx4KAn+Fc43LEpvGWr/k9OSYf+6HX+MQBaiBzwKYxSge" + System.getProperty("line.separator"));
			fw.write("a265iu3wzDaQH5J6wvPaM94umPNFNO4U3v8l5lZskusFydLqBLUjwxI9f2BdGC/2" + System.getProperty("line.separator"));
			fw.write("or0Ni0v3AgMBAAECggEAA4ugs+ScIvFduVv5ONlFewiL7pwpzej5hC2GByBRybrH" + System.getProperty("line.separator"));
			fw.write("2MSNrCSUj4u+qCwKiUTBrYe1qkh4fCiNCuvz1UOLosDkT5cdJBTDN+3B31qZyNsm" + System.getProperty("line.separator"));
			fw.write("r+e7sgc3ZlMm4etaFGZdNKb+YFfREAq/AqshC0IFez7gdxD1gXsmOGKrPYUIDFgs" + System.getProperty("line.separator"));
			fw.write("n4EOEawopHQ32vO11fXF/CDllVXBzRahHEmkLBYZgIT+eiYBmn2HhKzlAQPSI/GI" + System.getProperty("line.separator"));
			fw.write("lF2xHOakwb1NO209V8pLUslO5u34193+giERTo5JfTiJuAl+uREvsumbvkobGYtj" + System.getProperty("line.separator"));
			fw.write("6IK/4Z8Hl12LECjG17GvlHaWLNxPI3a4LF2qCsOQjQKBgQDh8RP2rv4beftNtnPi" + System.getProperty("line.separator"));
			fw.write("cppEzx3ztTwmb5iFFer5FJL8Cpvzi9/YgolS1U24E2weTUZGmjDJEZykBBVUzoIJ" + System.getProperty("line.separator"));
			fw.write("U5M8KPIyI/zL2WBgDGK50l3d5ZT/CthEKuNk1ecKFmial7ao9vCsVhMB0FkP+PVZ" + System.getProperty("line.separator"));
			fw.write("ZTTe61LstRn0ARaRJaJsKkjKowKBgQDj3GDu0hzWVDqIA4q3+GtQ3S6PPnrj9VXh" + System.getProperty("line.separator"));
			fw.write("y21PKupUP381pas9KFRsSfbnqd1I6UWFy2d/CuniItCATWbPixrN4ZTay8XISyE+" + System.getProperty("line.separator"));
			fw.write("EPiL0tg3QpC7fHEy7y2Kr8O0E1kETMerZML4BW2oDgWX22sXycA6rpJycRXkWmm3" + System.getProperty("line.separator"));
			fw.write("Pn42hkNCnQKBgQCpvypFeeE1GWbhUsMrjPgz2/tfvniLU+odioL3USrAquPRjPPG" + System.getProperty("line.separator"));
			fw.write("wQOUtOZn9keTWHEE2BDw7wdk/iBcj62Ifj6uwwB7BSiGqjDK2GNPwil9bV+MYURr" + System.getProperty("line.separator"));
			fw.write("qmEev8mNfZpnErZLqQLWJvCuuy4+XsdNu0iGdfveFI2EEVE4CTNG2lnHywKBgQDd" + System.getProperty("line.separator"));
			fw.write("WivsNL8A0sgdc1thxnjyEDv3uZmGM8GfXJAutSD4gMqiIfNMsQ36OIwblp86Aiqg" + System.getProperty("line.separator"));
			fw.write("g+1htAdqv6lOymJSwe5jDH4fYo8bOSjs822P315dqFOTXptVAJZ8QKzcFWOHUtMx" + System.getProperty("line.separator"));
			fw.write("Q+xHJnbVJ/H1CJlrfXk5SHdJp2O99rr14UaYzyst3QKBgC/kjHRUh+j7nkGyGvgG" + System.getProperty("line.separator"));
			fw.write("3NpZ/vzPE/L3IzCsn3AlWpj7qzR2CrIWS1a33ZxmL1IG+qQJmUGk6uX9Cs7T5Xr8" + System.getProperty("line.separator"));
			fw.write("lE2vLyxryREZg9fhRu8lXLz0QNV6bqZBW8YjNj8jDsXcY1nRWbMREC+w5UtqRZzV" + System.getProperty("line.separator"));
			fw.write("DGHTeXHu0BtKZ1QSUC+i59UL" + System.getProperty("line.separator"));
			fw.write("-----END PRIVATE KEY-----" + System.getProperty("line.separator"));

			fw.close();
		} catch (IOException e) {
			result = "An error occurred: " + e.toString();
		}

		return result;
	}
	
	@ThingworxServiceDefinition(name = "ProcessLogFiles", description = "Process log files within the specified date range", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = {"dataShape:dataShapeDef"})
    public Object ProcessLogFiles(
            @ThingworxServiceParameter(name = "StartDate", description = "Start date", baseType = "DATETIME", aspects = {"isRequired:true"}) DateTime  startDate,
            @ThingworxServiceParameter(name = "EndDate", description = "End date", baseType = "DATETIME", aspects = {"isRequired:true"}) DateTime  endDate,
            @ThingworxServiceParameter(name = "DirectoryPath", description = "Tomcat log directory name: hint Tomcat 9.0/logs", baseType = "STRING", aspects = {"isRequired:true"}) String  directoryPath) throws Exception {
        
            // Process log files within the specified date range
            return StringOccurrencesCounterService.program(startDate, endDate, directoryPath);
    	}

}
