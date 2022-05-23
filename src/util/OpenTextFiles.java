package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class OpenTextFiles {

	public static int[] discoverSize(String nomeFile,String delim) throws IOException {
		int[] dim = new int[2];//dim[0] = numrows dim[1] = numcols
		BufferedReader reader = new BufferedReader(new FileReader(nomeFile));
		StringTokenizer st = null;
		String line = null;
		String lastline = null;
		for(;;) {
			lastline = line;
			line = reader.readLine();
			if(line==null) break;
			dim[0]++;
		}
		st = new StringTokenizer(lastline, delim);
		while(st.hasMoreTokens()) {
			st.nextToken(delim);
			dim[1]++;
		}
		reader.close();
		return dim;
	}
	
	public static float[][] openTriangularDataset(String nomeFile, String delim) throws IOException{
		int[] dim = discoverSize(nomeFile, delim);
		BufferedReader br = new BufferedReader(new FileReader(nomeFile));
		float[][] matrix = new float[dim[0]][dim[1]];
		StringTokenizer st = null;
		String line = null;
		for(int i=0; i<matrix.length; i++){
			line = br.readLine();
			st = new StringTokenizer(line, delim);
			for(int j =0; j<=i && st.hasMoreTokens(); j++){
				String token = st.nextToken();
				float num = Float.parseFloat(token);
				matrix[i][j] = num;
			}
		}
		br.close();
		return matrix;
	}
	
	public static float[][] openDataset(String nomeFile, String delim) throws IOException{
		int[] dim = discoverSize(nomeFile, delim);
		BufferedReader br = new BufferedReader(new FileReader(nomeFile));
		float[][] matrix = new float[dim[0]][dim[1]];
		StringTokenizer st = null;
		String line = null;
		for(int i=0; i<matrix.length; i++){
			line = br.readLine();
			st = new StringTokenizer(line, delim);
			for(int j =0; j<matrix[0].length && st.hasMoreTokens(); j++){
				String token = st.nextToken();
				float num = Float.parseFloat(token);
				matrix[i][j] = num;
			}
		}
		br.close();
		return matrix;
	}

}

