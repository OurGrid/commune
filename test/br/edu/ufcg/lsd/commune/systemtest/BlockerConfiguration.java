package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.message.Message;

public class BlockerConfiguration {

	private String functionName;
	private int sequenceNumber = -1;
	
	
	public BlockerConfiguration(String functionName, int sequenceNumber) {
		super();
		this.functionName = functionName;
		this.sequenceNumber = sequenceNumber;
	}
	
	
	public String getFunctionName() {
		return functionName;
	}
	
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public boolean block(Message message) {
		boolean function = true;
		boolean sequence = true;
		
		if (functionName != null && !functionName.equals(message.getFunctionName())) {
			function = false;
		}
		
		if (sequenceNumber > 0 && !(sequenceNumber == message.getSequence())) {
			sequence = false;
		}
		
		return function && sequence;
	}
}