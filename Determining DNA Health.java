import java.io.*;
import java.util.*;

public class Solution {
    // Large constants based on problem constraints
    static final int MAX_NODES = 2000005; 
    static int[][] trie = new int[MAX_NODES][26];
    static int[] fail = new int[MAX_NODES];
    static int[] dictLink = new int[MAX_NODES];
    static List<Integer>[] geneIndices = new ArrayList[MAX_NODES];
    static List<Long>[] healthPrefixSums = new ArrayList[MAX_NODES];
    static int nodesCount = 1;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        int n = Integer.parseInt(br.readLine().trim()); 
        String[] genes = br.readLine().split("\\s+"); 
        String[] healthValues = br.readLine().split("\\s+"); 

        // 1. Build Trie efficiently
        for (int i = 0; i < n; i++) {
            int curr = 0;
            for (int j = 0; j < genes[i].length(); j++) {
                int c = genes[i].charAt(j) - 'a';
                if (trie[curr][c] == 0) trie[curr][c] = nodesCount++;
                curr = trie[curr][c];
            }
            if (geneIndices[curr] == null) {
                geneIndices[curr] = new ArrayList<Integer>();
                healthPrefixSums[curr] = new ArrayList<Long>();
            }
            geneIndices[curr].add(i);
            long prev = healthPrefixSums[curr].isEmpty() ? 0 : healthPrefixSums[curr].get(healthPrefixSums[curr].size() - 1);
            healthPrefixSums[curr].add(prev + Long.parseLong(healthValues[i])); 
        }

        buildAutomaton();

        int s = Integer.parseInt(br.readLine().trim()); 
        long minH = Long.MAX_VALUE, maxH = 0;

        // 2. Process DNA Strands
        for (int i = 0; i < s; i++) {
            String[] query = br.readLine().split("\\s+"); 
            int first = Integer.parseInt(query[0]);
            int last = Integer.parseInt(query[1]);
            String d = query[2];
            long currentHealth = 0;
            int curr = 0;

            for (int j = 0; j < d.length(); j++) {
                curr = trie[curr][d.charAt(j) - 'a'];
                int temp = curr;
                // Follow dictionary links only to nodes that have health values
                while (temp > 0) {
                    if (geneIndices[temp] != null) {
                        currentHealth += calculateRangeHealth(temp, first, last);
                    }
                    temp = dictLink[temp];
                }
            }
            if (currentHealth < minH) minH = currentHealth;
            if (currentHealth > maxH) maxH = currentHealth;
        }

        System.out.println(minH + " " + maxH); 
    }

    static void buildAutomaton() {
        Queue<Integer> q = new LinkedList<Integer>();
        for (int i = 0; i < 26; i++) {
            if (trie[0][i] > 0) q.add(trie[0][i]);
        }

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int i = 0; i < 26; i++) {
                if (trie[u][i] > 0) {
                    fail[trie[u][i]] = trie[fail[u]][i];
                    // Dictionary link points to the nearest suffix that is a complete gene
                    int f = fail[trie[u][i]];
                    dictLink[trie[u][i]] = (geneIndices[f] != null) ? f : dictLink[f];
                    q.add(trie[u][i]);
                } else {
                    trie[u][i] = trie[fail[u]][i];
                }
            }
        }
    }

    static long calculateRangeHealth(int node, int first, int last) {
        List<Integer> idxList = geneIndices[node];
        // Binary search for range [first, last]
        int start = Collections.binarySearch(idxList, first);
        if (start < 0) start = -(start + 1);
        int end = Collections.binarySearch(idxList, last);
        if (end < 0) end = -(end + 1) - 1;

        if (start > end) return 0;
        long total = healthPrefixSums[node].get(end);
        if (start > 0) total -= healthPrefixSums[node].get(start - 1);
        return total; 
    }
}
