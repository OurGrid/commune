package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.message.Message;

public class BlockerConfiguration {

	
	public static final Long DO_NOT_BLOCK_SEQUENCE = -1L;
	public static final String DO_NOT_BLOCK_FUNCTION = null;
	
	
	private String functionName;
	private Long sequenceNumber = DO_NOT_BLOCK_SEQUENCE;
	
	
	public BlockerConfiguration(){}
	
	public BlockerConfiguration(String functionName, Long sequenceNumber) {
		this.functionName = functionName;
		this.sequenceNumber = sequenceNumber;
	}
	
	
	public String getFunctionName() {
		return functionName;
	}
	
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	public Long getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public boolean block(Message message) {

		if (functionName == DO_NOT_BLOCK_FUNCTION && sequenceNumber == DO_NOT_BLOCK_SEQUENCE) {
			return false;
		}
		
		if (functionName != DO_NOT_BLOCK_FUNCTION && !functionName.equals(message.getFunctionName())) {
			return false;
		}
		
		if (sequenceNumber != DO_NOT_BLOCK_SEQUENCE && sequenceNumber != message.getSequence()) {
			return false;
		}
		
		return true;
	}
}