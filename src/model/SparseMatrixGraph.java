package model;

import java.io.IOException;
import javax.swing.JOptionPane;
import util.OpenBinaryFiles;
import util.SparseTriangularMatrix;

public class SparseMatrixGraph implements Graph {
	
	private SparseTriangularMatrix grafo;
	private int numNodi;
	
	public SparseMatrixGraph(String f_corr, int numGeni){
		try {
			float[][] g = OpenBinaryFiles.openTriangular(f_corr, numGeni);
			numNodi=numGeni;
			createMatrix(g);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
	}
	
	private void createMatrix(float[][] g){
		grafo = new SparseTriangularMatrix(g.length);
		for(int i=0; i<g.length; i++){
			for(int j=0; j<g[i].length; j++){
				if(g[i][j]!=0 && i!=j) grafo.put(i, j, g[i][j]);
			}
		}
	}
	
	public int getNumNodi(){
		return numNodi;
	}

	public void printGraph(){
		System.out.println("MATRICE DELLE CORRELAZIONI:");
		grafo.print();
	}
	
	public void printGraph_inLine() {
		System.out.println("TODO");
	}
	
	public float getCoorelation(int r, int c){
		return grafo.get(r,c);
	}
	
	@Override
	public float getCoorelation(int edgeIndex) {
		throw new UnsupportedOperationException();
	}
	
	public float getProbability(int r, int c){
		return 1;
	}	
	
	public static void main(String[] args) throws IOException {
		String file = "/home/cristina/DiscriminatingSubgraphDiscovery/software/patternMining_workspace/MiningDiscriminativePatterns/presentationTest/H/corr0_H.ds2";
		int ng = 4;
		SparseMatrixGraph g = new SparseMatrixGraph(file, ng);
		g.printGraph();
	}


}
