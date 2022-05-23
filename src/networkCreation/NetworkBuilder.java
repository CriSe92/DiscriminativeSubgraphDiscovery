package networkCreation;

import java.io.File;
import java.io.IOException;
import util.OpenBinaryFiles;
import util.OpenTextFiles;
import util.SaveBinaryFile;

public class NetworkBuilder {
	
	public static int nEdges=0;
	public static int globalEdge=0;
	
	public static float[] findCorrelation(float[] expressionData, float tau_s) {
		int nGenes= expressionData.length;
		float[] res = new float[nGenes*(nGenes-1)/2];
		int index=0;
		for (int i = 1; i < expressionData.length; i++) {
			for(int j=0; j<i; j++) {
				res[index] = Statistics.computeCorrelation(expressionData[i], expressionData[j]);
				//res[index]=(expressionData[i]>expressionData[j])?1:0;
				if(res[index]<tau_s) {
					res[index]=0;
				}else {
					nEdges++;
					globalEdge++;
				}
				index++;
			}
		}
		return res;
	}
	
	public static float[] probMatrix(float[] expressionData, float[] mCorr, float tau_r) {
		int nGenes= expressionData.length;
		float[] res = new float[nGenes*(nGenes-1)/2];
		int index=0;
		for (int i = 1; i < expressionData.length; i++) {
			for(int j=0; j<i; j++) {
				if(mCorr[index]>0){
					res[index] = 1-Statistics.computeProbability(expressionData[i], expressionData[j],mCorr[index]);
					if(res[index]<tau_r) {//res[index]>0 && res[index]<tau_r
						res[index]=0;
						mCorr[index]=0;
						nEdges--;
						globalEdge--;
					}
				}
				index++;
			}
		}
		return res;
	}
	
	public static void z_score_normalization(float[][] data) {
		float[] mean = new float[data[0].length];
		
		for(int i=0; i<data[0].length; i++) {
			for(int j=0; j<data.length; j++) {
				mean[i]=mean[i]+data[j][i];
			}
			mean[i] = mean[i]/data.length;
		}
		
		float[] standard_deviation = new float[data[0].length];
		for(int i=0; i<data[0].length; i++) {
			for(int j=0; j<data.length; j++) {
				standard_deviation[i]=standard_deviation[i]+(data[j][i]-mean[i])*(data[j][i]-mean[i]);
			}
			standard_deviation[i] = (float) Math.sqrt(standard_deviation[i]/(data.length-1));
		}
	
		
		for(int i=0; i<data[0].length; i++) {
			for(int j=0; j<data.length; j++) {
				data[j][i] = (data[j][i] -mean[i])/standard_deviation[i];
			}
		}
		
//		try {
//			SaveBinaryFile.save("/home/cristina/Scrivania/test_network_creation/normalized.ds2", data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
	}
	
	
	public static void usage() {
		System.out.println("Usage: NetworkBuilder <dataset_path> <dataset_name> <H/U>[options]\n" + "\tOPTIONS:\n"
				+ "\t-tau_s <tau_s value>: strenght threshold [default: 0.7]\n"
				+ "\t-tau_r <tau_r value>: relevance threshold [default: 0.9]\n"
				+ "\t-saveProb <true/false>: save prob matrix on a file [default: false]\n");
		System.exit(0);
	}
	
	public static void create_networks(String path,String datasetName, char type, float tau_s,float tau_r, boolean saveProb){

		String fileName = path+File.separator+datasetName+"_"+type;
		
		float[][] data =null; //nSamples x nGenes
		if((new File(fileName+".ds2").exists())) {
			try {
				data = OpenBinaryFiles.openDataset(fileName+".ds2");
			} catch (IOException e) {
				System.out.println("ERRORE durante l'apertura del dataset");
				e.printStackTrace();
			}
		}else {
			if((new File(fileName+".txt").exists())) {
				try {
					data = OpenTextFiles.openDataset(fileName+".txt",",");
				} catch (IOException e) {
					System.out.println("ERRORE durante l'apertura del dataset");
					e.printStackTrace();
				}
			}else if((new File(fileName+".csv").exists())) {
				try {
					data = OpenTextFiles.openDataset(fileName+".csv",",");
				} catch (IOException e) {
					System.out.println("ERRORE durante l'apertura del dataset");
					e.printStackTrace();
				}
			}else {
				System.err.println("Dataset file not found!");
				System.exit(0);
			}
		}
		
		z_score_normalization(data);
				
		int nSamples = data.length;
		//int nGenes = data[0].length;
		
		float[] mCorr;
		float[] mProb;
		
		
		//int b = path.lastIndexOf(File.separator);
		//datasetName = path.substring(0, b+1)+datasetName;
		datasetName = path+File.separator+datasetName;
		
		System.out.println("Creating networks ... ");
		System.out.println("Output available at "+datasetName+"_corr"+type+".ds2");
		
		for(int i=0; i<nSamples; i++) {
			System.out.println("Sample #"+(i+1));
			//System.out.print("Finding correlation matrix ...");
			mCorr=NetworkBuilder.findCorrelation(data[i],tau_s);
			//System.out.println(" Done");
			//System.out.println("Computing probability matrix ...");
			mProb=NetworkBuilder.probMatrix(data[i],mCorr,tau_r);
			
			System.out.println("Num edges: "+nEdges);
			nEdges=0;
						
			//Scrittura su file mCorr
			try {
				SaveBinaryFile.saveAsShort(datasetName+"_corr"+type+".ds2",mCorr, nSamples, i==0?true:false);
			} catch (IOException e) {
				System.out.println("Impossibile scrivere su "+datasetName+"_corr.ds2");
				e.printStackTrace();
			}
			if(saveProb)
				try {
					SaveBinaryFile.saveAsShort(datasetName+"_prob"+type+".ds2",mProb,nSamples,i==0?true:false);
				} catch (IOException e) {
					System.out.println("Impossibile scrivere su "+datasetName+"_prob.ds2");
					e.printStackTrace();
				}	
		}//for
		
		System.out.println("globalEdge: "+globalEdge);
		System.out.println();
		
	}
	
	public static void main(String[] args) {
		String path="";
		String datasetName="";
		char type='H';
		float tau_s = 0.7f;
		float tau_r = 0.9f;
		boolean saveProb = false;
		
		if(args.length > 2){
			path = args[0];
			datasetName = args[1];
			type = args[2].charAt(0);
		}else{
			usage();
		}
			
		if(args.length > 3){
			for(int i=3; i<args.length; i++){
				if(args[i].equals("-tau_s")){
					i++;
					if(i<args.length)
						tau_s = Float.parseFloat(args[i]);
					else{//errore
						usage();
					}
				}else if(args[i].equals("-tau_r")){
					i++;
					if(i<args.length)
						tau_r = Float.parseFloat(args[i]);
					else{//errore
						usage();
					}
				}else if(args[i].equals("-saveProb")){
					i++;
					if(i<args.length)
						saveProb = Boolean.parseBoolean(args[i]);
					else{//errore
						usage();
					}
				}
			}//for
		}//if
		
		create_networks(path,datasetName,type,tau_s,tau_r,saveProb);
	}//main
}



