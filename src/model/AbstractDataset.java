package model;

import java.util.ArrayList;


public abstract class AbstractDataset {
	
	public int nEdges=0;
	
	/*
	 * Un arco è rappresentato da una coppia di interi che individuano i nodi che esso collega.
	 * Effettuare il mapping degli archi consente di identificare univocamente con un numero ciascuno degli archi.
	 * Considero un'unica tabella per il mapping che contiene tutti i possibili archi e rappresento questa tabella come una matrice. Ciascuna colonna contiene una coppia di numeri (uno sulla riga 0 e l'altro sulla riga 1)
	 * che rappresentano i nodi connessi dall'arco; l'indice di colonna è l'ID dell'arco.
	 * 
	 * Quando si lavorerà con le due popolazioni è possibile usare una sola tabella per mappare gli archi che sarà
	 * trattata come una variabile avente rilevanza di classe poichè deve essere condivisa da entrambi gli oggetti che si
	 * istanzieranno per la gestione dei due dataset
	 */
	public static short[][] edgeMapping;
	
	
	
	public static void edgeMapping(int numNodi){
		edgeMapping = new short[2][numNodi*(numNodi-1)/2];
		int ind = 0;
		//Attenzione, non sono interessata agli archi che collegano un nodo con se stesso
		for(short i=1; i<numNodi; i++){
			for (short j=0; j<i; j++){
				edgeMapping[0][ind] = i;
				edgeMapping[1][ind] = j;
				ind++;
			}
		}
		//printEdgeMapping();
	}
	
	public static void printlnEdgeMapping(){
		for(int i = 0; i<edgeMapping[0].length; i++){
			System.out.println(i+")  <"+edgeMapping[0][i]+","+edgeMapping[1][i]+">");
		}
	}
	
	public static void printEdgeMapping(){
		for(int i = 0; i<edgeMapping[0].length; i++){
			System.out.print("<"+edgeMapping[0][i]+","+edgeMapping[1][i]+">"+" ");
		}
		System.out.println();
	}
	
	public static int getNumArchi(){
		return 	edgeMapping[0].length;
	}
	
	public static int getNumNodi(){
		return (int) ((1+Math.sqrt(1+4*2*getNumArchi()))/2);
	}
	
	public abstract int datasetSize();
	
	public abstract void add(Graph g);
		
	public abstract ArrayList<Integer> computeEdgeNeighbos(int edge, int newNode, boolean[] alreadyVisited);
	
	public abstract float computeCommonness(Pattern p, float sogliaProb, boolean isOther);
	
	public abstract float computeCommonness(Pattern father, Pattern child, float sogliaProb, boolean isOther);
	
	protected abstract boolean checkProbability(float prob, float sogliaProb, int patternSize);

}
