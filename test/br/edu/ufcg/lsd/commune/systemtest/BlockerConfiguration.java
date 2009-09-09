package br.edu.ufcg.lsd.commune.systemtest;

public class BlockerConfiguration {

	private String functionName;
	private int sequenceNumber;
	
	
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
}