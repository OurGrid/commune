package br.edu.ufcg.lsd.commune.systemtest.fd;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.commune.context.DefaultContextFactory;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesParser;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProperties;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.systemtest.SystemTestModule;

public class FailureDetectorTestInitializer {

	public static void main(String[] args) throws Exception {
		createModule("MODULE_A", "commune_test_user");
	}
	
	public static SystemTestModule createModule(String container, String user) throws Exception {
		return new SystemTestModule(container, createModuleContext(user));
	}
	
	private static ModuleContext createModuleContext(String username) {
		Map<String, String> properties = new HashMap<String,String>();
		properties.put(XMPPProperties.PROP_USERNAME, username);
		properties.put(XMPPProperties.PROP_XMPP_SERVERNAME, "xmpp.ourgrid.org");
		properties.put(XMPPProperties.PROP_PASSWORD, "password");
		properties.put(CertificationProperties.PROP_CERT_PROVIDER_CLASS, 
				FileCertificationDataProvider.class.getName());
		properties.put(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
				"data" + File.separator + "certification" + File.separator + "testLSD.cer");
		
		DefaultContextFactory factory = new DefaultContextFactory(new PropertiesParser(properties));
		return factory.createContext();
	}
	
}
