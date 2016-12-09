import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class Shingles {

	public static void main(String args[]) {
		String[] shingles = { "the class", "class will", "will cover", "cover web", "web crawling", "crawling is",
				"is expected", "expected in", "in each", "each search", "search engine", "engine presented",
				"presented in", "in the" };

		String s1 = "the class will cover web crawling";
		String s2 = "web crawling is expected in each search engine presented in the class";
		int[][] class_matrix = new int[shingles.length][2];

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int iterations = 25;
		map.clear();
		for (int i = 0; i < shingles.length; i++) {
			if (s1.contains(shingles[i])) {
				class_matrix[i][0] = 1;
				if (s2.contains(shingles[i]))
					class_matrix[i][1] = 1;
				else
					class_matrix[i][1] = 0;
			} else {
				class_matrix[i][0] = 0;
				if (s2.contains(shingles[i]))
					class_matrix[i][1] = 1;
				else
					class_matrix[i][1] = 0;
			}
			System.out.println(shingles[i] + ":[" + class_matrix[i][0] + "," + class_matrix[i][1] + "]");
			map.put(shingles[i], i);
		}

		// calculate the permutations.
		String[][] perms = new String[25][14];
		int i = 0;
		while (iterations > 0) {

			Iterator it = map.entrySet().iterator();
			List keys = new ArrayList(map.keySet());
			Collections.shuffle(keys);
			int j = 0;
			for (Object s : keys) {
				perms[i][j] = s.toString();
				System.out.print(s.toString() + ",");
				j++;
			}
			i++;
			System.out.println();
			iterations--;
		}

		// calculate the signatures
		Integer[][] signatures = new Integer[25][2];
		List shinglist = Arrays.asList(shingles);
		// for each permutation
		for (int k = 0; k < 25; k++) {

			String[] sarray = perms[k];
			boolean one = false;
			for (int m = 0; m < 2; m++) {
				int sigv = 0;
				for (int j = 0; j < sarray.length; j++) {
					int d = shinglist.indexOf(sarray[j]);
					if (class_matrix[d][m] == 1) {
						one = true;
						sigv = d;
						break;
					}
				}
				if (one == true) {
					signatures[k][m] = sigv;
					continue;
				}
			}
		}

		int intersection = 0;
		int union = 0;
		for (int k = 0; k < signatures.length; k++) {

			System.out
					.println(Arrays.toString(perms[k]) + " : " + (signatures[k][0] + 1) + "," + (signatures[k][1] + 1));
			if (signatures[k][0] == signatures[k][1]) {
				intersection += 1;
			}
			union++;

		}

		// Similarity between Sketch Vectors
		double sim = (double) intersection / (double) union;
		System.out.println("\nSimilarity between Sketch Vectors : " + sim);

		intersection = 0;
		union = 0;
		for (int k = 0; k < class_matrix.length; k++) {
			if (class_matrix[k][0] == class_matrix[k][1]) {
				intersection += 1;
			}
			union += 1;
		}

		// Similarity between Class Vectors
		sim = (double) intersection / (double) union;
		System.out.println("\nSimilarity between Class Vectors (Jaccard) : " + sim);

		double[][] rowHashes = new double[14][2];
		double inf = Double.POSITIVE_INFINITY;
		for (int k = 0; k < shingles.length; k++) {
			rowHashes[k][0] = inf;
			rowHashes[k][1] = inf;
		}

		double[][] hg1 = new double[shingles.length][2];
		double[][] hg2 = new double[shingles.length][2];

		for (int k = 0; k < shingles.length; k++) {
			if (k != 0) {
				// compare with values before
				double newh = hash_h(k + 1);
				double newg = hash_g(k + 1);
				System.out.println(" newh=" + newh + ", newg=" + newg);
				if (class_matrix[k][0] == 1) {
					if (newh < hg1[k - 1][0]) {
						hg1[k][0] = newh;
					} else {
						hg1[k][0] = hg1[k - 1][0];

					}
					if (newg < hg1[k - 1][1])
						hg1[k][1] = newg;
					else {
						hg1[k][1] = hg1[k - 1][1];

					}
				}

				if (class_matrix[k][1] == 1) {
					if (newh < hg2[k - 1][0]) {
						hg2[k][0] = newh;
					} else {
						hg2[k][0] = hg2[k - 1][0];
					}

					if (newg < hg2[k - 1][1]) {
						hg2[k][1] = newg;
					} else {
						hg2[k][1] = hg2[k - 1][1];
					}
				}

				System.out.println(shingles[k] + "--- h/g C1:" + hg1[k][0] + "/" + hg1[k][1] + " ,C2:" + hg2[k][0] + "/"
						+ hg2[k][1]);

			} else {

				// get new hash values
				double newh = hash_h(k + 1);
				double newg = hash_g(k + 1);

				System.out.println("newh=" + newh + ",newg=" + newg);
				if (class_matrix[k][0] == 1) {
					hg1[k][0] = newh;
					// System.out.println("hg1=" + hg1[k][0]);
					hg1[k][1] = newg;
				}
				if (class_matrix[k][1] == 1) {
					hg2[k][0] = newh;
					hg2[k][1] = newg;
				}
				System.out.println(shingles[k] + "--- h/g C1:" + hg1[k][0] + "/" + hg1[k][1] + " ,C2:" + hg2[k][0] + "/"
						+ hg2[k][1]);

			}
		}

	}

	public static double hash_h(int x) {
		double hash = (x + 1) % 5;
		return hash;
	}

	public static double hash_g(int x) {
		double hash = (x + 2) % 5;
		return hash;
	}

}
