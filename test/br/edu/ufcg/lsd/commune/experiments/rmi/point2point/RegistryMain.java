package br.edu.ufcg.lsd.commune.experiments.rmi.point2point;

import java.rmi.Naming;

public class RegistryMain {

	public static void main(String[] args) throws Exception {
	
		Naming.rebind(Registry.REGISTRY_SERVERNAME + Registry.REGISTRY_SERVICE, new RegistryImpl());
		
	}
}