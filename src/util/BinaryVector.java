package util;

import java.io.Serializable;

public class BinaryVector implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6706259159244783983L;
	
	static int intsize = 32;
	//int size;
	int count_one=0;
	int[] bitv=null;
	
	public BinaryVector() {
		
	}
	
	public BinaryVector(int n) {
        int size = (int) Math.ceil(((double)n/intsize));
        bitv = new int[size];
        for(int i=0; i<size; i++)
            bitv[i] = 0;
    }
	
	public BinaryVector(BinaryVector b){
        //this.size = b.getSize();
        this.count_one = b.count_one;
		bitv = new int[b.getSize()];
        for(int i=0; i<bitv.length; i++){
            bitv[i]=b.getInt(i);
        }
    }
	
	public int getSize() {
		return bitv.length;
	}
	
	public int get_count_one() {
		return count_one;
	}
	
	public int getInt(int i){
       if(i>bitv.length) throw new IllegalArgumentException("Index exceed array size");
       return bitv[i];
    }
	
    public void set(int i){
        int slot = i/intsize;
        int pos = 1;
        pos = (pos << ((slot+1)*intsize - i - 1));
        if(!((bitv[slot] & pos) == pos))
            count_one++;
        bitv[slot] = bitv[slot] | pos;
    }
    
    public void clear(){
        for(int i=0; i<bitv.length; i++){
            bitv[i] = 0;
        }
        count_one=0;
    }
    
    public void reset(int i){
        int slot = i/intsize;
        int pos = 1;
        pos = (pos << ((slot+1)*intsize -i - 1));
        if(((bitv[slot] & pos) == pos))
            count_one--;
        bitv[slot] = bitv[slot] & (~pos);
    }
    
    public boolean get(int i){
        int slot = i/intsize;
        int pos = 1;
        pos = (pos << ((slot+1)*intsize -i - 1));
        return (bitv[slot] & pos) == pos;
    }
    
    public boolean equals(BinaryVector b){
        if(getSize() != b.getSize() || count_one != b.get_count_one()) return false;
        for(int i=0; i<getSize(); i++)
            if(bitv[i]!=b.getInt(i))
                return false;
        return true;
    }
    
    public void print() {
    	 int mask;
    	          
         for(int i=0; i<bitv.length; i++){
             mask = (1 << (intsize-1));//porto 1 in prima posizione
             System.out.print("[");
             for(int l=0; l<intsize; l++){
//                 System.out.print(bitv[i]+" ---  "+mask+" BIT: ");
//                 int rslt = (int)( bitv[i] & mask ) ;
//                 System.out.println("NUMBER "+rslt);
                 if((bitv[i] & mask)==0) {
                	 System.out.print('0');
                 }else {
                	 System.out.print('1');
                 }
            	 mask = (mask >>> 1);
             }
             System.out.print("]\n");
         }
    }
    
    public static void main(String[] args) {
		BinaryVector b = new BinaryVector(5);
		System.out.println(b.getSize());
		System.out.println("PRIMA DI INSERIRE");
		b.print();
		b.set(27);
		System.out.println("DOPO L'INSERIMENTO");
		b.print();
		System.out.println(b.getInt(0));
	}
}