package model;

import java.io.IOException;
import javax.swing.JOptionPane;
import util.OpenBinaryFiles;

public class TinyGraph implements Graph{

	private float[][] grafo;
	
	public TinyGraph(String f_corr, int numGeni){
		try {
			grafo = OpenBinaryFiles.openTriangular(f_corr, numGeni);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	public TinyGraph(float[][] g){
		int nr = g.length;
		grafo  = new float[nr][];
		for(int j=0; j<nr; j++)
			grafo[j] = new float[j+1];
		for(int i=0; i<g.length; i++){
			for(int j=0; j<g[i].length; j++){
				grafo[i][j]=g[i][j];
			}
		}
	}
	
	public int getNumNodi(){
		return grafo.length;
	}
	
//	public long checkSize() throws IOException{
//		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		 ObjectOutputStream oos = new ObjectOutputStream(baos);
//		 oos.writeObject(grafo);
//		 oos.close();
//		 return baos.size();
//	}

	public void printGraph(){
		System.out.println("MATRICE DELLE CORRELAZIONI:");
		for(int i=0; i<grafo.length; i++){
			for(int j=0; j<grafo[i].length; j++){
				System.out.print(grafo[i][j]+"\t");
			}
			System.out.println("\n");
		}
	}
	
	public void printGraph_inLine() {
		System.out.println("TODO");
	}
	
	public float getCoorelation(int r, int c){
		return grafo[r][c];
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
		TinyGraph g = new TinyGraph(file, ng);
		g.printGraph();
	}

}
