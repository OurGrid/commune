package br.edu.ufcg.lsd.commune.experiments.commune;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.experiments.Util;

public class RegistryMain {

	public static void main(String[] args) throws Exception {
	
		String container = Registry.REGISTRY_CONTAINER;
		String user = Registry.REGISTRY_USERNAME;
		String server = Registry.REGISTRY_SERVERNAME;
		
		Module module = Util.createModule(container, user, server);
		module.getContainer().deploy(Registry.REGISTRY_SERVICE, new RegistryImpl());
	}
}