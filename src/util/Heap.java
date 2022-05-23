package util;

import java.util.*;

import model.Dataset;
import model.Pattern;

/*
 * Min_Heap,
 * Sarà usato per tenere i top-k patterns, cioè quelli aventi potere discriminante più elevato
 */
public class Heap<T extends Comparable<? super T>> implements Iterable<T>{
	private T[] heap;
	private int last=0; /*Indice dell'elemento libero più a destra*/
	private int size;
	private float soglia=-1;
	private boolean iterator_mode=false;
	private Comparator<T> c;
	
	public Heap(int size) {
		this(size, new Comparator<T>() { // Default: natural sorting
			public int compare(T a, T b) { return a.compareTo(b); }
		});
	} // Costruttore
	
	@SuppressWarnings("unchecked")
	public Heap(Heap<T> h) {
		this.last = h.getLength();
		this.size = h.getSize();
		this.soglia = h.getSoglia();
		this.c = h.getComparator();
		
		heap = (T[])new Comparable[size];
		T[] source = h.getArray();
		for(int i=0; i<size; i++) {
			heap[i] = source[i];
		}
		
	} // Costruttore
	

	@SuppressWarnings("unchecked")
	public Heap(int size, Comparator<T> c) {
		if (size <= 0) throw new IllegalArgumentException();
		this.size = size;
		heap = (T[])new Comparable[size];
		this.c = c;
	} // Costruttore 2
	
	public int getSize() { return size;}
	
	public int getLength() { return last;}
	
	public boolean isEmpty(){ return last==0; }
	
	public T getMin(){ return heap[0]; }
	
	public void setSoglia(float soglia) {
		this.soglia = soglia;
	}
	
	public T getElement(int pos) {
		//if(pos>=last) throw new IllegalArgumentException("Element at position "+pos+" not available!");
		return heap[pos];
	}
	
	public void resetElement(int pos) {
		heap[pos] = null;
	}
	
	//public int getLast() { return last; }
	
	public T[] getArray() { return heap; }
	
	public void reset() {last = 0;}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public Comparator<T> getComparator(){
		return c;
	}
	
	public boolean add(T elem) {
		if(((Pattern)elem).getDiscriminativePower()<soglia)/*sostituito <= con <*/
			return false;
		
		if (last == size) {
			if(heap[0].compareTo(elem)>0)
				return false;
			remove(0);
			
			if(soglia<0) soglia=0;//Inizio a lavorare sulla soglia quando l'heap è pieno
		}

		heap[last] = elem;
		last++;
		
		int i = last-1;
		T tmp; 
		//Finché mio padre è più grande (o non raggiungo la radice)
		while ((i > 0) && (heap[(i-1)/2].compareTo(heap[i]) > 0)) {
				tmp = heap[i];
				heap[i] = heap[(i-1)/2];
				heap[(i-1)/2] = tmp;
				i = (i-1)/2;
		}//while
	
		if(last == size || soglia>=0 && !iterator_mode)soglia=Math.max(soglia,((Pattern)this.getMin()).getDiscriminativePower());
		return true;
	} // add
	
	// elem vale 0 per la tradizionale rimozione dalla testa
	public T remove(int elem) {
		last--;
		T removed = heap[elem]; 
		heap[elem]=heap[last];
		int i=elem;
		T tmp;
		
		//L'elemento deve scendere
		while(i<last && ((2*i+1<last && heap[2*i+1].compareTo(heap[i])<0) || (2*i+2<last && heap[2*i+2].compareTo(heap[i])<0))){
			  if(!(2*i+2<last) || heap[2*i+2].compareTo(heap[2*i+1])>0){
                  tmp=heap[i];
                  heap[i]=heap[2*i+1];
                  heap[2*i+1]=tmp;
                  i=2*i+1;
            }else{
                  tmp=heap[i];
                  heap[i]=heap[2*i+2];
                  heap[2*i+2]=tmp;
                  i=2*i+2;
             }
		}//while
		
		// L'elemento deve salire
		while(i>0 && heap[(i-1)/2].compareTo(heap[i])>0){
			tmp=heap[i];
            heap[i]=heap[(i-1)/2];
            heap[(i-1)/2]=tmp;
            i=(i-1)/2;
		}
		
		return removed;

	} // remove
	
	public float getSoglia() {
		return soglia;
	}
	
	public Iterator<T> iterator() { return new HeapIterator(); }
	
	public String toString() {
		StringBuilder sb = new StringBuilder(500);
		sb.append('[');
		for (T e: this) {
			sb.append(e); sb.append(",\n");
		}
		sb.append(']');
		return sb.toString();
	} // toString
	

	public boolean contains(T elem) {
		for(int i=0; i<this.getLength(); i++) {
			if(heap[i].equals(elem)) return true;
		}
		return false;
	}
	
	public void sorted_print() {//STAMPA MA DISTRUGGE ... OCCORRE DEFINIRE IL COSTRUTTORE DI COPIA PER STAMPARE (E SVUOTARE) LA COPIA
		Iterator<T> it = this.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
			it.remove();
		}
	}

	public void print() {
		for(int i=0; i<this.last; i++) {
			System.out.println("Pattern#"+(i+1)+" "+heap[i]);
		}
	}
	
	private class HeapIterator implements Iterator<T> {
		int corrente = 0;
		boolean rimovibile=false;
		
		public boolean hasNext() {
			return corrente < last;
		} // hasNext
		
		public T next() {
			if (!hasNext()) throw new NoSuchElementException();
			rimovibile = true;
			T pCurr = heap[corrente];
			corrente++;
			return pCurr;
		} // next
		
		public void remove() {
			if (!rimovibile) throw new IllegalStateException();
			else{
				rimovibile=false;
				corrente--;
				heap[corrente] = null;
				int tmp = last-1; last = corrente;
				iterator_mode=true;
				for (int i = corrente + 1; i <= tmp; i++) add(heap[i]);
				if(soglia>=0 && !Heap.this.isEmpty())soglia=Math.max(soglia,((Pattern)Heap.this.getMin()).getDiscriminativePower());
				iterator_mode=false;
				heap[tmp] = null;
			}
		} // remove
	} // HeapIterator

	public static void main(String[] args) {
	
		Heap<Pattern> h = new Heap<Pattern>(5);
		Dataset.edgeMapping(50);
		for(int i=1; i<=5;i++){
			Pattern p = new Pattern(i);
			p.setDiscriminativePower((float)(5-i)/10);
			h.add(p);
		}
		
		System.out.println("HEAP");
		h.print();
		
		Iterator<Pattern> it = h.iterator();
		while(it.hasNext()){
			Pattern p = it.next();
			if(p.getDiscriminativePower()<0.3)
				it.remove();
		}
		
		System.out.println("HEAP");
		h.print();
		System.out.println("SOGLIA "+h.getSoglia());
	}

} // Heap

