package br.edu.ufcg.lsd.commune.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.context.DefaultContextFactory;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesParser;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProperties;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;

public class Util {

	public static  Module createModule(String container, String user, String server) throws Exception {
		return new Module(container, createModuleContext(user, server));
	}
	
	public static ModuleContext createModuleContext(String username, String server) {
		Map<String, String> properties = new HashMap<String,String>();
		properties.put(XMPPProperties.PROP_USERNAME, username);
		properties.put(XMPPProperties.PROP_XMPP_SERVERNAME, server);
		properties.put(CertificationProperties.PROP_CERT_PROVIDER_CLASS, 
				FileCertificationDataProvider.class.getName());
		properties.put(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
				"data/certification" + File.separator + "testLSD.cer");
		
		DefaultContextFactory factory = new DefaultContextFactory(new PropertiesParser(properties));
		return factory.createContext();
	}


	public static void log(String msg) {
//		System.err.println(System.currentTimeMillis() + "\t" + msg);
	}
	
	public static Map<Integer, String> parseExperimentProperties() {
		String fileName = "experiment.properties";
		
		Properties properties = new Properties();
		File propertiesFile = new File( fileName );

		if (!propertiesFile.exists()) {
			throw new RuntimeException(fileName + " does not exists");
		}
		
		/* load the properties file, if it exists */
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream( propertiesFile );
		} catch (FileNotFoundException e) {
			throw new CommuneRuntimeException("Properties couldn't be loaded. " + fileName + 
					" does not exist. Please check this file. Exception: " + e.getMessage());
		}
		try {
			properties.load( fileInputStream );
		} catch ( IOException e ) {
			throw new CommuneRuntimeException("Properties couldn't be loaded. " + fileName + 
					" is corrupted. Please check this file. Exception: " + e.getMessage());
		} catch (IllegalArgumentException iae ) {
			throw new CommuneRuntimeException("Properties couldn't be loaded. " + fileName + 
					" isn't a properties file. Please use correct file format.");
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				throw new CommuneRuntimeException("Properties couldn't be loaded. " + fileName + 
						" is corrupeed. Please check this file. Exception: " + e.getMessage());
			}
		}
		
		Map<Integer, String> responseMap = new LinkedHashMap<Integer, String>();
		
		for (Object key : properties.keySet()) {
			if (!properties.get(key).toString().trim().equals("")) {
				responseMap.put(new Integer((String)key), (String)properties.get(key));
			}
		}
		
		return responseMap;
	}
}