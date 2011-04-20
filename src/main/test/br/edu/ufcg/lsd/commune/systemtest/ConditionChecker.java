package br.edu.ufcg.lsd.commune.systemtest;

public class ConditionChecker<T> {

	private final T t;
	private final Condition<T> condition;

	public ConditionChecker(T t, Condition<T> condition) {
		this.t = t;
		this.condition = condition;
	}
	
	public boolean waitUntilCondition(int delay, int times) throws Exception {
		int i = 0;
		
		while (true) {
			i++;
			
			if (i > times) {
				break;
			}
			
			if (condition.test(t)) {
				return true;
			}
			
			Thread.sleep(delay);
		}
		
		return false;
	}
	
	public boolean doNotOccurs(int delay, int times) throws Exception {
		int i = 0;
		
		while (true) {
			i++;
			
			if (i > times) {
				break;
			}
			
			if (condition.test(t)) {
				return false;
			}

			Thread.sleep(delay);
		}
		
		return true;
	}
	
}
