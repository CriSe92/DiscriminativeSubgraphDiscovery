package minig;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import application.ExperimentInfo;
import application.Main;
import model.Pattern;
import util.OpenBinaryFiles;
import util.SaveBinaryFile;
import util.Sorter;

public class Preprocessing {

	//static BinaryVector index = null;
	static ArrayList<Pattern> orderedEdges = new ArrayList<Pattern>();
	static boolean hard_check = false;
		
	public static void first_level_mining(boolean type, String read_from_file, String save_to_file){
		if(read_from_file != null && (new File(read_from_file+"_data.ds2").exists()) && (new File(read_from_file+"_index.ds2").exists())){
			read_first_level(read_from_file);
		}else{
			visit_first_level(type, save_to_file);
		}
	}
	
//	private static void load_old_data() throws IOException {
//		float[][] data = OpenBinaryFiles.openMatrix("/home/cristina/Scrivania/bigTest/gastric/H_vs_U_top350/preprocessedInfo_H_vs_U.ds2");
//		int[] numNodi = {1};
//		int[] index = OpenBinaryFiles.openVector("/home/cristina/Scrivania/bigTest/gastric/H_vs_U_top350/index_H_vs_U.ds2",numNodi);
//		int size = data.length;
//		final int comm=0;
//		final int ds=1;
//		final int ub=2;
//
//		for(int i=0; i<size;i++){
//			Pattern p = new Pattern(1);
//			p.addEdge(index[i]);
//			p.setCommonness(data[i][comm]);
//			p.setDiscriminativePower(data[i][ds]);
//			p.setUpperBound(data[i][ub]);			
//
//			orderedEdges.add(p);
//			//Riempimento heap
//			Main.patterns.add(p);
//		}
//	}
	

	public static void visit_first_level(boolean type, String save_to_file) {
		float[] comm_main = null;
		float[] comm_other = null;
		int[] edges_ids = null;
		//BinaryVector[] support =  new BinaryVector[ExperimentInfo.getNumArchi()];
		int main_size=0;
		int other_size=0;
		
		long time = System.currentTimeMillis();
		
		try {
			if(type) {
				comm_main = OpenBinaryFiles.computeCommonnessVector(ExperimentInfo.path_corr_h);
				comm_other = OpenBinaryFiles.computeCommonnessVector(ExperimentInfo.path_corr_u);
				main_size = ExperimentInfo.numCampioniSani;
				other_size = ExperimentInfo.numCampioniMalati;
			}else {
				comm_main = OpenBinaryFiles.computeCommonnessVector(ExperimentInfo.path_corr_u);
				//comm_main = OpenBinaryFiles.computeCommonnessAndSupportVector(ExperimentInfo.path_corr_u,support);
				comm_other = OpenBinaryFiles.computeCommonnessVector(ExperimentInfo.path_corr_h);	
				main_size = ExperimentInfo.numCampioniMalati;
				other_size = ExperimentInfo.numCampioniSani;
			}
		}catch(IOException e){
			System.out.println("ERRORE DURANTE LA LETTURA DEI FILE CONTNENTI I GRAFI");
			e.printStackTrace();
			System.exit(-1);
		}
		int patternSize = 1;
		float upper_bound;
		float discriminative_power;
		//index = new BinaryVector(comm_main.length);
		edges_ids = Sorter.heap_sort(comm_main);
		
		double end = (System.currentTimeMillis() - time) / (1000.0);
		System.out.printf("\n[ END OF COMMONNESS COMPUTING IN %6.2f sec ]\n", end);
		
//		DataOutputStream data_file;
//		if(save_to_file != null) {
//			save_index_vector(save_to_file, edges_ids);
//			 data_file = prepare_preprocessing_file(save_to_file,edges_ids.length); 
//		}

		for(int i=0; i<comm_main.length; i++) {
			//if (comm_main[i] > 0) {
				upper_bound = Measures_Calculator.upperBound(main_size, other_size, comm_main[i]);
				if (prosegui(upper_bound, getSoglia())) {//L'arco può proseguire, lo aggiungo alla lista che andrà successivamente a formare index
					//index.set(edges_ids[i]);
					Statistics_collector.update_max_upper_bound(0,upper_bound);
					//Poichè l'upper_bound è sempre maggiore o uguale al potere discriminante corrente, si tenta l'inseriemnto nell'heap solo se la condizione nell'if è verificata
					Pattern child = new Pattern(patternSize);
					child.addEdge(edges_ids[i]);
					discriminative_power = Measures_Calculator.discriminativePower(main_size,other_size,comm_main[i],comm_other[edges_ids[i]]);
					if(!ExperimentInfo.filling_mode) Statistics_collector.nodes++;
					child.setCommonness(comm_main[i]);
					child.setCommonness_other(comm_other[edges_ids[i]]);
					child.setDiscriminativePower(discriminative_power);
					child.setUpperBound(upper_bound);
					child.setIsBest(true);
					orderedEdges.add(child);
					//System.out.println(i+")"+Main.patterns.getSoglia());
					//if(save_to_file != null){
					//	save_line(data_file,comm_main[i],comm_other[edges_ids[i]],discriminative_power,upper_bound)
					//}
					if(canAdd(child)) {
						//System.out.println("ADDED "+child);
						Main.patterns.add(child);
					}
				} else {
					if(!ExperimentInfo.filling_mode) Statistics_collector.cuttedPatterns = comm_main.length - Statistics_collector.nodes;
					break; //concludi il for
				}
			//}
		}
		
		if(save_to_file != null) save(save_to_file);
		//if(save_to_file != null) data_file.close();
	}
	
	
	public static boolean canAdd(Pattern p) {
		if(!ExperimentInfo.filling_mode) return true;
		return DiscoverPatterns_inDepth.canAdd_hardCheck(p);
	}
	
	private static void read_first_level(String nomeFile){

		float[][] data = null;
		int[] numNodi={1};
		int[] index = null;
		try {
			data = OpenBinaryFiles.openMatrix(nomeFile+"_data.ds2");
			index = OpenBinaryFiles.openVector(nomeFile+"_index.ds2",numNodi);
		} catch (IOException e) {
			System.out.println("Impossibile leggere i dati di preprocessing");
			e.printStackTrace();
			System.exit(0);
		}
				
		int size = data.length;
		final int comm=0;
		final int comm_other=1;
		final int ds=2;
		final int ub=3;
		float max_ub = 0;
		//Inserire id grafi che contengono il pattern
		for(int i=0; i<size;i++){
			Pattern p = new Pattern(1);
			p.addEdge(index[i]);
			p.setCommonness(data[i][comm]);
			p.setCommonness_other(data[i][comm_other]);
			p.setDiscriminativePower(data[i][ds]);
			p.setUpperBound(data[i][ub]);
			p.setIsBest(true);
			if(max_ub < data[i][ub])
				max_ub = data[i][ub];
			orderedEdges.add(p);
			//Riempimento heap
			Main.patterns.add(p);
		}
		
		//Update Statistics
		if(!ExperimentInfo.filling_mode) {
			Statistics_collector.update_max_upper_bound(0,max_ub);
			Statistics_collector.nodes = orderedEdges.size();
			Statistics_collector.cuttedPatterns = numNodi[0]*(numNodi[0]-1)/2 - Statistics_collector.nodes;
		}
		//Carica heap serializzato
//		if((new File(nomeFile+"_heap.dat").exists())){
//			System.out.println("CARICO HEAP SERIALIZZATO...");
//			ObjectInputStream ois = null; 
//			try {
//				ois = new ObjectInputStream(new FileInputStream(nomeFile+"_heap.dat"));
//				Pattern p = null;
//				for(;;){
//					try {
//						p = (Pattern)ois.readObject();
//					} catch (ClassNotFoundException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					} catch (EOFException e2){break;}
//					Main.patterns.add(p);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}finally{
//				try {
//					ois.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}else {
//			for(int i=0; i<orderedEdges.size();i++){
//				Main.patterns.add(orderedEdges.get(i));
//			}
		//}
	}
	
	public static void save(String nomeFile) {
		//Salvare formato .ds2, una matrice con 4 colonne (comm_main,comm_other, discriminativePower, UpperBound) e il vettore degli indici
		//final int comm_main=0;
		//final int comm_other=1;
		//final int ds=2;
		//final int ub=3;
		
		int rows = orderedEdges.size();
		int cols = 4;
		float[] data = new float[cols];
		int[] index = new int[1];
		Pattern currentPattern;
		DataOutputStream dos_float = null,dos_int = null;
		try{
			dos_float = SaveBinaryFile.open_file(nomeFile+"_data.ds2");
			dos_int = SaveBinaryFile.open_file(nomeFile+"_index.ds2");
			if(dos_float == null || dos_int== null) {
				System.err.println("I risultati del preprocessing non verranno salvati su file");
				return;
			}
			SaveBinaryFile.writeDimension( rows, cols, dos_float);
			//Per Compatibilità con i files prodotti da Matlab il primo elemento dell'file contenente gli indic conterà il numero di geni del dataset
			SaveBinaryFile.writeDimension( rows+1, 1, dos_int);
			index[0]=ExperimentInfo.numNodi;
			SaveBinaryFile.save_int_vector(dos_int, index);
			
			for(int i=0; i<orderedEdges.size(); i++){
				currentPattern = orderedEdges.get(i);
				data[0] = currentPattern.getCommonness();
				data[1] = currentPattern.getCommonness_other();
				data[2] = currentPattern.getDiscriminativePower();
				data[3] = currentPattern.getUpperBound();
				index[0] = currentPattern.getEdges()[0];
				SaveBinaryFile.save_float_vector(dos_float, data);
				SaveBinaryFile.save_int_vector(dos_int, index);
			}
		}catch (IOException e) {
			System.err.println("Errore durante il salvataggio dei risultati del preprocessing\n");
		}finally{	
			try {
				if(dos_float != null) dos_float.close();
				if(dos_int != null) dos_int.close();
			} catch (IOException e) {
				System.err.println("Errore durante la chiusura del Data Output Stream per il salvataggio dei risultati del preprocessing");
				e.printStackTrace();
			}
		}
	}
	
	/*Il file *_data.ds2 e *_index.ds2 contengono le inofrmazioni su tutti gli archi (orinati per commonness)
	 * occoore stabilire dove fermarsi nella lettura
	 * */
	public static void read_first_level_full_file(String nomeFile){
		
		/*Caricare in memoia i files un pezzo per volta e provare ad aggiungere finchè prosegui() dice si. Se i dati attualemte in memoria 
		 * terminano caricarne una ulteriore porzione*/
		
		float[][] data = null;
		int[] numNodi={1};
		int[] index = null;
		try {
			data = OpenBinaryFiles.openMatrix(nomeFile+"_data.ds2");
			index = OpenBinaryFiles.openVector(nomeFile+"_index.ds2",numNodi);
		} catch (IOException e) {
			System.out.println("Impossibile leggere i dati di preprocessing");
			e.printStackTrace();
			System.exit(0);
		}
				
		int size = data.length;
		final int comm=0;
		final int comm_other=1;
		final int ds=2;
		final int ub=3;
		float max_ub = 0;
		//Inserire id grafi che contengono il pattern
		for(int i=0; i<size;i++){
			Pattern p = new Pattern(1);
			p.addEdge(index[i]);
			p.setCommonness(data[i][comm]);
			p.setCommonness_other(data[i][comm_other]);
			p.setDiscriminativePower(data[i][ds]);
			p.setUpperBound(data[i][ub]);
			p.setIsBest(true);
			if(max_ub < data[i][ub])
				max_ub = data[i][ub];
			orderedEdges.add(p);
			//Riempimento heap
			Main.patterns.add(p);
		}
		
		//Update Statistics
		if(!ExperimentInfo.filling_mode) {
			Statistics_collector.update_max_upper_bound(0,max_ub);
			Statistics_collector.nodes = orderedEdges.size();
			Statistics_collector.cuttedPatterns = numNodi[0]*(numNodi[0]-1)/2 - Statistics_collector.nodes;
		}
	}
	
	//Salvo tutto il preprocessing e poi lo leggo fino alla riga che soddisfa le mie esigenze
	public static void save_index_vector(String nomeFile, int[] edges_id) {
		int rows = edges_id.length;
		DataOutputStream dos_int = null;
		try{
			dos_int = SaveBinaryFile.open_file(nomeFile+"_index.ds2");
			if( dos_int== null) {
				System.err.println("I risultati del preprocessing non verranno salvati su file");
				return;
			}
			//Per Compatibilità con i files prodotti da Matlab il primo elemento dell'file contenente gli indic conterà il numero di geni del dataset
			SaveBinaryFile.writeDimension( rows+1, 1, dos_int);
			dos_int.writeInt(ExperimentInfo.numNodi);
			SaveBinaryFile.save_int_vector(dos_int, edges_id);
			
		}catch (IOException e) {
			System.err.println("Errore durante il salvataggio dei risultati del preprocessing\n");
		}finally{	
			try {
				if(dos_int != null) dos_int.close();
			} catch (IOException e) {
				System.err.println("Errore durante la chiusura del Data Output Stream per il salvataggio dei risultati del preprocessing");
				e.printStackTrace();
			}
		}
	}
	
	public static DataOutputStream prepare_preprocessing_file(String nomeFile, int rows) {
		int cols = 4;
		DataOutputStream dos_float = null;
		try {
			dos_float = SaveBinaryFile.open_file(nomeFile+"_data.ds2");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dos_float == null ) {
			System.err.println("I risultati del preprocessing non verranno salvati su file");
			return null;
		}
		try {
			SaveBinaryFile.writeDimension( rows, cols, dos_float);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dos_float;
	}
	
	public static void save_line(DataOutputStream data_file,float comm_main,float comm_other,float discriminative_power,float upper_bound) {
		int cols = 4;
		float[] data = new float[cols];
		data[0] = comm_main;
		data[1] = comm_other;
		data[2] = discriminative_power;
		data[3] = upper_bound;
		try {
			SaveBinaryFile.save_float_vector(data_file, data);
		} catch (IOException e) {
			System.err.println("Errore durante il salvataggio dei risultati del preprocessing\n");
		}
		
	}
	

	private static float getSoglia() {
		return Math.max(ExperimentInfo.discirminative_power_threshold,Main.patterns.getSoglia());
	}

	private static boolean prosegui(float upper_bound, float soglia) {
		return upper_bound >= soglia;
	}
	
	
	public static ArrayList<Pattern> get_ordered_edges(){
		return orderedEdges;
	}
	
	public static void print_float_vector(float[] v) {
		System.out.print("[");
		for(int i=0; i<v.length; i++) {
			System.out.print(" "+v[i]);
		}
		System.out.println("]");
	}
	
	public static void print_int_vector(int[] v) {
		System.out.print("[");
		for(int i=0; i<v.length; i++) {
			System.out.print(" "+v[i]);
		}
		System.out.println("]");
	}
}
