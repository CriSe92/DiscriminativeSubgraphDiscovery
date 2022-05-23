package minig;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
//import java.util.Random;

import application.ExperimentInfo;
import application.Main;
import model.Dataset;
import model.Pattern;
import util.Heap;


public class DiscoverPatterns {

	private Dataset main, other;
	private float sogliaDiscriminativePower = 0;//0.6254462f;
	private int dimMax = 1;
	private int previousRun_dimMax = 0;
	private boolean hard_check = false;
	
	public static Pattern edge1 = new Pattern(1);
	public static Pattern edge2 = new Pattern(1);
	public static Pattern edge3 = new Pattern(1);
//	//ANALISI PATTERN Num. archi: 3 [105112858, 126852856, 126857127, ] Score: 28.978466 Score (other population): 0.0 Discriminative Power: 0.7680275 Upper Bound: 0.7680275
//
	static {
		edge1.addEdge(318);
		edge2.addEdge(1146);
	}

	
	//ANALISI PATTERN Num. archi: 3 [212, 782, 792, ] Score: 3.0 Score (other population): 0.0 Discriminative Power: 0.3958156 Upper Bound: 0.3958156
	//SUPER Num. archi: 1 [212, ] Score: 4.0 Score (other population): 0.0 Discriminative Power: 0.60998654 Upper Bound: 0.60998654

	// ['V','C', (212) , 'AO','C', (782) , 'AO','M', (792) ]
	
	private int level;

	
	public DiscoverPatterns(Dataset main, Dataset other) {
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
		this.hard_check = hard_check;
	}
	
	public void reset_pattern_dimension() {
		previousRun_dimMax = 0;
		dimMax = 1;
	}
	
	public void discoverPatterns() {
		level=0;
		System.out.println("==== HEAP ===");
		Main.patterns.print();
		System.out.println("=============");
		patternMining(0, 0, Preprocessing.orderedEdges,new boolean[Dataset.getNumArchi()]);
	}// discoverInterestingPatterns
	
	
	private void patternMining(float best_ds, float best_comm, List<Pattern> orderedChildren, boolean[] alreadyVisisted) {
		level++;
		Statistics_collector.maxDepth = (level > Statistics_collector.maxDepth) ? level : Statistics_collector.maxDepth;
		
		int i = 0;
		for (i = 0; i < orderedChildren.size(); i++) {
			Pattern child = orderedChildren.get(i);
			if (child.size() < dimMax && prosegui(child.getUpperBound(), getSoglia())) {
				alreadyVisisted[child.getLastAddedEdge()] = true;
				float discriminativePower = best_ds;
				float best_comm_new = best_comm;
				if (child.size() == 1 && ExperimentInfo.verbose>=1){
					System.out.println("ARCO#" + i + " SOGLIA CORRENTE " + getSoglia());
					System.out.println(child);
				}
				if((child.equals(edge1) || child.contains(edge1)) || (child.equals(edge2) || child.contains(edge2)) ||(child.equals(edge3) || child.contains(edge3))) {
					System.out.println("==== HEAP ===");
					Main.patterns.print();
					System.out.println("=============");
				}
				if (child.getIsBest()) {
					discriminativePower = child.getDiscriminativePower();
					best_comm_new = child.getCommonness();
				}
				//if (child.size() < dimMax) {// Espandere il pattern
					// Calcolo l'insieme dei vicini con cui estendere child
				List<Integer> neigh = getNeighborhood((child.size() == 1) ? null : orderedChildren, alreadyVisisted,child.getLastAddedEdge(), child.getLastAddedNode());
				ArrayList<Pattern> children = orderChildren(child, neigh, ExperimentInfo.tau_r, discriminativePower,best_comm_new);
				if(ExperimentInfo.verbose>1) {
					System.out.println("LIV#" + level + "SOGLIA REALE " + Main.patterns.getMin().getDiscriminativePower()+" SOGLIA USATA: "+getSoglia());
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
				//}//Anche i fratelli non dovranno essere espansi quindi potrei svuotare la lista e tornare al padre
			} else {//Svuota la lista
				if(ExperimentInfo.verbose>1) System.out.println("LIV#"+level+" FIGLI ESPANSI " + i + " SU " + orderedChildren.size());
				//if(child.size()>) Statistics_collector.cuttedPatterns = Statistics_collector.cuttedPatterns + orderedChildren.size() - i;
				//if(child.size()==1) {
					//System.out.println("WARNING: Sto svuotando la lista di livello 1");
					//System.out.println("SOGLIA "+getSoglia());
				//}
				// cuttedPatternPerLevel.set(level-1,
				// cuttedPatternPerLevel.get(level-1)+orderedChildren.size()-i);
				// Ripristino alreadyVisited
				for (int k = 0; k < i; k++) {
					alreadyVisisted[orderedChildren.get(k).getLastAddedEdge()] = false;
				}
				//orderedChildren.clear();
				break;
			}
		} // for

//		if (orderedChildren.size() > 0) {
//			for (int k = 0; k < i; k++) {
//				alreadyVisisted[orderedChildren.get(k).getLastAddedEdge()] = false;
//			}
//		}

		level--;
	}
	
	public LinkedList<Pattern> final_cleaning(Heap<Pattern> patterns) {
		// CLEANING:
		if (ExperimentInfo.verbose>1) {
			System.out.println("HEAP");
			System.out.println(patterns.toString());
		}
		
		Iterator<Pattern> it = patterns.iterator();
		LinkedList<Pattern> toRemove = new LinkedList<Pattern>();
		LinkedList<Pattern> result = new LinkedList<Pattern>();
		
		int pos = 0;
		while (it.hasNext()) {
			Pattern min = it.next();
			it.remove();
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
		} // while
		return result;
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
			upper_bound = Measures_Calculator.upperBound(main.datasetSize(),other.datasetSize(), comm);
			
//			if (prosegui(upper_bound, getSoglia())) {//L'arco può proseguire, lo aggiungo alla lista che andrà successivamente a formare index
//				//index.set(edges_ids[i]);
//				Statistics_collector.update_max_upper_bound(level,upper_bound);
//				Statistics_collector.nodes++;
//				
//				child.setCommonness(comm);
//				child.setCommonness_other(other.computeCommonness(child,ExperimentInfo.tau_r, true));
//				beta = Measures_Calculator.discriminativePower(main.datasetSize(),other.datasetSize(),child);
//				child.setDiscriminativePower(beta);
//				child.setUpperBound(upper_bound);
//				child.setIsBest(true);
//				if (canAdd(child, best_ds, best_comm)) {
//					// LUNGO LA LINEA DI ESTENSIONE DI CHILD best_ds e best_comm
//					// devono essere aggiornati
//					// Potrei segnare nel pattern con un boolean che i suoi
//					// valori di ds e comm sono i migliori lungo la sua linea di
//					// espansione
//					child.setIsBest(true);
////					System.out.println("PROSEGUE? "+(prosegui(child, getSoglia())?"YES":"NO"));
////					System.out.println(child.getUpperBound()+">="+getSoglia());
////					System.out.println("AGGIUNTO ALL HEAP? "+((child.size()>previousRun_dimMax)?"YES":"NO"));
////					System.out.println(child.size()+">"+previousRun_dimMax);
////					System.out.println(child);
//					if(child.size()>previousRun_dimMax) {
//						Main.patterns.add(child);
//					}
//				}
//				children.add(child);
//			} else {
//				if(child.size()>previousRun_dimMax) Statistics_collector.cuttedPatterns++;
//			}
				
			if (comm > 0) {
				if(child.size()>previousRun_dimMax) Statistics_collector.nodes++;
				// if(levelSize.size()<=level)
				// levelSize.add(level,1);
				// else levelSize.set(level, levelSize.get(level)+1);
				comm_other = other.computeCommonness(child,ExperimentInfo.tau_r, true);
				child.setCommonness(comm);
				child.setCommonness_other(comm_other);
				beta = Measures_Calculator.discriminativePower(main.datasetSize(),other.datasetSize(),child);
				child.setDiscriminativePower(beta); // H(N)-H(N|P)
				child.setUpperBound(upper_bound);	
				Statistics_collector.update_max_upper_bound(level,upper_bound);
				
				if (canAdd(child, best_ds, best_comm)) {
					// LUNGO LA LINEA DI ESTENSIONE DI CHILD best_ds e best_comm
					// devono essere aggiornati
					// Potrei segnare nel pattern con un boolean che i suoi
					// valori di ds e comm sono i migliori lungo la sua linea di
					// espansione
					child.setIsBest(true);
					
					//System.out.println("PROSEGUE? "+(prosegui(upper_bound, getSoglia())?"YES":"NO"));
					//System.out.println(child.getUpperBound()+">="+getSoglia());
					//System.out.println("AGGIUNTO ALL HEAP? "+((child.size()>previousRun_dimMax)?"YES":"NO"));
					//System.out.println(child.size()+">"+previousRun_dimMax);
					//System.out.println(child);
					if(child.size()>previousRun_dimMax && canAdd_hardCheck(child)) {
						Main.patterns.add(child);
					}
				}
				// Se l'upperBound è sotto soglia posso evitare di aggiungerlo
				if (prosegui(upper_bound, getSoglia())) {
					children.add(child);
					//if(root==241) System.out.println(child);
				} else {
					if(child.size()>previousRun_dimMax) Statistics_collector.cuttedPatterns++;
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

		if (p.size() == 1)
			return true;

		if (p.getDiscriminativePower() > beta || p.getDiscriminativePower() == beta && p.getCommonness() >= score) {
			// Verifico se è già stato inserito un superpattern di p
			if(p.size()>previousRun_dimMax) Statistics_collector.addedPattern++;// Incremento il numero di pattern meritevoli di
							// essere aggiunti al result-set
			containsSuperPattern(p);
			return true;
		}
		return false;
	}
	
	/*Controlla se p è già presente nell'heap o esiste un super-pattern migliore di lui*/
	public boolean canAdd_hardCheck(Pattern p) {
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
//				System.out.println("=========");
//				System.out.println("CURR "+curr);
//				System.out.println("TO INSERT "+p);
//				System.out.println("=========");
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
			if (e.size() > p.size() && e.contains(p)) {
				// Se esiste un superpattern meno informativo di p devo
				// eliminare il superpattern dall'insieme
				if (e.compareTo(p) < 0) {
					flag = true;
					if(p.size()>previousRun_dimMax) Statistics_collector.addedPattern--;
					it.remove();
				} // if
			} // if
		} // while
		return flag;
	}// containsSuperPattern


	// Il caso migliore è c vs 0
//	private float upperBound(Pattern p) {
//		float c = p.getCommonness();
//		if (c <= 0)
//			c = main.computeCommonness(p, ExperimentInfo.tau_r, false);
//		return Measures_Calculator.upperBound(main.datasetSize(),other.datasetSize(),c);
//	}


//	public List<Pattern> getPatternSet() {
//		return result;
//	}


	// UNUSED
	
//	// Riempio l'heap con i pattern monodimensionali
//		private ArrayList<Pattern> orderEdges(float sogliaProb) {
//			ArrayList<Pattern> orderedEdges = new ArrayList<Pattern>(Dataset.getNumArchi());
//			for (int i = 0; i < Dataset.getNumArchi(); i++) {
//				// nodes++;
//				Pattern p = new Pattern(1);
//				p.addEdge(i);
//				float comm = main.computeCommonness(p, sogliaProb, false);
//				if (comm > 0) {
//					float comm_other = other.computeCommonness(p, ExperimentInfo.tau_r, true);
//					p.setCommonness(comm);
//					p.setCommonness_other(comm_other);
//					float beta = Measures_Calculator.discriminativePower(main.datasetSize(),other.datasetSize(),p);
//					p.setDiscriminativePower(beta); // H(N)-H(N|P)
//					float upperBound = upperBound(p);
//					p.setUpperBound(upperBound);
//					Main.patterns.add(p);
//					if (prosegui(p, getSoglia())) {
//						orderedEdges.add(p);
//					}
//				}
//			}
//
//			Collections.sort(orderedEdges, new Comparator<Pattern>() {
//				public int compare(Pattern x, Pattern y) {
//					if (y.getCommonness() - x.getCommonness() > 0)
//						return 1;
//					if (y.getCommonness() - x.getCommonness() < 0)
//						return -1;
//					return 0;
//				}
//			});
//
//			// System.out.println(patterns);
//			System.out.println("MINIMO " + Main.patterns.getMin().getDiscriminativePower());
//			return orderedEdges;
//		}

	public static void main(String[] args) {
	
		int heap_size = 6;
		Dataset.edgeMapping(heap_size);
		
		Heap<Pattern> patterns = new Heap<Pattern>(heap_size);
		//Random r = new Random(351);
		//float super_ds;
		int i=0;
		while(i<3) {
			Pattern p = new Pattern(1);
			p.addEdge(i);
			p.setDiscriminativePower(0.3f);
			patterns.add(p);
			i++;
		}
		Pattern p1 = new Pattern(2);
		p1.addEdge(1);
		p1.addEdge(0);
		p1.setDiscriminativePower(0.7f);
		patterns.add(p1);
		
		Pattern p2 = new Pattern(2);
		p2.addEdge(2);
		p2.addEdge(0);
		p2.setDiscriminativePower(0.7f);
		patterns.add(p2);
		
		Pattern p3 = new Pattern(2);
		p3.addEdge(2);
		p3.addEdge(1);
		p3.setDiscriminativePower(0.7f);
		patterns.add(p3);
		
		
		System.out.println("PRINT BEFORE");
		System.out.println(patterns.toString());
		(new DiscoverPatterns(null, null)).clean_heap(patterns);
		System.out.println("PRINT AFTER");
		System.out.println(patterns.toString());
		
		System.out.println("SOGLIA: "+patterns.getMin().getDiscriminativePower());
		System.out.println("SIZE: "+patterns.getSize());
		System.out.println("NUM ELEM: "+patterns.getLength());
		
	}

}
