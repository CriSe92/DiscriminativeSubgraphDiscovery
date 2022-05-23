package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;

import application.ExperimentInfo;
import util.BinaryVector;

public class Pattern implements Comparable<Pattern>,Serializable{
	

	private static final long serialVersionUID = -8545098274956713522L;
	
	private int[] edges;
	private int[] nodes;
	//private ArrayList<Integer> graphID = new ArrayList<Integer>();//sostituire con array binario, ma occorre sapere quanti campioni ci sono
	//private boolean[] graphID = new boolean[ExperimentInfo.id=='H'?ExperimentInfo.numCampioniSani:ExperimentInfo.numCampioniMalati];
	private BinaryVector graphID; 
	//private int support=0; //Disponibile all'interno di BinaryVector
	private float upperBound;
	//private int size;
	private float commonness;
	private float commonness_other;
	private float discriminativePower;
	//private int in; //Se in=-1 il pattern si considera chiuso e non più espandibile
	private int numNodi=0;
	private int lastAddedNode=-1;
	private int lastAddedEdge=-1;
	boolean isBest=false;
	//boolean isIn = false;
	
	public static String geneNamesMapping=null;
	static String[] geneNames;
	private boolean error=false;
	
	public Pattern(int size){
		//this.size=size;
		edges=new int[size];
		nodes= new int[size+1]; //Se ci sono size archi nel caso peggiore di pattern rappresentato da una catena ci saranno size+1 nodi
		graphID =  new BinaryVector(ExperimentInfo.id=='H'?ExperimentInfo.numCampioniSani:ExperimentInfo.numCampioniMalati);
	}
	
	public Pattern(String p) {
		//'A_23_P66844','A_23_P54861', (52310485) , 'A_24_P693461','A_23_P54861', (126852235)
		StringTokenizer st = new StringTokenizer(p,"()");
		int num_archi=0;
		while(st.hasMoreTokens()) {
			st.nextToken();
			if(st.hasMoreTokens()) {
				st.nextToken();
				num_archi++;
			}
		}
		edges=new int[num_archi];
		nodes= new int[num_archi+1]; //Se ci sono size archi nel caso peggiore di pattern rappresentato da una catena ci saranno size+1 nodi
		
		st = new StringTokenizer(p,"()");
		while(st.hasMoreTokens()) {
			st.nextToken();
			if(st.hasMoreTokens()) {
				this.addEdge(Integer.parseInt(st.nextToken()));
			}
		}
	}
	
	public Pattern(Pattern p){
		this(p.size());
		int[] e = p.getEdges();
		for (int i = 0; i < e.length; i++) {
			edges[i]=e[i];
		}
		graphID = new BinaryVector(p.getGraphID());
		//in=-1;
	}
	
	public Pattern(Pattern p, int size){
		this(size);
		int[] e = p.getEdges();
		for (int i = 0; i < e.length; i++) {
			edges[i]=e[i];
		}
		int[] n = p.getNodes();
		for (int i = 0; i < n.length; i++) {
			nodes[i]=n[i]; 
			numNodi++;
		}
		//if(e.length<size) in=e.length;
		//else in=-1;
	}
	
	//restituisce il nodo a cui è stato attaccato il nuovo arco
	public int addEdge(int edge){
		//if(in==-1) throw new RuntimeException("Impossible aggiungere l'arco");
		if(edges.length==1) edges[0]=edge;
		int n1 = Dataset.edgeMapping[0][edge];
		int n2 = Dataset.edgeMapping[1][edge]; // per costruzione n2<n1
		int newNode=-1;
		if(numNodi==0){//Il pattern è ancora vuoto
			nodes[0]=n2;
			nodes[1]=n1;
			numNodi=2;
			edges[0]=edge;
		}else{
			for(int i=0; i<edges.length; i++) {
				if(edges[i]>edge || (edges[i]==0 && i>0)) {
					int e = edges[i];
					edges[i]=edge;
					shift(e, i+1);
					break;
				}
			}
			boolean trovato=false;
			for(int i=0; i<numNodi; i++){//Individuo il nuovo nodo da aggiungere			
				if(nodes[i]<n2)
					continue;
				if(nodes[i]==n2){
					newNode=n1; //Devo trovare la posizione giusta e aggiungere n1
					trovato=true;
				}else if(nodes[i]>n2 && !trovato){//n2 non trovato
					newNode=n2; //Devo aggiungere n2 qui e terminare
					int n = nodes[i];
					nodes[i]=n2;
					addNode(n, i+1);
					break;
				}else{
					//Cerca posizione per n1
					if(nodes[i]>n1){
						//newNode=n1; QUESTA è la giusta posizione, correggere e gestire opportunamente cosa accade in computeEdgeNeighbos
						int n = nodes[i];
						nodes[i]=n1;
						addNode(n, i+1);
						break;
					}
				}
			}
		}
		lastAddedEdge=edge;
		lastAddedNode=newNode;
		//in=-1;
		return newNode;
	}
	
	public int getLastAddedNode(){
		return lastAddedNode;
	}
	
	public int getLastAddedEdge(){
		return lastAddedEdge;
	}
	
	private void shift(int element, int index) {
		for(int i=index; i<edges.length; i++) {
			int e = edges[i];
			edges[i]=element;
			element=e;
		}
	}
	
	private void addNode(int element, int index) {
		for(int i=index; i<nodes.length; i++) {
			int e = nodes[i];
			nodes[i]=element;
			element=e;
		}
	}
	
	public void addGraph(int id){
		//if(graphID.contains(id)) throw new IllegalArgumentException();
		//graphID.add(id);
		graphID.set(id);
		//support++;
	}
	
	public void setSupportVector(BinaryVector v) {
		//support = v.get_count_one();
		graphID = new BinaryVector(v);
	}
	
	public BinaryVector getGraphID(){
		return graphID;
	}
	
//	public void setSupport(int support) {
//		this.support = support;
//	}
	

	public int[] getEdges() {
		return edges;
	}
	
	public int getSize(){
		return edges.length;
	}
	
	public int[] getNodes(){
		return nodes;
	}

	public float getUpperBound() {
		return upperBound;
	}
	
	public int getSupport() {
		if(graphID!=null)
			return graphID.get_count_one();
		return -1;
		//return support;
	}

	public void setUpperBound(float upperBound) {
		this.upperBound = upperBound;
	}

	public int size() {
		return edges.length;
	}
	
	public int numNodi(){
		return numNodi;
	}
	
	public boolean getIsBest(){
		return isBest;
	}
	
	public void setIsBest(boolean isBest){
		this.isBest=isBest;
	}
	
	public int[] getPattern(){
		return edges;
	}
	
	public void setCommonness(float s){
		commonness = s;
	}
	
	public void setCommonness_other(float s){
		commonness_other = s;
	}
	
	public float getCommonness(){
		return commonness;
	}
	
	public float getCommonness_other(){
		return commonness_other;
	}
	
	public void setDiscriminativePower(float e){
		discriminativePower = e;
	}

	public float getDiscriminativePower(){
		return discriminativePower;
	}

	/*
	 * Il metodo restituisce true se p è contenuto in this
	 */
	public boolean contains(Pattern p){
		int[] e = p.getEdges();
		if(e.length>edges.length) return false;
		int j=0;
		for (int i = 0; i < edges.length; i++) {
			if(edges[i]<e[j]) continue;
			if(edges[i]==e[j]) {
				j++;
				if(j==e.length) return true;
			}
			if(edges[i]>e[j]) return false;
		}
		return false;
	}
	

	private void instantiateGeneNames() throws IOException{
		if(ExperimentInfo.geneNames==null) throw new IOException();
		BufferedReader br = new BufferedReader(new FileReader(ExperimentInfo.geneNames));
		String line = br.readLine();
		int count=0;
		while(line != null){
			line = br.readLine();
			count++;
		}
		br.close();
		geneNames = new String[count];
		br = new BufferedReader(new FileReader(ExperimentInfo.geneNames));
		for(int i=0; i<count; i++){
			geneNames[i]=br.readLine();
		}
		br.close();
	}
	
	public String getPatternInfo_geneNames(){
		if(geneNames==null){
			try{
				instantiateGeneNames();
			}catch(IOException e){
				//e.printStackTrace();
				error = true;
				return getPatternInfo_edgesCode();
			}
		}
		StringBuilder sb = new StringBuilder(500);
		sb.append("Num. archi: "+edges.length+" ");
		sb.append("[");
		for(Integer e: edges){
			//map node numbers to node names
			int g1 = Dataset.edgeMapping[0][e];
			int g2 = Dataset.edgeMapping[1][e];
			sb.append("'"+geneNames[g1]+"','"+geneNames[g2]+"'"+", ("+e+") "+", ");
		}
		sb.replace(sb.length()-2, sb.length(), "] ");
		sb.append(this.getCommonness()+" "+this.getCommonness_other()+" "+this.getDiscriminativePower()+" "+this.getUpperBound()+" "+this.getSupport());
		
		return sb.toString();
	}
	
	
	public String getPatternInfo_edgesCode(){
		if(!error && ExperimentInfo.geneNames != null){
			return getPatternInfo_geneNames();
		}
		StringBuilder sb = new StringBuilder(500);
		sb.append("Num. archi: "+edges.length+" ");
		sb.append("[");
		for(Integer e: edges){
			sb.append(e+", ");
		}
		sb.replace(sb.length()-2, sb.length(), "] ");
		sb.append(this.getCommonness()+" "+this.getCommonness_other()+" "+this.getDiscriminativePower()+" "+this.getUpperBound()+" "+this.getSupport());
		return sb.toString();
	}
	
	
	private String toGeneNames(){
		if(geneNames==null){
			try{
				instantiateGeneNames();
			}catch(IOException e){
				error = true;
				return toString();
			}
		}
		
		StringBuilder sb = new StringBuilder(500);
		sb.append("Num. archi: "+edges.length+" ");
		sb.append("[");
		for(Integer e: edges){
			//map node numbers to node names
			int g1 = Dataset.edgeMapping[0][e];
			int g2 = Dataset.edgeMapping[1][e];
			sb.append("'"+geneNames[g1]+"','"+geneNames[g2]+"'"+", ("+e+") "+", ");
		}
		sb.replace(sb.length()-2, sb.length(), "] ");
		//sb.append("] ");
		sb.append("Score: "+this.getCommonness());
		sb.append(" Score (other population): "+this.getCommonness_other());
		sb.append(" Discriminative Power: "+this.getDiscriminativePower());
		sb.append(" Upper Bound: "+this.getUpperBound());
		sb.append(" Support: "+this.getSupport());
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o){
		//System.out.println("Pattern found");
		if (this == o) return true;
		if (! (o instanceof Pattern)) return false;
		Pattern p = (Pattern)o;
		if(this.edges.length!=p.edges.length /*|| this.commonness!=p.getCommonness()*/)
			return false;
		//Controllo che i due patterns contengano gli stessi archi (poichè gli archi sono ordinati per costruzione se i due pattern sono ugali avranno archi uguali nelle stesse posizioni)
		for(int i=0; i<this.edges.length; i++){ 
			if(this.edges[i]!=p.edges[i])
				return false;
		}
		return true;
	}
	
	/*
	 * Più l'entropia H(N|P) è elevata, meno il pattern è discriminante. 
	 */
	@Override
	public int compareTo(Pattern p) {
		if(p==null) return 1;
		if (this.equals(p)) return 0;
		if (this.getDiscriminativePower()==p.getDiscriminativePower()){
			if (this.commonness>p.getCommonness()) return 1;
			return -1;
		}else{
			if (this.getDiscriminativePower()>p.getDiscriminativePower()) return 1;
			return -1;
		}
	}
	
	
	
	public String toString(){
		if(!error && ExperimentInfo.geneNames != null){
			return toGeneNames();
		}
		StringBuilder sb = new StringBuilder(500);
		sb.append("Num. archi: "+edges.length+" ");
		sb.append("[");
		for(Integer e: edges){
			sb.append(e+", ");
		}
		sb.append("] ");
		sb.append("Score: "+this.getCommonness());
		sb.append(" Score (other population): "+this.getCommonness_other());
		sb.append(" Discriminative Power: "+this.getDiscriminativePower());
		sb.append(" Upper Bound: "+this.getUpperBound());
		sb.append(" Support: "+this.getSupport());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for(Integer i:edges){
			result = prime * result + i.hashCode();
		}
		return result;
	}
	
	public static void main(String[] args) {
		Pattern p1 = new Pattern(1);
		Pattern p2 = new Pattern(1);
		
		p1.addEdge(1);
		p2.addEdge(2);
		
		p1.setDiscriminativePower(0.5f);
		p2.setDiscriminativePower(1);
		
		if(p1.compareTo(p2)<0)
			System.out.println("TRUE");
		else
			System.out.println("FALSE");
	}
}
