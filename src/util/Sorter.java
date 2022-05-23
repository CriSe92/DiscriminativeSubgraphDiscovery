package util;


public class Sorter {
	
	public static int[] heap_sort(float[] array) {
		int[] index = new int[array.length];
		
		for(int i=0; i<array.length; i++) {
			index[i] = i;
		}
		
	    for(int i = 0; i<array.length;i++) {
	    	add(array,index,array[i],i+1);
	    }
	    
	    //Metto il minimo in coda e risistino l'heap
	    for(int i=0; i<array.length; i++) {
	    	//int removed = remove(array,index,0);
	    	array[array.length-1-i] = remove(array,index,0);;
	    }
		return index;
	}
	
	
	static int last=0; /*Indice dell'elemento libero più a destra*/	
	public static boolean add(float[] heap, int[] index_vector, float elem, int size) {

		if (last == size) {
			if(heap[0]>elem)
				return false;
			remove(heap,index_vector,0);
		}

		heap[last] = elem;
		last++;
		
		int i = last-1;
		float tmp;
		int tmp_index;
		//Finché mio padre è più grande (o non raggiungo la radice)
		while ((i > 0) && (heap[(i-1)/2]>(heap[i]))) {
				tmp = heap[i];
				heap[i] = heap[(i-1)/2];
				heap[(i-1)/2] = tmp;
				
				tmp_index = index_vector[i];
				index_vector[i] = index_vector[(i-1)/2];
				index_vector[(i-1)/2] = tmp_index;
				
				i = (i-1)/2;
		}//while
		return true;
	} // add
	

	public static float remove(float[] heap, int[] index_vector,int elem) {
		last--;
		float removed = heap[elem];		
		heap[elem]=heap[last];
		
		int i=elem;
		float tmp;
		int tmp_index;
		
		tmp_index = index_vector[elem];
		index_vector[elem] = index_vector[last];
		index_vector[last] = tmp_index;
	  

		
		//L'elemento deve scendere
		while(i<last && ((2*i+1<last && heap[2*i+1]<heap[i]) || (2*i+2<last && heap[2*i+2]<heap[i]))){
			  if(!(2*i+2<last) || heap[2*i+2]>heap[2*i+1]){
				  tmp=heap[i];
                  heap[i]=heap[2*i+1];
                  heap[2*i+1]=tmp;
                  
  				  tmp_index = index_vector[i];
  				  index_vector[i] = index_vector[2*i+1];
  				  index_vector[2*i+1] = tmp_index;
                  
                  i=2*i+1;
            }else{
            	//scambia con il figlio desto
                  tmp=heap[i];
                  heap[i]=heap[2*i+2];
                  heap[2*i+2]=tmp;
                  
                  tmp_index = index_vector[i];
  				  index_vector[i] = index_vector[2*i+2];
  				  index_vector[2*i+2] = tmp_index;
                  
                  
                  i=2*i+2;
             }
		}//while
		
		// L'elemento deve salire
		while(i>0 && heap[(i-1)/2]>heap[i]){
			tmp=heap[i];
            heap[i]=heap[(i-1)/2];
            heap[(i-1)/2]=tmp;
            
            tmp_index = index_vector[i];
            index_vector[i] = index_vector[(i-1)/2];
			index_vector[(i-1)/2] = tmp_index;
               
            i=(i-1)/2;
		}
		
		return removed;
	} // remove
	
	public static void main(String[] args) {
		float[] v = {7.0f,5.0f,5.0f,3.0f,5.0f,9.0f};
		System.out.println("VETTORE");
	    for(int i=0; i<v.length; i++) {
	    	System.out.print(v[i]+" ");
	    }
	    System.out.println();
		int[] index = heap_sort(v);
		System.out.println("VETTORE ORDINATO");
	    for(int i=0; i<v.length; i++) {
	    	System.out.print(v[i]+" ");
	    }
	    System.out.println();
		System.out.println("INDEX");
	    for(int i=0; i<index.length; i++) {
	    	System.out.print(index[i]+" ");
	    }
	}
}
