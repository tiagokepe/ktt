package ufpr.inf.kepe;

public enum AlgorithmType {
	Bacteriological("BA"), Genetic("GA");
	
	private String algName;
	public String getAlgName() {
		return algName;
	}

	private AlgorithmType(String name) {
		algName = name;
	}
}
