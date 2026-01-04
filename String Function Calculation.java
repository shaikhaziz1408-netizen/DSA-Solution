import java.io.*;
import java.util.*;

class Result {

    /*
     * Complete the 'maxValue' function below.
     * * NOTE: Return type changed to 'long' because the maximum value 
     * can exceed the range of a 32-bit integer for |t| = 10^5.
     */
    public static long maxValue(String t) {
        int n = t.length();
        // SAM arrays. Max states is 2*n
        int maxStates = 2 * n + 1;
        int[] len = new int[maxStates];
        int[] link = new int[maxStates];
        int[][] next = new int[maxStates][26];
        long[] cnt = new long[maxStates];
        
        // Initialize root (state 0)
        len[0] = 0;
        link[0] = -1;
        // -1 indicates null transition
        for (int i = 0; i < maxStates; i++) {
            Arrays.fill(next[i], -1);
        }
        
        int sz = 1;   // Size of automaton (number of states)
        int last = 0; // Index of state representing the entire string processed so far
        
        // 1. Build Suffix Automaton
        for (int i = 0; i < n; i++) {
            int c = t.charAt(i) - 'a';
            int cur = sz++;
            len[cur] = len[last] + 1;
            cnt[cur] = 1; // This state represents a prefix, so it occurs at least once
            
            int p = last;
            while (p != -1 && next[p][c] == -1) {
                next[p][c] = cur;
                p = link[p];
            }
            
            if (p == -1) {
                link[cur] = 0;
            } else {
                int q = next[p][c];
                if (len[p] + 1 == len[q]) {
                    link[cur] = q;
                } else {
                    int clone = sz++;
                    len[clone] = len[p] + 1;
                    System.arraycopy(next[q], 0, next[clone], 0, 26);
                    link[clone] = link[q];
                    // Cloned states have cnt = 0 initially; they get counts propagated later
                    cnt[clone] = 0; 
                    
                    while (p != -1 && next[p][c] == q) {
                        next[p][c] = clone;
                        p = link[p];
                    }
                    link[q] = clone;
                    link[cur] = clone;
                }
            }
            last = cur;
        }
        
        // 2. Propagate counts up the suffix link tree
        // We sort states by length descending so we process longer substrings first
        // Using bucket sort logic (or simply creating an index array and sorting it)
        Integer[] nodes = new Integer[sz];
        for (int i = 0; i < sz; i++) nodes[i] = i;
        
        final int[] finalLen = len;
        Arrays.sort(nodes, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                // Sort descending by length
                return Integer.compare(finalLen[b], finalLen[a]);
            }
        });
        
        // Propagate counts: if state u has a suffix link to v, then occurrences of u are also occurrences of v
        for (int i : nodes) {
            if (link[i] != -1) {
                cnt[link[i]] += cnt[i];
            }
        }
        
        // 3. Calculate max value
        long maxVal = 0;
        for (int i = 1; i < sz; i++) {
            // f(s) = length * count
            long currentVal = (long) len[i] * cnt[i];
            if (currentVal > maxVal) {
                maxVal = currentVal;
            }
        }
        
        return maxVal;
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        // Use standard output if OUTPUT_PATH is not set
        String outputPath = System.getenv("OUTPUT_PATH");
        BufferedWriter bufferedWriter;
        if (outputPath != null) {
            bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        } else {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        }

        String t = bufferedReader.readLine();

        // Use long to capture the result correctly
        long result = Result.maxValue(t);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
