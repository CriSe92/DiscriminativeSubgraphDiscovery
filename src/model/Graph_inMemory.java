package model;

import java.io.IOException;

import javax.swing.JOptionPane;

import util.*;

/*
 * Un grafo è rappresentato dalla sua matrice di adiacenza che è mantenuta in forma triangolare inferiore. 
 * Questi grafi andranno a comporre un dataset di grafi. 
 * In prima istanza tali grafici avranno matrici di adiacenza della stessa dimensione, ma questo non è vero in generale 
 * poichè se si decide di optare per un taglio sul numero di nodi è possibile che esistanpo dei nodi presenti
 * in alcuni grafi ma non in altri, tuttavia per il momento si riterrà  che in una tale situazione sia possibile considerare
 * la riga che identifica quel dato nodo come tutta nulla.
 * 
 * La costruzione del grafo parte dai file che contengono due matrici di adiacenza per il grafo, la prima contiene i valori
 * delle correlazioni, l'altra quella dei pesi probabilistici, già opportunamente modificatre per eliminare gli archi per i quali
 * si ha una elevata probabilità che siano dovuti al caso, quelli con valore di correlazione al di sotto di una certa soglia
 * Mi aspetto che le matrici siano costruite correttamente e abbiano egual dimensione.
 */

public class Graph_inMemory implements Graph {

	/*
	 * La matrice 0 contiene le correlazioni (strength), la matrice 1 le probabilità (relevance)
	 */
	private float[][][] grafo;
	
	public Graph_inMemory(String f_corr, String f_prob, int numGeni){
		grafo = createTriangularMatrix(numGeni);
		try {
			grafo[0] = OpenBinaryFiles.openTriangular(f_corr, numGeni);
			grafo[1] = OpenBinaryFiles.openTriangular(f_prob, numGeni);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
	}
	
	public int getNumNodi(){
		return grafo[0].length;
	}
	
	private float[][][] createTriangularMatrix(int nr){ /* riceve il numero di righe che ricavo da matlab quando creo la matrice*/
		float[][][] grafo  = new float[2][nr][];
		for(int i=0; i<grafo.length; i++)
			for(int j=0; j<grafo[i].length; j++)
				grafo[i][j] = new float[j+1];
		return grafo;
	}

	public void printGraph(){
		System.out.println("MATRICE DELLE CORRELAZIONI:");
		for(int i=0; i<grafo[0].length; i++){
			for(int j=0; j<grafo[0][i].length; j++){
				System.out.print(grafo[0][i][j]+"\t");
			}
			System.out.println("\n");
		}
		System.out.println("MATRICE DELLE PROBABILITA':");
		for(int i=0; i<grafo[1].length; i++){
			for(int j=0; j<grafo[1][i].length; j++){
				System.out.print(grafo[1][i][j]+"\t");
			}
			System.out.println("\n");
		}
	}
	
	public void printGraph_inLine() {
		System.out.println("TODO");
	}
	
	public float getCoorelation(int r, int c){
		return grafo[0][r][c];
	}
	
	public float getProbability(int r, int c){
		return grafo[1][r][c];
	}

	@Override
	public float getCoorelation(int edgeIndex) {
		throw new UnsupportedOperationException();
	}	
}

