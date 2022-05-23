package util;

import java.util.HashMap;

public class SparseTriangularMatrix {
	
	class Coordinata implements Comparable<Coordinata>{
		int r, c;
		
		public Coordinata(int r, int c){
			this.r=r;
			this.c=c;
		}
		
		public int getRiga(){return r;}
		public int getColonna(){return c;}

		@Override
		public int compareTo(Coordinata other) {
			if(this.r>other.getRiga()) return 1;
			if(this.r<other.getRiga()) return -1;
			if(this.r==other.getRiga()){
				if(this.c>other.getColonna()) return 1;
				if(this.c<other.getColonna()) return -1;
			}
			return 0;
		}
		
		public int hashCode(){
			return c;
		}
	}
	
	
	private int dim;
	private HashMap<Integer, Float> matrix = new HashMap<>();
	
	public SparseTriangularMatrix(int dim){
		this.dim=dim;
	}
	
	public void put(int r, int c, float value){
		matrix.put(index(r,c), value);
	}
		
	public float get(int r, int c){
		Float v = matrix.get(index(r,c));
		if(v==null) return 0;
		return v;
	}
	
	private int index(int r, int c){
		return dim*r+c;
	}
	
	public void print(){
		for(int i=0; i<dim; i++){
			for(int j=0; j<=i; j++){
				System.out.print(get(i,j)+"\t");
			}
			System.out.println("\n");
		}
	}
}
