package model;

public interface Graph {
	
	public int getNumNodi();
	
	public void printGraph();
	
	public void printGraph_inLine();
	
	public float getCoorelation(int r, int c);
	
	public float getCoorelation(int edgeIndex);
	
	public float getProbability(int r, int c);
	
}
