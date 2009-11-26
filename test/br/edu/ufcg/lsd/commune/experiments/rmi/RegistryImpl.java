package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.RemoteException;

import br.edu.ufcg.lsd.commune.experiments.Util;

public class RegistryImpl implements Registry {

	
	public void getPeerList() throws RemoteException{
		Util.log("registry->getPeerList()");
	}
}