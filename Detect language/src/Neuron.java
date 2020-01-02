package src;


public class Neuron {
	
	double[] vector;
	String attribute;
	
	public Neuron(double[] vector,String attribute) {
		this.vector = vector;
		this.attribute = attribute;
	}
	
	public String toString() {
		
		String dataVector = "[";
		for(int i=0; i < vector.length; i++) {
			dataVector += vector[i] + ",";
		}
		dataVector = dataVector.substring(0, dataVector.length()-1)+"]";
		return dataVector + " " + attribute;
	}
}
