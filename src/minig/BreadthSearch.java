package minig;

import java.io.IOException;
import java.util.ArrayList;
import application.Main;
import model.Pattern;
import util.OpenBinaryFiles;

public class BreadthSearch {
	
	//Riempio l'heap con i pattern monodimensionali
	public static ArrayList<Pattern> orderEdges(int[] index, String path_data, String path_supp) throws IOException{
		float[][] data = OpenBinaryFiles.openMatrix(path_data);
		boolean[][] support = OpenBinaryFiles.openLogicMatrix(path_supp);
		System.out.println(support.length+"  "+support[0].length); //<NUM ARCHI,NUM CAMPIONI>
			
		int size = data.length;
		final int comm=0;
		final int ds=1;
		final int ub=2;
		//Inserire id grafi che contengono il pattern
		ArrayList<Pattern> orderedEdges = new ArrayList<Pattern>(size);
		for(int i=0; i<size;i++){
			Pattern p = new Pattern(1);
			p.addEdge(index[i]);
			p.setCommonness(data[i][comm]);
			p.setDiscriminativePower(data[i][ds]);
			p.setUpperBound(data[i][ub]);			
			for(int k=0;k<support[i].length; k++){
				if(support[i][k])
					p.addGraph(k);
			}
			orderedEdges.add(p);
			//Riempimento heap
			Main.patterns.add(p);
		}

//L'ordinamento è fatto in fase di preprocessing già in ambiente matlab
//		Collections.sort(orderedEdges, new Comparator<Pattern>(){
//			public int compare(Pattern x, Pattern y){
//				if(y.getCommonness()-x.getCommonness()>0) return 1;
//				if(y.getCommonness()-x.getCommonness()<0) return -1;
//				return 0;
//			}
//		});
		
		//System.out.println(patterns);
		System.out.println("MINIMO "+Main.patterns.getSoglia());
		return orderedEdges;
	}

}
