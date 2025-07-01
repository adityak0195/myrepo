package com.kb;

import javax.net.ssl.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class KBCertCreator {

    public static String createCertContainer() {
    	String log = "java.home: "+System.getProperty("java.home")+"\r\n";
    	log += "THINGWORX_PLATFORM_SETTINGS: "+System.getenv("THINGWORX_PLATFORM_SETTINGS")+"\r\n";
    	log += "TEMP: "+System.getenv("TEMP")+"\r\n";
    	//MUC
    	log += installCert("smartproduction.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductiontest.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductiondev.corp.knorr-bremse.com", 443);
    	log += installCert("thingworx.corp.knorr-bremse.com", 443);

    	//LIS
    	log += installCert("smartproductionlis.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionlistest.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionlisdev.corp.knorr-bremse.com", 443);

    	//LIB
    	log += installCert("smartproductionlib.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionlibtest.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionlibdev.corp.knorr-bremse.com", 443);

    	//ALD
    	log += installCert("smartproductionald.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionaldtest.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionalddev.corp.knorr-bremse.com", 443);

    	//BWG
    	log += installCert("smartproductionbwg.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionbwgtest.corp.knorr-bremse.com", 443);

    	//KEC
    	log += installCert("smartproductionkec.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionkectest.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionkecdev.corp.knorr-bremse.com", 443);

    	//BER
    	log += installCert("smartproductionber.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionbertest.corp.knorr-bremse.com", 443);

    	//MIL
    	log += installCert("smartproductionmil.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionmiltest.corp.knorr-bremse.com", 443);

    	//BUD
    	log += installCert("smartproductionbud.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionbudtest.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionbuddev.corp.knorr-bremse.com", 443);

    	//BRQ
    	log += installCert("smartproductionbrq.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionbrqtest.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionbrqdev.corp.knorr-bremse.com", 443);

    	//MLK
    	log += installCert("smartproductionmlk.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionmlktest.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionmlkdev.corp.knorr-bremse.com", 443);

    	//PNA
    	log += installCert("smartproductionpna.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionpnatest.corp.knorr-bremse.com", 443);

    	//KBA
    	log += installCert("smartproductionkba.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionkbatest.corp.knorr-bremse.com", 443);

    	//KBB
    	log += installCert("smartproductionkbb.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionkbbtest.corp.knorr-bremse.com", 443);
		
		//ART
    	log += installCert("smartproductionart.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionarttest.corp.knorr-bremse.com", 443);
    	
    	//PUN
    	log += installCert("smartproductionpun.corp.knorr-bremse.com", 443);
    	log += installCert("smartproductionpuntest.corp.knorr-bremse.com", 443);

    	//SMP (Small Plants)
    	log += installCert("smartproductionsmp.corp.knorr-bremse.com", 443);
    	
    	log += installCert("mucs70389.corp.knorr-bremse.com", 443);
    	log += installCert("mucs78114.corp.knorr-bremse.com", 50201);
    	
    	//APO
    	log += installCert("pidci000.corp.knorr-bremse.com", 50201);
    	log += installCert("piqci000.corp.knorr-bremse.com", 50201);
    	log += installCert("pipci000.corp.knorr-bremse.com", 50201);
		log += installCert("podjv000.corp.knorr-bremse.com", 50201);
    	log += installCert("poqjv000.corp.knorr-bremse.com", 50201);
    	log += installCert("popjv000.corp.knorr-bremse.com", 50201);

    	//Config
    	log += installCert("kmpweb.corp.knorr-bremse.com",443);
    	log += installCert("kmqweb.corp.knorr-bremse.com",443);
    	log += installCert("kmdweb.corp.knorr-bremse.com",443);
    	log += installCert("km1ci000.corp.knorr-bremse.com",443);
    	
    	
    	//R3
    	log += installCert("kmpci000.corp.knorr-bremse.com", 8443);
    	log += installCert("kmqci000.corp.knorr-bremse.com", 8443);
    	log += installCert("kmdci000.corp.knorr-bremse.com", 8443);
    	
    	//Windchill
    	log += installCert("windchill01.corp.knorr-bremse.com", 443);
    	log += installCert("windchill00node01.corp.knorr-bremse.com", 443);
    	log += installCert("windchill01node01.corp.knorr-bremse.com", 443);
    	log += installCert("windchill02node01.corp.knorr-bremse.com", 443);
    	log += installCert("wtquality.corp.knorr-bremse.com", 443);
	log += installCert("wtint01.corp.knorr-bremse.com", 443);
    	log += installCert("wtint03.corp.knorr-bremse.com", 443);
    	log += installCert("wtdemo.corp.knorr-bremse.com", 443);
    	log += installCert("wtupg02.corp.knorr-bremse.com", 1443);
    	log += installCert("wtupg03.corp.knorr-bremse.com", 443);
	log += installCert("wt13upg01.corp.knorr-bremse.com", 443);
	log += installCert("wt13upg02.corp.knorr-bremse.com", 443);

    	//Snowflake
    	log += installCert("dz96977.west-europe.azure.snowflakecomputing.com", 443);

    	//Azure Functions
    	log += installCert("smartproductiondataconnectordev.azurewebsites.net", 443);
    	log += installCert("smartproductiondataconnectortest.azurewebsites.net", 443);
    	log += installCert("smartproductiondataconnector.azurewebsites.net", 443);

    	log += copyJssecacerts2cacerts();
    	
    	return log;
    }
    
    public static String copyJssecacerts2cacerts(){
    	
		File source = new File(getFullOutputFileName("jssecacerts"));
		File target = new File(getFullOutputFileName("cacerts"));
		try (InputStream in = new BufferedInputStream(new FileInputStream(source));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {

			byte[] buffer = new byte[1024];
			int lengthRead;
			while ((lengthRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, lengthRead);
				out.flush();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return "\r\nFiles created: "+source.getAbsolutePath()+", "+target.getAbsolutePath();
		
    }

    public static String getFullOutputFileName(String file){
    	
        char SEP = File.separatorChar;
        String TWX = System.getenv("THINGWORX_PLATFORM_SETTINGS");
        String TEMP = System.getenv("TMP");
        String outputFile;
        
        if (TWX == null) {
        	outputFile = TEMP + SEP+file;
        } else {
        	outputFile = TWX + SEP+file;
        }
        
        return outputFile;
    }
    
    public static String installCert(String host, int port){
    	String log = "";
    	try {
	        log = "*******************************************************************************";
	        log += "\r\nStart installCert, host: "+host+", port: "+port;
            char SEP = File.separatorChar;
	        char[] passphrase = "changeit".toCharArray();

	        String outputFile = getFullOutputFileName("jssecacerts");
	        
	        /*
	        if ((args.length == 1) || (args.length == 2)) {
	            String[] c = args[0].split(":");
	            host = c[0];
	            port = (c.length == 1) ? 443 : Integer.parseInt(c[1]);
	            String p = (args.length == 1) ? "changeit" : args[1];
	            passphrase = p.toCharArray();
	        } else {
	            log += "\r\nUsage: java InstallCert [:port] [passphrase]";
	            return;
	        }
	        */
	
	        File file = new File(outputFile);
	        if (file.isFile() == false) {
	            File dir = new File(System.getProperty("java.home") + SEP
	                    + "lib" + SEP + "security");
	            file = new File(dir, "jssecacerts");
	            if (file.isFile() == false) {
	                file = new File(dir, "cacerts");
	            }
	        }
	        log += "\r\nLoading KeyStore " + file + "...";
	        InputStream in = new FileInputStream(file);
	        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
	        ks.load(in, passphrase);
	        in.close();
	
	        SSLContext context = SSLContext.getInstance("TLS");
	        TrustManagerFactory tmf =
	                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	        tmf.init(ks);
	        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
	        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
	        context.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory factory = context.getSocketFactory();
	
	        log += "\r\nOpening connection to " + host + ":" + port + "...";
	        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
	        socket.setSoTimeout(10000);
	        try {
	            log += "\r\nStarting SSL handshake...";
	            socket.startHandshake();
	            socket.close();
	            log += "\r\nNo errors, certificate is already trusted";
	        } catch (SSLException e) {
	            log += "\r\nSSL Error, certificate to be added";
	            //e.printStackTrace(System.out);

	        
		        X509Certificate[] chain = tm.chain;
		        if (chain == null) {
		            log += "\r\nCould not obtain server certificate chain";
		            return log;
		        }
		
		        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		        log += "\r\nServer sent " + chain.length + " certificate(s):";
		        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		        MessageDigest md5 = MessageDigest.getInstance("MD5");
		        for (int i = 0; i < chain.length; i++) {
		            X509Certificate cert = chain[i];
		            //log += ("\r\n " + (i + 1) + " Subject " + cert.getSubjectDN();
		            //log += "\r\n   Issuer  " + cert.getIssuerDN();
		            sha1.update(cert.getEncoded());
		            //log += "   sha1    " + toHexString(sha1.digest()));
		            md5.update(cert.getEncoded());
		            //log += "\r\n   md5     " + toHexString(md5.digest());
		        }
		
		        log += "\r\nAdd to trusted keystore";
		        //String line = reader.readLine().trim();
		        int k = 0;
		        /*
		        try {
		            k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
		        } catch (NumberFormatException e) {
		            log += "\r\nKeyStore not changed";
		            return;
		        }
		*/
		        X509Certificate cert = chain[k];
		        String alias = host + "-" + (k + 1);
		        ks.setCertificateEntry(alias, cert);
		
		        OutputStream out = new FileOutputStream(outputFile);
		        ks.store(out, passphrase);
		        out.close();
		
		        //log += "\r\n"+cert;
		        log += "\r\nAdded certificate to keystore '"+outputFile+"' using alias '"+ alias + "'";
	        }
	        log += "\r\nEnd installCert, host: "+host+", port: "+port;
	        log += "\r\n*******************************************************************************\r\n";
	        
    	} catch (Exception e) {
	        log += "\r\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\r\n";
	        log += e;
	        log += "\r\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\r\n";
    	}
    	return log;
    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }

}
