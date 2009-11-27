package br.edu.ufcg.lsd.commune.experiments;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
}
