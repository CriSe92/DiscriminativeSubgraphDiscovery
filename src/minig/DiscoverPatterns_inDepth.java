package minig;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import application.ExperimentInfo;
import application.Main;
import model.Dataset;
import model.Pattern;
import util.Heap;


public class DiscoverPatterns_inDepth {

	private Dataset main, other;
	private float sogliaDiscriminativePower = 0;//0.6254462f;
	private int dimMax = 1;
	private int previousRun_dimMax = 0;
	private static boolean hard_check = false;
	
//	public static Pattern edge1 = new Pattern(1);
//	public static Pattern edge2 = new Pattern(1);
//	public static Pattern edge3 = new Pattern(1);
//	public static Pattern p6 = new Pattern(6);
//	public static Pattern p4 = new Pattern(4);
//	public static Pattern p2 = new Pattern(2);
////	//ANALISI PATTERN Num. archi: 3 [105112858, 126852856, 126857127, ] Score: 28.978466 Score (other population): 0.0 Discriminative Power: 0.7680275 Upper Bound: 0.7680275
////
//	static {
//		p6.addEdge(13814220);
//		p6.addEdge(46146501);
//		p6.addEdge(52310485);
//		p6.addEdge(77400717);
//		p6.addEdge(105112858);
//		p6.addEdge(126857127);
//		
//		p4.addEdge(13814220);
//		p4.addEdge(46146501);
//		p4.addEdge(52310485);
//		p4.addEdge(77400717);
//		
//		p2.addEdge(52310485);
//		p2.addEdge(105112858);
//	}

	
	private int level;

	public DiscoverPatterns_inDepth(Dataset main, Dataset other) {
		this.main = main;
		this.other = other;
	}

	public void set_soglia_discriminative_power(float soglia_dp) {
		sogliaDiscriminativePower = soglia_dp;
	}
	
	public void set_maxDept(int dimMax) {
		//if(hard_check) previousRun_dimMax = Integer.MAX_VALUE;
		//else 
		previousRun_dimMax = this.dimMax;
		this.dimMax = dimMax;
	}
	
	public void set_hard_check(boolean hard_check) {
		DiscoverPatterns_inDepth.hard_check = hard_check;
	}
	
	public void reset_pattern_dimension() {
		previousRun_dimMax = 0;
		dimMax = 1;
	}
	
	public void discoverPatterns() {
		level=0;
		if(ExperimentInfo.verbose>1) {
			System.out.println("==== HEAP ====");
			Main.patterns.print();
			System.out.println("=============");
		}
		patternMining(0, 0, Preprocessing.orderedEdges,new boolean[Dataset.getNumArchi()]);
	}// discoverInterestingPatterns
	
	
	private void patternMining(float best_ds, float best_comm, List<Pattern> orderedChildren, boolean[] alreadyVisisted) {
		level++;
		Statistics_collector.maxDepth = (level > Statistics_collector.maxDepth) ? level : Statistics_collector.maxDepth;
		
		int i = 0;
		for (i = 0; i < orderedChildren.size(); i++) {
			Pattern child = orderedChildren.get(i);
			
			/**AGGIUNTA*/
			if (canAdd(child, best_ds, best_comm)) {
//				child.setIsBest(true);
//				if(child.size()>previousRun_dimMax && canAdd_hardCheck(child)) {
//					Main.patterns.add(child);
//				}
				containsSuperPattern(child);
			}
			
//			if(child.equals(p6) || child.equals(p4) || child.equals(p2)){
//				System.out.println(child);
//				System.out.println("=== HEAP ===");
//				Main.patterns.print();
//				System.out.println("=============");
//			}
//			
	
			/**ESPANSIONE*/
			if (prosegui(child.getUpperBound(), getSoglia())) {
				if(child.size() < dimMax){
					alreadyVisisted[child.getLastAddedEdge()] = true;
					float discriminativePower = best_ds;
					float best_comm_new = best_comm;
					if (child.size() == 1 && ExperimentInfo.verbose>=1){
						System.out.println("ARCO#" + i + " SOGLIA CORRENTE " + getSoglia());
						System.out.println(child);
					}
					//stampa_alreadyVisited(alreadyVisisted,level);
					if (child.getIsBest()) {
						discriminativePower = child.getDiscriminativePower();
						best_comm_new = child.getCommonness();
					}

					List<Integer> neigh = getNeighborhood((child.size() == 1) ? null : orderedChildren, alreadyVisisted,child.getLastAddedEdge(), child.getLastAddedNode());
					ArrayList<Pattern> children = orderChildren(child, neigh, ExperimentInfo.tau_r, discriminativePower,best_comm_new);
				
					if(ExperimentInfo.verbose>1) {
						//System.out.println("LIV#" + level + "SOGLIA REALE " + Main.patterns.getMin().getDiscriminativePower()+" SOGLIA USATA: "+getSoglia());
						System.out.println("LIV#" + level + " NUM. FIGLI " + children.size() + " DI " + child);
						for(int j=0; j<children.size(); j++) {
							System.out.println(children.get(j));
						}
					}
				
					if (children.size() > 0) {
						patternMining(discriminativePower, best_comm_new, children, alreadyVisisted);
					}else {
						if(ExperimentInfo.verbose>1) System.out.println("LIV "+level+"Il pattern "+child+" non ha figli");
					}
				}//Anche i fratelli non dovranno essere espansi quindi potrei svuotare la lista e tornare al padre
			} else {//Svuota la lista
				if(ExperimentInfo.verbose>1) System.out.println("LIV#"+level+" FIGLI ESPANSI " + i + " SU " + orderedChildren.size());
				// Ripristino alreadyVisited
//				for (int k = 0; k < i; k++) {
//					alreadyVisisted[orderedChildren.get(k).getLastAddedEdge()] = false;
//				}
				break;
			}
		} // for
		
		// Ripristino alreadyVisited
		for (int k = 0; k < i; k++) {
			alreadyVisisted[orderedChildren.get(k).getLastAddedEdge()] = false;
		}

		level--;
	}
	
//	private void stampa_alreadyVisited(boolean[] alreadyVisisted, int level){
//		System.out.print("VISTED EDGES (liv: "+level+") [");
//		
//		int conta=0;
//		for (int j = 0; j < alreadyVisisted.length; j++) {
//			if(alreadyVisisted[j]){conta++; System.out.print(j+" ");}
//		}
//		System.out.println("] archi visitati: "+conta);
//	}
	
	public LinkedList<Pattern> final_cleaning(Heap<Pattern> patterns) {
		// CLEANING:
		if (ExperimentInfo.verbose>1) {
			System.out.println("HEAP");
			System.out.println(patterns.toString());
		}
		
		LinkedList<Pattern> toRemove = new LinkedList<Pattern>();
		LinkedList<Pattern> result = new LinkedList<Pattern>();
		
		int pos = 0;
		
		while(!patterns.isEmpty()){
			Pattern min = patterns.getMin();
			patterns.remove(0);
			result.add(min);
			for (int i = pos - 1; i >= 0; i--) {
				if (pos != i && min.contains(result.get(i))) {
					toRemove.add(result.get(i));
				}
			} // for
			int removed = toRemove.size();
			pos = pos - removed + 1;

			if (removed > 0) {
				result.removeAll(toRemove);
				toRemove.clear();
			}
		}
		
		return result;
//		
//		Iterator<Pattern> it = patterns.iterator();
//		LinkedList<Pattern> toRemove = new LinkedList<Pattern>();
//		LinkedList<Pattern> result = new LinkedList<Pattern>();
//		
//		int pos = 0;
//		while (it.hasNext()) {
//			Pattern min = it.next();
//			it.remove();
//			result.add(min);
//			for (int i = pos - 1; i >= 0; i--) {
//				if (pos != i && min.contains(result.get(i))) {
//					toRemove.add(result.get(i));
//				}
//			} // for
//			int removed = toRemove.size();
//			pos = pos - removed + 1;
//
//			if (removed > 0) {
//				result.removeAll(toRemove);
//				toRemove.clear();
//			}
//		} // while
//		return result;
	}
	
	public void clean_heap(Heap<Pattern> patterns) {
		
		//Pattern[] heap = patterns.getArray();
		//Iterator<Pattern> it = patterns.iterator()
		Pattern curr;
		Pattern examinate;
		int heap_array_size = patterns.getLength();
		for(int i=0; i<patterns.getLength(); i++) {
			curr = patterns.getElement(i);
			if(curr!=null) {
				for(int j=0; j<patterns.getLength(); j++) {
					examinate = patterns.getElement(j);
		
					if(i!=j && examinate!=null && curr.contains(examinate)) { //curr è un superpattern di heap[j]
						if(curr.compareTo(examinate)<0) throw new IllegalStateException("E' presente un sottopattern migliore del pattern corrente!");
						//examinate=null;
						patterns.resetElement(j);
					}
				}
			}
		}//for_i
		
		//Sistema_heap
		patterns.reset();
		int count_ins=0;
	    for(int i = 0; i<heap_array_size;i++) {
	    	if(patterns.getElement(i)!=null) {
	    		count_ins++;
	    		patterns.setSize(count_ins);
	    		patterns.add(patterns.getElement(i));
	    	}
	    }
	    patterns.setSize(ExperimentInfo.k);
	}

	private ArrayList<Pattern> orderChildren(Pattern father, List<Integer> neighs, float sogliaProb, float best_ds,
			float best_comm) {
		int howMany = Dataset.getNumArchi();
		int patternSize = 1;
		float upper_bound;
		float beta;
		float comm_other;
		if (neighs != null && father != null) {
			howMany = neighs.size();
			patternSize = father.size() + 1;
		} else if ((neighs != null && father == null) || (neighs == null && father != null))
			throw new IllegalArgumentException("orderChildren error");

		ArrayList<Pattern> children = new ArrayList<Pattern>(howMany);
		for (int i = 0; i < howMany; i++) {
			Pattern child;
			if (neighs != null) {
				child = new Pattern(father, patternSize);
				child.addEdge(neighs.get(i));
			} else {
				child = new Pattern(patternSize);
				child.addEdge(i);
			}
			float comm = main.computeCommonness(father, child, sogliaProb, false);
			//upper_bound = Measures_Calculator.upperBound(main.datasetSize(),other.datasetSize(), comm);
			float support = child.getSupport();
			upper_bound = Measures_Calculator.upperBound(main.datasetSize(),other.datasetSize(), support);

				
			if (comm > 0) {
				if(child.size()>previousRun_dimMax && !ExperimentInfo.filling_mode) Statistics_collector.nodes++;
				// if(levelSize.size()<=level)
				// levelSize.add(level,1);
				// else levelSize.set(level, levelSize.get(level)+1);
				comm_other = other.computeCommonness(child,ExperimentInfo.tau_r, true);
				child.setCommonness(comm);
				child.setCommonness_other(comm_other);
				beta = Measures_Calculator.discriminativePower(main.datasetSize(),other.datasetSize(),child);
				child.setDiscriminativePower(beta); // H(N)-H(N|P)
				child.setUpperBound(upper_bound);	
				if(!ExperimentInfo.filling_mode) Statistics_collector.update_max_upper_bound(level,upper_bound);
				
				if (canAdd(child, best_ds, best_comm)) {
//					// LUNGO LA LINEA DI ESTENSIONE DI CHILD best_ds e best_comm
//					// devono essere aggiornati
//					// Potrei segnare nel pattern con un boolean che i suoi
//					// valori di ds e comm sono i migliori lungo la sua linea di
//					// espansione
					child.setIsBest(true);					
					if(child.size()>previousRun_dimMax && canAdd_hardCheck(child)) {
						Main.patterns.add(child);
					}
				}
				// Se l'upperBound è sotto soglia posso evitare di aggiungerlo
				if (prosegui(upper_bound, getSoglia())) {
					children.add(child);
					//if(root==241) System.out.println(child);
				} else {
					if(child.size()>previousRun_dimMax && !ExperimentInfo.filling_mode) Statistics_collector.cuttedPatterns++;
					// if(cuttedPatternPerLevel.size()<=level)
					// cuttedPatternPerLevel.add(level,1);
					// else cuttedPatternPerLevel.set(level,
					// cuttedPatternPerLevel.get(level)+1);
				}
			}
			
		} // for

		// if(cuttedPatternPerLevel.size()<=level)
		// cuttedPatternPerLevel.add(level,0);

		Collections.sort(children, new Comparator<Pattern>() {
			public int compare(Pattern x, Pattern y) {
				if (y.getCommonness() - x.getCommonness() > 0)
					return 1;
				if (y.getCommonness() - x.getCommonness() < 0)
					return -1;
				return 0;
			}
		});

		return children;
	}

	
	private float getSoglia() {
		return Math.max(sogliaDiscriminativePower, Main.patterns.getSoglia());
	}

	private boolean prosegui(float upper_bound, float soglia) {
		return upper_bound >= soglia;
	}

	/*
	 * Il metodo riceve un pattern p (per il quale si desidera stabilire se ha i
	 * requisiti per essere inserito nell'insieme dei pattern da restituire) e
	 * un valore beta che indica lo score dell'ultimo pattern aggiunto sulla
	 * stessa linea lungo la qulae è generato p; si tratta cioè del valore di
	 * entropy del miglior sottopattern già generato. Se p ha già una entropy
	 * inferiore a beta non serve fare ulteriori controlli, altrimenti bisogna
	 * verificare che l'insieme dei pattern da restituire non contenga già un
	 * superpattern di p. Se tale superpattern esiste e la sua entropy è
	 * inferiore a quella di p, allora è necessario eliminare il superpattern e
	 * inserire p.
	 */
	private boolean canAdd(Pattern p, float beta, float score) {
		
		if (p.size() == 1) {
			//containsSuperPattern(p);
			return true;
			//return false; //Poichè è già stato inserito in precedenza
		}

		if (p.getDiscriminativePower() > beta || p.getDiscriminativePower() == beta && p.getCommonness() >= score) {
			// Verifico se è già stato inserito un superpattern di p
			if(p.size()>previousRun_dimMax && !ExperimentInfo.filling_mode) Statistics_collector.addedPattern++;// Incremento il numero di pattern meritevoli di
							// essere aggiunti al result-set
			//containsSuperPattern(p);
			return true;
		}
		return false;
	}
	
	/*Controlla se p è già presente nell'heap o esiste un super-pattern migliore di lui*/
	public static boolean canAdd_hardCheck(Pattern p) {
		if(!hard_check) return true;
//		if(p.getDiscriminativePower()<Main.patterns.getSoglia()) return false;
//		Iterator<Pattern> it = Main.patterns.iterator();
//		if(p.getDiscriminativePower()<Main.list.get(0).getDiscriminativePower()) return false;
		Iterator<Pattern> it = Main.list.iterator();
		Pattern curr;
		while(it.hasNext()) {
			curr = it.next();
			if(curr.size()==p.size() && curr.equals(p)) return false; //p è già nell'heap
			if(curr.size()>p.size() && curr.contains(p) /*&& curr.compareTo(p)>0*/) {
				return false;
			}
			if(curr.size()<p.size() && p.contains(curr) /*&& curr.compareTo(p)>0*/) {
				return false;
			}
		}
//		System.out.println("=========");
//		System.out.println("HARD CHECK SAYS YES FOR "+p);
//		System.out.println("=========");
		return true;
	}
	
	private List<Integer> getNeighborhood(List<Pattern> fatherNeigh, boolean[] alreadyVisisted, int newEdge,
			int newNode) {
		// NB se father==null sto calcolando i vicini dell'arco singolo, in
		// tutti gli altri casi ho il valore di newNode a meno che
		// l'estensioe del passo predente non abbia comportato l'aggiunta di un
		// arco che si inseriva tra i nodi esistenti, senza aggingerne di nuovi
		// In questo caso non serve chiamare computeEdgeNeighbos poichè i vicini
		// sono quelli di mio padre
		List<Integer> newEdgeNeigh = new ArrayList<>();
		if (!(newNode < 0 && fatherNeigh != null))
			newEdgeNeigh = main.computeEdgeNeighbos(newEdge, newNode, alreadyVisisted);

		if (fatherNeigh != null) {
			for (Pattern p : fatherNeigh) {
				int edge = p.getLastAddedEdge();
				if (edge != newEdge && !contains(edge, newNode) && !alreadyVisisted[edge]) {
					newEdgeNeigh.add(edge);
				}
			}
		}
		return newEdgeNeigh;
	}

	private boolean contains(int edge, int node) {
		int v1 = Dataset.edgeMapping[0][edge];
		int v2 = Dataset.edgeMapping[1][edge];
		return v1 == node || v2 == node;
	}

	/*
	 * In questa sede si desidera individuare la presenza di superpattern meno
	 * informativi di p. Qualora si verifichi una situazione del genere il
	 * pattern in questione deve essere eliminato dal dataset perchè non
	 * rispetta la definizione (cioè esiste un sottopattern più informativo di
	 * lui).
	 */
	public boolean containsSuperPattern(Pattern p) {
		boolean flag = false;
		Iterator<Pattern> it = Main.patterns.iterator();
		while (it.hasNext()) {
			Pattern e = it.next();
//			if(p.equals(p2) && Statistics_collector.maxDepth==6){
//				System.out.println("CONTROL");
//				Main.patterns.print();
//			}
			if (e.size() > p.size() && e.contains(p)) {
				// Se esiste un superpattern meno informativo di p devo
				// eliminare il superpattern dall'insieme
				if (e.compareTo(p) < 0) {
//					if(e.equals(p6)){
//						System.out.println("REMOVING P6 BECAUSE OF");
//						System.out.println(p);
//					}
//
//					if(p.equals(p2)){
//						System.out.println("P2 REMOVES");
//						System.out.println(e);
//					}
					flag = true;
					if(p.size()>previousRun_dimMax && !ExperimentInfo.filling_mode) Statistics_collector.addedPattern--;
					it.remove();
				} // if
			} // if
		} // while
		return flag;
	}// containsSuperPattern


	// Il caso migliore è c vs 0
//	private float upperBound(Pattern p) {
////		float c = p.getCommonness();
////		if (c <= 0)
////			c = main.computeCommonness(p, ExperimentInfo.tau_r, false);
//		float c = p.getSupport();
//		return Measures_Calculator.upperBound(main.datasetSize(),other.datasetSize(),c);
//	}
}
