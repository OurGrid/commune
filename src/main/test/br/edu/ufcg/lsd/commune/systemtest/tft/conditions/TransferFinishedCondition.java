package br.edu.ufcg.lsd.commune.systemtest.tft.conditions;

import java.util.List;

import br.edu.ufcg.lsd.commune.systemtest.Condition;
import br.edu.ufcg.lsd.commune.systemtest.tft.TransferDestinationReceiver;

public class TransferFinishedCondition implements Condition<List<TransferDestinationReceiver>> {

	private final int transfersToFinish;

	public TransferFinishedCondition(int transfersToFinish) {
		this.transfersToFinish = transfersToFinish;
	}
	
	public boolean test(List<TransferDestinationReceiver> tdrs) {
		boolean b = true;
		for (TransferDestinationReceiver tdr : tdrs) {
			b &= (tdr.transfersFinished == transfersToFinish && tdr.transferCompleted);
		}
		return b;
	}

}
