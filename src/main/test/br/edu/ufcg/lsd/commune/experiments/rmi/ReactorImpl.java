package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

import br.edu.ufcg.lsd.commune.experiments.Util;

public class ReactorImpl implements Reactor, Serializable {

	
	private static final long serialVersionUID = 1L;
	
	
	public String ping() throws RemoteException{
		Util.log("ping()");
		return "pong";
	}
}