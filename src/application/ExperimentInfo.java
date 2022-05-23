package application;

import java.io.File;

public class ExperimentInfo {
	

	//Mi aspetto che tutti i files siano nella cartella path
	public static String path = "";
	public static String datasetName = "";
	public static String geneNames = null;
	
	public static String path_corr_h;
	public static String path_corr_u;
	
	public static String path_p_h;
	public static String path_p_u;
	
	public static float tau_s = 0.7f;
	public static float tau_r = 0.9f;
	
	public static int numCampioniSani;
	public static int numCampioniMalati;
	public static int numNodi;
	public static char id = 'H'; // H/U
	
	public static int step = 1; 
	public static int starting_level = 2; 
	
	public static boolean skip_saving = false;
	public static boolean serialize_heap = false;
	public static String preprocessing_file_to_read = null;
	public static boolean fill_heap = false;
	
	public static boolean filling_mode = false;
	
	public static int k = 20; // Num patterns to be given in output
	public static int dimMax = 5;
	public static float discirminative_power_threshold = 0.0f;
	public static int verbose = 1;
	
	public static void set_strategy(boolean in_depth) {
		if(in_depth) {
			starting_level = dimMax;
		}
	}
	
	public static void set_filling_mode(boolean fill) {
			filling_mode = fill;
	}
	
	public static int getNumArchi() {
		return ExperimentInfo.numNodi*(ExperimentInfo.numNodi-1)/2;
	}
	
	public static void print_info() {
		System.out.println("Analyzing data in " + path);
		System.out.println("strength threshold: " + tau_s);
		System.out.println("relevance threshold: " + tau_r);
		//System.out.println("discriminative power threshold: " + discirminative_power_threshold);
		System.out.println("Dim Max: " + dimMax+" edges");
		System.out.println("k: " + k);
		System.out.println("Discriminative for " + id);
		if(geneNames!=null && (new File(geneNames).exists())) System.out.println("Gene names available in "+ geneNames);
		else System.out.println("Gene names file not available");
	}

}
