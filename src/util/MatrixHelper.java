package util;

public class MatrixHelper {
	
	/*Crea una matrice triangolare inferiore di float*/
	public static float[][] createMatrix(int nr){ 
		float[][] grafo  = new float[nr][];
		for(int j=0; j<nr; j++)
				grafo[j] = new float[j+1];
		return grafo;
	}
	
	public static int getIndex(int r, int c, int nr) {
		return c*nr-c*(c+1)/2+r-(c+1);
	}

}
