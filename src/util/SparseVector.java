package util;

import java.util.HashMap;
import java.util.Set;


public class SparseVector {
	
	//private int dim; //NumGeni
	private HashMap<Integer, Short> vector = new HashMap<>();
	
	public SparseVector(){}
	
//	public SparseVector(int dim){
//		this.dim=dim;
//	}
	
	public void put(int index, short value){
		vector.put(index, value);
	}
	
//	public int getDim(){
//		return dim;
//	}
		
	public float get(int index){
		Short v = vector.get(index);
		if(v==null) return 0;
		return NumberHelper.toFloat(v);
	}
	
	public Set<Integer> getKeys(){
		return vector.keySet();
	}
	
	public int size(){
		return vector.keySet().size();
	}
	
	public void print(){
		//TODO
	}
}
