package application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import minig.DiscoverPatterns_inDepth;
import minig.Preprocessing;
import minig.Statistics_collector;
import model.Dataset;
import model.Graph_sparseVector;
import model.Pattern;
import networkCreation.NetworkBuilder;
import util.Heap;

import java.io.*;

public class Main {
	
	public static Heap<Pattern> patterns; //Result-set
	public static List<Pattern> list;
	//public static DiscoverPatterns mining = null;

	public static DiscoverPatterns_inDepth mining = null;

	public static void usage() {
		System.out.println("Usage: Main <dataset_path_folder> <dataset_name> [options]\n" + "\tOPTIONS:\n"
				+ "\t-type <H/U>: H or U as main population [default: H]\n"
				+ "\t-names <geneNames_path>: geneNames [default: null]\n"
				+ "\t-k <numPattern_output>: num pattern in output [default: 20]\n"
				+ "\t-dim <max_cardinality>: max_cardinality of pattern [default: 10]\n"
				+ "\t-tau_s <tau_s value>: strenght threshold (to be used for network creation if they are not availabe yet) [default: 0.7]\n"
				+ "\t-tau_r <tau_r value>: relevance threshold (to be used for network creation if they are not availabe yet) [default: 0.9]\n"
				//+ "\t-dp <dp value>: discriminative power threshold [default: ??]\n"
				+ "\t-verbose <0/1/2>: verbose(0: silent; 1: small-print; 2:extended-print) [default: 1]\n"
				//+ "\t-skip_saving: specify if preprocessing results have to be written on a file [defalut false]\n"
				+ "\t-serialize_heap:  specify if the heap has to be serialized at each step [defalut false]\n"
				+ "\t-fill_results: ### add description ### [default false]\n"
				+ "\t-depth: visit search space in depth [defalut false]\n");
		System.exit(0);
	}
	
	public static void print_heap() {
		System.out.println(patterns.toString());
	}
	
	public static List<Pattern> write_results(String nomeFile, Heap<Pattern> res, int dimMax, long start_time, float soglia) {	
		List<Pattern> res_list = mining.final_cleaning(res);
		write_results(nomeFile, res_list, dimMax, start_time,soglia);
		return res_list;
	}
	
	
	public static List<Pattern> write_results(String nomeFile, List<Pattern> res_list, int dimMax, long start_time, float soglia) {
				
		PrintWriter pw = null;
		int sizeRes = res_list.size();
		int curr_size;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(nomeFile)));	
			if(soglia > 0){
				pw.print("\n===  DEPTH "+dimMax+" STARTING THRESHOLD "+soglia);
				if(sizeRes>0) pw.print(" FINAL THRESHOLD "+res_list.get(0).getDiscriminativePower()+" ===\n");
			}
			pw.println("1: Nodes names (or edges id)");
			pw.println("2: Score");
			pw.println("3: Score (other population)");
			pw.println("4: Discriminative Power");
			pw.println("5: Upper Bound");
			pw.println("6: Support");
			pw.println("\nTop-" + sizeRes + " Pattern:");
			Statistics_collector.max_pattern_size = 0;
			for (int i = sizeRes - 1; i >= 0; i--) {
				//Scrivi su file
				pw.print("Pattern #" + (sizeRes - i)+" ");
				pw.println(res_list.get(i).getPatternInfo_geneNames());
				curr_size = res_list.get(i).getSize();
				if(curr_size>Statistics_collector.max_pattern_size)
					Statistics_collector.max_pattern_size = curr_size;
			}
			Statistics_collector.print_on_file(pw);
			
			if(start_time > 0 ) {
				Statistics_collector.print_time_on_file(pw,start_time);
			}else{
				Statistics_collector.print_global_time_on_file(pw);
			}
		} catch (IOException e) {
			System.err.println("Impossibile scrivere su "+nomeFile);
		}finally {
			if(pw != null)pw.close();
		}
		
		if(dimMax == ExperimentInfo.dimMax) {
			display_results(res_list);
		}
		return res_list;
	}
	
	public static void display_results(List<Pattern> res_list){
		int sizeRes = res_list.size();
		int curr_size = 0;
		Statistics_collector.max_pattern_size = 0;
		System.out.println("Top-" + sizeRes + " Pattern:");
		for (int i = sizeRes - 1; i >= 0; i--) {
			//Scrivi su file
			System.out.println("Pattern #" + (sizeRes - i));
			System.out.println(res_list.get(i));
			curr_size = res_list.get(i).getSize();
			if(curr_size>Statistics_collector.max_pattern_size)
				Statistics_collector.max_pattern_size = curr_size;
		}
		
		Statistics_collector.print();
		Statistics_collector.print_time();
	}
	
	public static void serialize_heap(int level){
		if(ExperimentInfo.serialize_heap){
			//Serializza l'heap
			System.out.println("SERIALIZING LAST HAEP...");
			String heap_file_name = ExperimentInfo.path + File.separator+ExperimentInfo.datasetName+"_top"+ExperimentInfo.k+"_curr_dim"+level+"_"+ExperimentInfo.id+"_heap.dat";
			ObjectOutputStream oos = null;
			try{
				oos = new ObjectOutputStream(new FileOutputStream(heap_file_name));
				for(Pattern p:patterns){
					oos.writeObject(p);
				}
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String args[]) {

		// Se true l'esame del primo livello è già stato effettuato
		//boolean preprocessing = false;
		// int graphType=4;

		//String path_index = null;
		//String path_data = null;
		//String path_support = null;
		
		long time;
		String preprocessing_file = null;
		boolean in_depth_strategy = false;
		
		if (args.length > 1) {
			ExperimentInfo.path = args[0];
			ExperimentInfo.datasetName = args[1];
		} else {
			usage();
		}

		if (args.length > 2) {
			for (int i = 2; i < args.length; i++) {
				if (args[i].equals("-type")) {
					i++;
					if (i < args.length)
						ExperimentInfo.id = args[i].charAt(0);
					else {// errore
						usage();
					}
				} else if (args[i].equals("-names")) {
					i++;
					if (i < args.length)
						ExperimentInfo.geneNames = args[i];
					else {// errore
						usage();
					}
				} else if (args[i].equals("-k")) {
					i++;
					if (i < args.length)
						ExperimentInfo.k = Integer.parseInt(args[i]);
					else {// errore
						usage();
					}
				} else if (args[i].equals("-dim")) {
					i++;
					if (i < args.length)
						ExperimentInfo.dimMax = Integer.parseInt(args[i]);
					else {// errore
						usage();
					}
				} else if (args[i].equals("-tau_s")) {
					i++;
					if (i < args.length)
						ExperimentInfo.tau_s = Float.parseFloat(args[i]);
					else {// errore
						usage();
					}
				} else if (args[i].equals("-tau_r")) {
					i++;
					if (i < args.length)
						ExperimentInfo.tau_r = Float.parseFloat(args[i]);
					else {// errore
						usage();
					}
				}else if (args[i].equals("-depth")) {
					in_depth_strategy  = true;
//				} else if (args[i].equals("-dp")) {
//					i++;
//					if (i < args.length)
//						ExperimentInfo.discirminative_power_threshold = Float.parseFloat(args[i]);
//					else {// errore
//						usage();
//					}
				}else if (args[i].equals("-verbose")) {
					i++;
					if (i < args.length)
						ExperimentInfo.verbose = Integer.parseInt(args[i]);
					else {// errore
						usage();
					}
				}else if (args[i].equals("-skip_saving")){
					ExperimentInfo.skip_saving = true;
				}else if (args[i].equals("-serialize_heap")){
					ExperimentInfo.serialize_heap = true;
				}else if (args[i].equals("-fill_results")){
					ExperimentInfo.fill_heap= true;
				} else {// errore
					usage();
				}
			}
		}

		ExperimentInfo.path_corr_h = ExperimentInfo.path + File.separator + ExperimentInfo.datasetName + "_corrH.ds2";
		// String path_p_h = path+File.separator+datasetName+"_pH.ds2";
		ExperimentInfo.path_corr_u = ExperimentInfo.path + File.separator + ExperimentInfo.datasetName + "_corrU.ds2";
		// String path_p_u = path+File.separator+datasetName+"_pU.ds2";
		

		System.out.println("WELCOME");	
		ExperimentInfo.print_info();

		/*
		 * Se i file con le reti non esistono li crea
		 */
		if(!(new File(ExperimentInfo.path_corr_h).exists()))
			NetworkBuilder.create_networks(ExperimentInfo.path, ExperimentInfo.datasetName, 'H', ExperimentInfo.tau_s, ExperimentInfo.tau_r, true);
		if(!(new File(ExperimentInfo.path_corr_u).exists()))
			NetworkBuilder.create_networks(ExperimentInfo.path, ExperimentInfo.datasetName, 'U', ExperimentInfo.tau_s, ExperimentInfo.tau_r, true);
				
		System.out.printf("\n====== MINING PROCEDURE BEGINS =====\n\n");
		
		int[] dim_h = null, dim_u = null;
		try {
			dim_h = util.OpenBinaryFiles.discoverSize(ExperimentInfo.path_corr_h);
			dim_u = util.OpenBinaryFiles.discoverSize(ExperimentInfo.path_corr_u);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERRORE DURANTE LA LETTURA DEL FILE");
			System.exit(0);
		}

		if (dim_h[1] != dim_u[1])
			throw new RuntimeException("Numero nodi non in accordo nelle due popolazioni");

		ExperimentInfo.numCampioniSani = dim_h[0];
		ExperimentInfo.numCampioniMalati = dim_u[0];
		ExperimentInfo.numNodi = (int) ((1 + Math.sqrt(1 + 4 * 2 * dim_h[1])) / 2);
		Dataset.edgeMapping(ExperimentInfo.numNodi);
		patterns = new Heap<Pattern>(ExperimentInfo.k);
		
		System.out.println("H samples: " + ExperimentInfo.numCampioniSani + "\nU samples: " + ExperimentInfo.numCampioniMalati + "\nnumNodi: " + ExperimentInfo.numNodi);
	
		/**PREPROCESSING, cioè analisi del primo livello*/
		
		System.out.printf("\n====== PREPROCESSING IN PROGRESS ======\n\n");
		
		time = System.currentTimeMillis();
		
		preprocessing_file = ExperimentInfo.path + File.separator+ExperimentInfo.datasetName+"_top"+ExperimentInfo.k+"_"+ExperimentInfo.id;
		
		Preprocessing.first_level_mining(ExperimentInfo.id=='H',preprocessing_file,(ExperimentInfo.skip_saving)?null:preprocessing_file);
		
		Statistics_collector.set_preprocessing_time(System.currentTimeMillis() - time);
		System.out.printf("\n====== PREPROCESSING ENDS IN %6.2f sec (keep %d edges)======\n\n", (System.currentTimeMillis() - time) / (1000.0),Preprocessing.get_ordered_edges().size());
				
		/**INIZIALIZZAZIONE DEGLI OGGETTI DATASET*/
		
		time = System.currentTimeMillis(); 
		
		System.out.println("Loading Dataset H...");
		
		Dataset dbH = new Dataset(ExperimentInfo.numCampioniSani);
		for (int i = 0; i < ExperimentInfo.numCampioniSani; i++) {
			Graph_sparseVector g = new Graph_sparseVector(ExperimentInfo.path_corr_h, i, 'H');
			//Graph_sparseVector g = new Graph_sparseVector(ExperimentInfo.path_corr_h, ExperimentInfo.numNodi, i, Preprocessing.get_index_vector());
			//Graph_ShortArray g = new Graph_ShortArray(path_corr_h, ExperimentInfo.numNodi, i);
			dbH.add(g);
		}
		
		System.out.println("Loading Dataset U...");
		

		Dataset dbU = new Dataset(ExperimentInfo.numCampioniMalati);
		for (int i = 0; i < ExperimentInfo.numCampioniMalati; i++) {
			Graph_sparseVector g = new Graph_sparseVector(ExperimentInfo.path_corr_u, i, 'U');
			//Graph_sparseVector g = new Graph_sparseVector(ExperimentInfo.path_corr_u, ExperimentInfo.numNodi, i, Preprocessing.get_index_vector());
			//Graph_ShortArray g = new Graph_ShortArray(path_corr_h, ExperimentInfo.numNodi, i);
			dbU.add(g);
		}
		
		Statistics_collector.set_loading_time(System.currentTimeMillis() - time);
		System.out.printf("\n====== END OF DATASET LOADING (%6.2f sec) ======\n\n", (System.currentTimeMillis() - time) / (1000.0));
		
		if (ExperimentInfo.id == 'H') {
			//mining = new minig.DiscoverPatterns(dbH, dbU);
			mining = new DiscoverPatterns_inDepth(dbH, dbU);
		} else {
			//mining = new minig.DiscoverPatterns(dbU, dbH);
			mining = new DiscoverPatterns_inDepth(dbU, dbH);
		}
		
		/**LANCIARE CON LE DIVERSE SOGLIE*/
		
		/*L'array contiene, in posizione i-1, la soglia con cui ha terminato il livelli i-esimo, 
		 * cioè il potere discriminate del pattern meno sigificativo presente nell'heap quando
		 * la procedura di mining è scesa fino al livello i (con i che va da 1 a dimMax)
		 * */
		float[] soglie = new float[ExperimentInfo.dimMax];
		float last_starting_threshold = 0;
		List<Pattern> res = null;
		int i;
		
		ExperimentInfo.set_strategy(in_depth_strategy);
		long start_experiments = System.currentTimeMillis();
		for(i=ExperimentInfo.starting_level; i<=ExperimentInfo.dimMax; i=i+ExperimentInfo.step) {
			mining.set_maxDept(i);
			mining.set_soglia_discriminative_power(patterns.getSoglia());
			System.out.println("=== START RUN LEVEL "+i+" THRESHOLD "+patterns.getSoglia()+" ===");
			soglie[i-ExperimentInfo.starting_level] = patterns.getSoglia();
			last_starting_threshold = patterns.getSoglia();
			time = System.currentTimeMillis();
			mining.discoverPatterns();
			System.out.printf("\n Time elapsed: %6.2f secondi\n", (System.currentTimeMillis() - time) / (1000.0));
			System.out.println("\n=== END RUN LEVEL "+i+" THRESHOLD "+patterns.getSoglia()+" ===\n");
			//Save results
			res = write_results(preprocessing_file+((in_depth_strategy)?"_depth_strategy":"_per_level_strategy")+"_depth#"+i+".res",new Heap<Pattern>(patterns),i,time,soglie[i-ExperimentInfo.starting_level]);  
			if(ExperimentInfo.serialize_heap) serialize_heap(i);
			if(last_starting_threshold==patterns.getSoglia()){
				System.out.println("WARNING! LEVEL"+i+" RUN DOESN'T INCREASE THE THRESHOLD");
				Statistics_collector.set_mining_time(System.currentTimeMillis() - start_experiments);
				display_results(res);
				ExperimentInfo.dimMax=i;
				break;
			}
		}
		
		soglie[ExperimentInfo.dimMax-1] = patterns.getSoglia();
		Statistics_collector.set_mining_time(System.currentTimeMillis() - start_experiments);
		
		System.out.println("=== SOGLIE ===");
		for(int j=0; j<soglie.length; j++) {
			System.out.print(soglie[j]+" ");
		}
		System.out.println();
		
		
		//Heap<Pattern> h = patterns;
		
		/**CONVERGENZA PER SISTEMAZIONE DEI RISULTATI*/		
		if(ExperimentInfo.fill_heap) {
			long start_fill = System.currentTimeMillis();
			System.out.println("\n=== FILL RESULT SET ===");
			//while(heap pieno or ho finito le soglie or l'utente mi ha dato una solgia minima e la ho garantita)
			
			list = new LinkedList<Pattern>(res);
			int heap_size = ExperimentInfo.k-list.size();
			patterns = new Heap<Pattern>(heap_size);
			patterns.setSoglia(last_starting_threshold);
			
			mining.reset_pattern_dimension(); /*previousRun_dimMax = 0; dimMax = 1;*/
			mining.set_hard_check(true);
			ExperimentInfo.set_filling_mode(true);
			Preprocessing.visit_first_level(ExperimentInfo.id=='H', ExperimentInfo.path + File.separator+ExperimentInfo.datasetName+"_fill_top"+heap_size+"_"+ExperimentInfo.id);
			
			for(i=ExperimentInfo.starting_level; i<=ExperimentInfo.dimMax; i=i+ExperimentInfo.step) {
				mining.set_maxDept(i);
				mining.set_soglia_discriminative_power(patterns.getSoglia());
				System.out.println("=== START RUN LEVEL "+i+" THRESHOLD "+patterns.getSoglia()+" ===");
				last_starting_threshold = patterns.getSoglia();
				time = System.currentTimeMillis();
				mining.discoverPatterns();
				System.out.printf("\n Time elapsed: %6.2f secondi\n\n", (System.currentTimeMillis() - time) / (1000.0));
				//Save results
				res = write_results(preprocessing_file+((in_depth_strategy)?"_depth_strategy":"_per_level_strategy")+"_fill_depth#"+i+".res",new Heap<Pattern>(patterns),i,time,last_starting_threshold);  
				if(ExperimentInfo.serialize_heap) serialize_heap(i);
				if(last_starting_threshold==patterns.getSoglia()){
					System.out.println("WARNING! LEVEL"+i+" RUN DOESN'T INCREASE THE THRESHOLD");
					ExperimentInfo.dimMax=i;
					break;
				}
			}
		
			/*Unione delle liste*/
			list.addAll(res);
			Collections.sort(list);
			write_results(preprocessing_file+((in_depth_strategy)?"_depth_strategy":"_per_level_strategy")+"_FINAL_RESULT_"+".res",list,ExperimentInfo.dimMax,-2,-2);  			
			Statistics_collector.set_fill_time(System.currentTimeMillis() - start_fill);
		}
		
		System.out.printf("\n=========== END in %6.2f sec ===========\n",Statistics_collector.get_global_time());

		
//		boolean convergence = false;
//		ExperimentInfo.verbose = 0;
//		mining.set_hard_check(true);
//		while(!convergence) {
//			System.out.println("SOGLIA (before cleaning): "+patterns.getMin().getDiscriminativePower());
//			System.out.println("VAR SOGLIA (before cleaning): "+patterns.getSoglia());
//			System.out.println("HEAP --- before cleaning ---");
//			patterns.print();
//			System.out.println("HEAP --- after cleaning ---");
//			mining.clean_heap(patterns);
//			patterns.print();
//			soglia = patterns.getMin().getDiscriminativePower();
//			mining.set_maxDept(ExperimentInfo.dimMax);
//			mining.set_soglia_discriminative_power(soglia);
//			mining.discoverPatterns();
//			//check covergenza
//			System.out.println("SOGLIA: "+patterns.getMin().getDiscriminativePower());
//			System.out.println("VAR SOGLIA (before cleaning): "+patterns.getSoglia());
//			System.out.println("NUM ELEM: "+patterns.getLength());	
//			System.out.println("SIZE: "+patterns.getSize());	
//		}
	}
}
