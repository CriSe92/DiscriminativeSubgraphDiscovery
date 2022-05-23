package model;

import java.io.IOException;
import javax.swing.JOptionPane;
import util.MatrixHelper;
import util.NumberHelper;
import util.OpenBinaryFiles;

public class Graph_ShortArray implements Graph {
	
	short[] graph;
	int id;
	int numGeni;
	//Sparse vector
	
	public Graph_ShortArray(String f_corr, int numGeni, int id){
		this.id=id;//L'ID identifica il campione e perciò la riga da leggere sulla matrice
		this.numGeni=numGeni;
		try {
			graph = OpenBinaryFiles.openShortVector(f_corr, id);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(0);
		}
	}
	
	@Override
	public int getNumNodi() {
		return numGeni;
	}

	@Override
	public void printGraph() {
		for(int i=0; i<numGeni; i++){
			for(int j=0; j<=i; j++){
				if(i==j) System.out.printf("%5.4f\t",1.0);
				else{
					System.out.printf("%5.4f\t",getCoorelation(i, j));
				}
			}
			System.out.println();
		}
	}
	
	public void printGraph_inLine() {
		System.out.println("TODO");
	}

	@Override
	public float getCoorelation(int r, int c) {
		int index=MatrixHelper.getIndex(r, c, numGeni);
		return NumberHelper.toFloat(graph[index]);
	}
	
	@Override
	public float getCoorelation(int edgeIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getProbability(int r, int c) {
		return 1;
	}

	public static void main(String[] args) {
		String path = "/home/cristina/DiscriminatingSubgraphDiscovery/software/dataset/test_tesi/10random/10random_corrH.ds2";
		Graph_ShortArray g = new Graph_ShortArray(path, 10,0);
		g.printGraph();
	}
}
