package model;

import java.io.IOException;
import java.util.Set;

import javax.swing.JOptionPane;

import application.ExperimentInfo;
import util.BinaryVector;
import util.OpenBinaryFiles;
import util.SparseVector;

public class Graph_sparseVector implements Graph {
	
	SparseVector graph;
	int id;
	//int numGeni;
	//Sparse vector
	
	public Graph_sparseVector(String f_corr, int id, char sample_type){
		this.id=id;//L'ID identifica il campione e perciò la riga da leggere sulla matrice
		//this.numGeni=numGeni;
		try {
			graph = OpenBinaryFiles.openShortVector2SparseVector(f_corr, id, sample_type);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(0);
		}
	}
	
	public Graph_sparseVector(String f_corr, int id, char sample_type, int[] colsToRead){
		this.id=id;//L'ID identifica il campione e perciò la riga da leggere sulla matrice
		//this.numGeni=numGeni;
		try {
			if(colsToRead==null){graph = OpenBinaryFiles.openShortVector2SparseVector(f_corr, id,sample_type);}
			else{graph = OpenBinaryFiles.openShortVector2SparseVector(f_corr, id,colsToRead);}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(0);
		}
	}
	
	public Graph_sparseVector(String f_corr, int id, char sample_type, BinaryVector colsToRead){
		this.id=id;//L'ID identifica il campione e perciò la riga da leggere sulla matrice
		//this.numGeni=numGeni;
		try {
			if(colsToRead==null){graph = OpenBinaryFiles.openShortVector2SparseVector(f_corr, id, sample_type);}
			else{graph = OpenBinaryFiles.openShortVector2SparseVector(f_corr, id,colsToRead);}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(0);
		}
	}

	@Override
	public int getNumNodi() {
		return ExperimentInfo.numNodi;
	}
	
	public int getNumArchi(){
		return graph.getKeys().size();
	}
	
	public void printEdgesIndex(){
		Set<Integer> set = graph.getKeys();
		for(Integer i: set){
			System.out.print(i+" ");
		}
		System.out.println();
	}

	@Override
	public void printGraph() {
		for(int i=0; i<ExperimentInfo.numNodi; i++){
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
		for(int i=0; i<ExperimentInfo.numNodi; i++){
			for(int j=0; j<=i; j++){
				if(i!=j) System.out.printf("%5.4f\t",getCoorelation(i, j));
			}
		}
		System.out.println("SIZE: "+graph.size());
	}

	@Override
	public float getCoorelation(int r, int c) {
		int index  = (r-1)*r/2+c;
		return getCoorelation(index);
		//throw new UnsupportedOperationException();
	}
	
	public float getCoorelation(int index) {
		return graph.get(index);
	}

	@Override
	public float getProbability(int r, int c) {
		return 1;
	}
}
