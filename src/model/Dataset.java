package model;

import java.util.ArrayList;
import util.BinaryVector;

public class Dataset extends AbstractDataset {

	/*
	 * Ciascun dataset è modellato come un array di grafi (oggetti di classe
	 * Graph), infatti è noto a priori il numero di grafi presenti nel dataset
	 */
	private Graph[] samples;

	/*
	 * In fase di inserimento dei grafi nel dataset, si utilizzano gli interi
	 * seguenti per segnalare la posizione in cui fare l'inserimento
	 */
	private int lastIns = 0;

	public Dataset(int numIndividui) {
		samples = new Graph[numIndividui];
	}

	public int datasetSize() {
		return samples.length;
	}

	public void add(Graph g) {
		samples[lastIns] = g;
		lastIns++;
	}

	// Calcola i vicini di un arco, escludendo dall'insieme restituito quelli che sono già stati visitati
	public ArrayList<Integer> computeEdgeNeighbos(int edge, int newNode, boolean[] alreadyVisited) {
		// Considero i potenziali vicini dell'arco edge, da questi tolgo gli alreadyVisisted
		ArrayList<Integer> neigh = new ArrayList<Integer>();
		int row = edgeMapping[0][edge];
		int col = edgeMapping[1][edge];

		if (newNode == row || newNode == -1) {
			// Calocolo gli archi che insisrono sul nodo "row"
			int startR = (row - 1) * row / 2;
			int stepR = row + 1;
			// Considero gli archi il cui nodo "di sopra" coincide con row
			for (int i = 0; i < row; i++) {
				if (edgeMapping[1][startR] != col && !alreadyVisited[startR])
					neigh.add(startR);
				startR++;
			}
			// Considero gli archi il cui nodo "di sotto" coincide con row
			startR = (row + 1) * (row + 2) / 2 - 1;
			while (startR < edgeMapping[0].length) {
				if (!alreadyVisited[startR])
					neigh.add(startR);
				startR = startR + stepR;
				stepR = stepR + 1;
			}
		}
		if (newNode == col || newNode == -1) {
			// Calocolo gli archi che insisrono sul nodo "col"
			int startC = (col - 1) * col / 2;
			int stepC = col + 1;
			// Considero gli archi il cui nodo "di sopra" coincide con col
			for (int i = 0; i < col; i++) {
				if (!alreadyVisited[startC])
					neigh.add(startC);
				startC++;
			}
			// Considero gli archi il cui nodo "di sotto" coincide con col
			startC = (col + 1) * (col + 2) / 2 - 1;
			while (startC < edgeMapping[0].length) {
				if (edgeMapping[0][startC] != row && !alreadyVisited[startC])
					neigh.add(startC);
				startC = startC + stepC;
				stepC = stepC + 1;
			}
		}

		return neigh;
	}

	// Calcola il punteggio di significatività (commonness) del pattern
	public float computeCommonness(Pattern p, float sogliaProb, boolean isOther) {
		float ris = 0;
		float localComm = 0;
		// prendo la lista degli archi che compongono il pattern
		for (int k = 0; k < samples.length; k++) {
			// Se il pattern è presente nel grafo k con un valore cogruo di probabilità il metodo mi restituisce il punteggio
			localComm = containsPattern(k, p.getEdges(), sogliaProb);
			if (!isOther && localComm > 0)
				p.addGraph(k);
			ris = ris + localComm;
		}
		return ris;
	}

	// Calcola il punteggio di significatività (commonness) del pattern
	public float computeCommonness(Pattern father, Pattern child, float sogliaProb, boolean isOther) {
		if (father == null) {
			return computeCommonness(child, sogliaProb, isOther);
		}
		float ris = 0;
		float localComm = 0;
		BinaryVector fatherSupport=father.getGraphID();
		//int[] lastAddedEdge = new int[1];
		for(int k=0; k< samples.length; k++){
			if(fatherSupport.get(k)){
				// Se il pattern è presente nel grafo k con un valore cogruo di probabilità il metodo mi restituisce il punteggio
				//lastAddedEdge[0] = child.getLastAddedEdge();
				localComm = containsPattern(k, child.getEdges(), sogliaProb); //Posso verificare se c'è solo l'ultimo arco aggiunto in child visto che father è già presente nel grafo k
				if (!isOther && localComm > 0)
					child.addGraph(k);
				ris = ris + localComm;
			}
		}
		return ris;
	}

	// Verifica se il grafo di indice <grafo> contiene il pattern formato dalla lista degli archi edges e ne restituisce la commonness
	public float containsPattern(int grafo, int[] edges, float sogliaProb) {
		Graph g = samples[grafo];
		float weight = 0;
		float prob = 1;
		float corr = 0;
		for (int i = 0; i < edges.length; i++) {
			int row = edgeMapping[0][edges[i]];
			int col = edgeMapping[1][edges[i]];
			corr = g.getCoorelation(edges[i]);
			if (corr <= 0) {
				return 0;
			} else {
				weight = weight + g.getCoorelation(edges[i]);
			}
			prob = prob * g.getProbability(row, col);
		}
		if (checkProbability(prob, sogliaProb, edges.length))
			return weight / edges.length;
		return 0;
	}

	protected boolean checkProbability(float prob, float sogliaProb, int patternSize) {
		return prob > Math.pow(sogliaProb, patternSize);
	}
	
	public void print_dataset() {
		printEdgeMapping();
		for(int i=0; i<samples.length; i++) {
			samples[i].printGraph_inLine();
		}
	}
}
