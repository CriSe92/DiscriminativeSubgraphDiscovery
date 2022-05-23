package minig;

import model.Pattern;

public class Measures_Calculator {
	
	public static float populationEntropy(int main_population_size, int other_population_size) {
		float n = (float)(main_population_size + other_population_size);
		float h = (float) (-(main_population_size / (n)) * Math.log(main_population_size / n) / Math.log(2) - (other_population_size / n) * Math.log(other_population_size / n) / Math.log(2));
		return h;
	}


	// Per un pattern l'upper bound potrebbe essere calcolato tenendo conto
	// della commonnes dell'arco che segue nell'elenco l'arco utilizzato per
	// andare dal padre al figlio
	public static float upperBound(int main_population_size, int other_population_size, float commonness) {
		float n = main_population_size + other_population_size;
		float q2 = (main_population_size  - commonness) / (main_population_size  - commonness + other_population_size);
		float q = commonness / n;
		// float hp = 0;
		float hnp = (q2 == 0 || q2 == 1) ? 0
				: (float) (-q2 * Math.log(q2) / Math.log(2) - (1 - q2) * Math.log(1 - q2) / Math.log(2));
		float h = (1 - q) * hnp;
		return populationEntropy( main_population_size, other_population_size) - h;
	}

	/*
	 * h <-- "punteggio" del pattern p nella popolazione dei sani u <--
	 * "punteggio" del pattern p nella popolazione dei malati q = h/(h+u)
	 * H(C|E1...En) =(?) - qlog(q) - (1-q)log(1-q)
	 *
	 */
	/*
	 * h <-- "punteggio" del pattern p nella popolazione dei sani u <--
	 * "punteggio" del pattern p nella popolazione dei malati q = h/(h+u)
	 * H(C|E1...En) =(?) - qlog(q) - (1-q)log(1-q)
	 *
	 */
	public static float discriminativePower(int main_population_size, int other_population_size, Pattern p) {
		return discriminativePower(main_population_size,other_population_size, p.getCommonness(),p.getCommonness_other());
	}
	
	public static float discriminativePower(int main_population_size, int other_population_size, float comm_main, float comm_other) {
//		float comm_main = p.getCommonness();
//		float comm_other = p.getCommonness_other();

		if ((comm_main == 0 && comm_other == 0) || comm_main < comm_other)
			return 0;

		float q = (comm_main + comm_other) / (main_population_size + other_population_size);
		float q1 = comm_main / (comm_main + comm_other);
		float q2 = (main_population_size - comm_main + other_population_size - comm_other)!=0?(main_population_size - comm_main) / (main_population_size - comm_main + other_population_size - comm_other):0;

		float hsp = (q1 == 0 || q1 == 1) ? 0
				: (float) (-q1 * Math.log(q1) / Math.log(2) - (1 - q1) * Math.log(1 - q1) / Math.log(2));
		float hsnp = (q2 == 0 || q2 == 1) ? 0
				: (float) (-q2 * Math.log(q2) / Math.log(2) - (1 - q2) * Math.log(1 - q2) / Math.log(2));

		float entropy = hsp * q + hsnp * (1 - q);

		return populationEntropy( main_population_size, other_population_size) - entropy;
	}
	
}
