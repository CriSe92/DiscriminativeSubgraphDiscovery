package model;


public class GraphFactroy {

	public static Graph createGraph(int type, String f_corr, String f_prob, int numGeni, char sample_type, int index){
		switch (type) {
		case 0:
			return new Graph_inMemory(f_corr, f_prob, numGeni);
		case 1: 
			return new TinyGraph(f_corr, numGeni);
		case 2: 
			return new Graph_ShortArray(f_corr, numGeni,index);
		case 3: 
			return new Graph_onDisk(f_corr, numGeni,index);
		case 4:
			return new Graph_sparseVector(f_corr,index,sample_type);
		default:
			throw new RuntimeException("Implementazione non supportata");
		}
	}
}
