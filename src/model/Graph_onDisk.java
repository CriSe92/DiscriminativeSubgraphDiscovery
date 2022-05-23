package model;

import java.io.File;
import java.io.IOException;
import util.MatrixHelper;
import util.NumberHelper;
import util.OpenBinaryFiles;

public class Graph_onDisk implements Graph{
	
	String graph;
	int id;
	int numGeni;
	int numMaxArchi;
	//Sparse vector
	
	public Graph_onDisk(String f_corr, int numGeni, int id){
		this.id=id;//L'ID identifica il campione e perciò la riga da leggere sulla matrice
		this.numGeni=numGeni;
		this.numMaxArchi = numGeni*(numGeni-1)/2;
		graph = f_corr;
		if(!(new File(graph)).exists()) throw new IllegalArgumentException("File inesistente!");
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

	@Override
	public float getCoorelation(int r, int c) {
		int index=MatrixHelper.getIndex(r, c, numGeni);
		short val=0;
		try {
			val = OpenBinaryFiles.getValue(graph, id, index, numMaxArchi);
		} catch (IOException e) {
			System.out.println("ERRORE DURANTE LA LETTURA DEL FILE");
			System.exit(0);
			e.printStackTrace();
		}
		return NumberHelper.toFloat(val);
	}
	
	public void printGraph_inLine() {
		System.out.println("TODO");
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
		Graph_onDisk g = new Graph_onDisk(path, 10,6);
		g.printGraph();
	}

}
