package minig;

import java.io.PrintWriter;

import application.ExperimentInfo;

public class Statistics_collector {
	
	// Conta i pattern generati
	public static int addedPattern = 0;
	// Conta i nodi dell'albero di ricerca visitati (% spazio di ricerca)
	public static int nodes = 0;
	// Massima profondità raggiunta
	public static int maxDepth = 0;
	public static int max_pattern_size = 0;
	public static int cuttedPatterns = 0;
	
	public static float[] max_upper_bound_per_level = new float[(ExperimentInfo.dimMax<Integer.MAX_VALUE)?ExperimentInfo.dimMax:0];
	
	public static double mining_time = 0;
	public static double fill_time = 0;
	public static double preprocessing_time = 0;
	public static double loading_time = 0;
	
	
	public static int visitedNodes() {
		return nodes;
	}

	public static int getAddedPattern() {
		return addedPattern;
	}

	public static int getCuttedPattern() {
		return cuttedPatterns;
	}

	public static int getMaxDepth() {
		return maxDepth;
	}
	
	public static void set_mining_time(long time) {
		mining_time = time / (1000.0);
	}
	
	public static void set_preprocessing_time(long time) {
		preprocessing_time = time / (1000.0);
	}
	
	public static void set_loading_time(long time) {
		loading_time = time / (1000.0);
	}
	
	
	public static void set_fill_time(long time) {
		fill_time = time / (1000.0);
	}
	
	public static double get_global_time() {
		return loading_time+preprocessing_time+fill_time+mining_time;
	}
	
	public static void update_max_upper_bound(int level, float v) {
		
		if(max_upper_bound_per_level.length > 0 && max_upper_bound_per_level[level]<v)
			max_upper_bound_per_level[level] = v;
	}
	
	public static void print() {
		System.out.println("\nSTATISTICS");
		System.out.println("\nVisited nodes: " + Statistics_collector.visitedNodes());
		System.out.println("\nMax Depth: " + Statistics_collector.getMaxDepth());
		System.out.println("\nMax Pattern size: " + max_pattern_size+" edges");
		System.out.println("\nCutted Patterns: " + Statistics_collector.getCuttedPattern());
		System.out.println("\nMax Upper Bound per level: ");
		System.out.print("[ ");
		for(int i=0; i<max_upper_bound_per_level.length; i++) {
			System.out.printf("%5.4f ",max_upper_bound_per_level[i]);
		}
		System.out.println("]");
	}
	
	public static void print_on_file(PrintWriter pw) {
		pw.println("\nSTATISTICS");
		pw.println("\nVisited nodes: " + Statistics_collector.visitedNodes());
		pw.println("\nMax Depth: " + Statistics_collector.getMaxDepth());
		pw.println("\nMax Pattern size: " + max_pattern_size+" edges");
		pw.println("\nCutted Patterns: " + Statistics_collector.getCuttedPattern());
		pw.println("\nMax Upper Bound per level: ");
		pw.print("[ ");
		for(int i=0; i<max_upper_bound_per_level.length; i++) {
			pw.printf("%5.4f ",max_upper_bound_per_level[i]);
		}
//		pw.println("\nStarting Threshold per level: ");
//		pw.print("[ ");
//		for(int i=0; i<thresholds.length; i++) {
//			pw.printf("%5.4f ",thresholds[i]);
//		}
		pw.println("]");
	}
	
	public static void print_time_on_file(PrintWriter pw, long start) {
		double end = (System.currentTimeMillis() - start) / (1000.0);
		pw.println("\nTIME");
		pw.printf("\nTempo impiegato: %6.2f secondi\n", end);
	}
	
	public static void print_time() {
		System.out.println("\nTIME\n");
		System.out.println("Preprocessing time "+preprocessing_time+" sec");
		System.out.println("Loading dataset time "+loading_time+" sec");
		System.out.println("Mining time "+mining_time+" sec");
		if(fill_time>0) System.out.println("Fill result set time time "+fill_time+" sec");
		System.out.println();
	}
	
	
	public static void print_global_time_on_file(PrintWriter pw) {
		pw.println("\nTIME\n");
		pw.println("Preprocessing time "+preprocessing_time+" sec");
		pw.println("Loading dataset time "+loading_time+" sec");
		pw.println("Mining time "+mining_time+" sec");
		if(fill_time>0) pw.println("Fill result set time "+fill_time+" sec");
	}
	
	/*INSERIRE VARIABILI PER COLLEZIONARE I TEMPI*/

}
