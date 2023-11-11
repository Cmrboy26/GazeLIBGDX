package net.cmr.gaze.debug;

public class RateCalculator {

	double totalCount;
	double[] variables;
	long[] millis;
	int oldestTimeIndex = 0;
	int index = 0;
	boolean cycled;
	
	public RateCalculator(int maxVariables) {
		if(maxVariables > 200) {
			throw new IndexOutOfBoundsException();
		}
		variables = new double[maxVariables];
		millis = new long[maxVariables];
	}
	public RateCalculator() {
		variables = new double[20];
		millis = new long[20];
	}
	
	public double ratePerSecond() {
		long elapsedMillis = System.currentTimeMillis()-millis[oldestTimeIndex];
		double elapsedSeconds = elapsedMillis/1000d;
		return totalCount/elapsedSeconds;
	}
	
	public void add(double number, long now) {
		double overrideNumber = variables[index];
		totalCount -= overrideNumber;
		
		if(cycled) {
			oldestTimeIndex = Math.floorMod(index+1, variables.length);
		}
		
		millis[index] = now;
		variables[index] = number;
		totalCount+=number;
		
		index++;
		if(index!=Math.floorMod(index, variables.length)) {
			cycled = true;
			index = 0;
		}
	}
	
}
