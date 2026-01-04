import java.io.*;
import java.util.*;

class Result {

    /*
     * Complete the 'buildString' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER a (Cost to add a character)
     * 2. INTEGER b (Cost to copy a substring)
     * 3. STRING s  (Target string)
     */
    public static int buildString(int a, int b, String s) {
        int n = s.length();
        
        // 1. Build Suffix Automaton for the string S
        SAM sam = new SAM(s);
        
        // 2. Compute DP
        // dp[i] = min cost to build prefix of length i
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        
        // We will compute LPF (Longest Previous Factor) on the fly or effectively 
        // find the max copy length for each position i.
        // We maintain the current state in the SAM matching s[i...]
        int curr = 0; // Start at root
        int len = 0;  // Current match length
        
        for (int i = 0; i < n; i++) {
            // -- DP Step 1: Append character --
            // Always valid to append one char from previous state
            if (dp[i] + a < dp[i+1]) {
                dp[i+1] = dp[i] + a;
            }
            
            // -- DP Step 2: Copy substring --
            // Find longest match starting at i that appeared in s[0...i-1]
            // We slide the window of the match in the SAM.
            // Move to next char s[i]
            int c = s.charAt(i) - 'a';
            
            // If the transition doesn't exist or the state found is invalid 
            // (meaning the substring doesn't appear before index i), shrink the window.
            while (curr != 0 && (sam.nodes[curr].next[c] == -1 || sam.nodes[sam.nodes[curr].next[c]].firstEndPos >= i)) {
                curr = sam.nodes[curr].link;
                len = sam.nodes[curr].len;
            }
            
            // Try to extend with character c
            if (sam.nodes[curr].next[c] != -1 && sam.nodes[sam.nodes[curr].next[c]].firstEndPos < i) {
                curr = sam.nodes[curr].next[c];
                len++;
            } else {
                // If we couldn't extend even after shrinking to root (or appropriate suffix),
                // it means this specific char hasn't appeared before validly in a sequence.
                // Reset to root.
                // Note: if curr is 0 and next[c] is invalid, we stay at 0 with len 0.
            }
            
            // 'len' is now the length of the longest substring ending at 'i'? 
            // NO, the sliding window above maintains the match ending at 'i' for standard search.
            // BUT here we need the match *starting* at 'i' (to look forward).
            // The standard O(N) approach processes i from 0 to N. The sliding window 
            // usually finds the longest match ending at the *current* processing index.
            
            // To solve correctly within O(N) for "longest match starting at i":
            // We need the LPF array pre-calculated or adapt the logic.
            // Actually, calculating LPF[i] (start at i) is tricky in one forward pass of DP.
            // Let's use the explicit O(N) LPF construction pass first, then DP.
        }
        
        // Re-run proper Logic:
        // 1. Calculate LPF array where LPF[i] is max length of substring s[i...i+L-1]
        //    that occurs in s[0...i-1].
        //    This requires running s[i...] through the automaton, but we can't do that N times (O(N^2)).
        //    Wait, constraints N=30000. O(N^2) matching is borderline but might pass.
        //    However, an optimized LPF calculation is safer. 
        //    Let's stick to the O(N^2) traversal for LPF calculation because it's simpler to implement correctly 
        //    and 30,000 * 30,000 operations is ~9*10^8, but average LPF is small.
        //    Given the "Hard" difficulty, O(N) or O(N log N) is preferred.
        //    Actually, we can just perform the DP with the inner loop check using the SAM.
        
        // Reset DP
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        
        for (int i = 0; i < n; i++) {
            // Option 1: Single char addition
            if (dp[i] + a < dp[i+1]) {
                dp[i+1] = dp[i] + a;
            }
            
            // Option 2: Copy
            // Find max length match starting at i using SAM
            // We traverse starting from root.
            if (b < a) { // Optimization: Only check copy if it could potentially be cheaper than 1 char add
                         // Actually, copy cost B could be cheaper than 1*A? 
                         // Yes. But usually copy is used for length > 1.
            }
            
            int u = 0; // root
            int matchLen = 0;
            // Greedily find longest match
            for (int j = i; j < n; j++) {
                int c = s.charAt(j) - 'a';
                if (sam.nodes[u].next[c] == -1) break;
                
                int nextU = sam.nodes[u].next[c];
                // Check if this match appeared strictly before i
                // The occurrence ends at sam.nodes[nextU].firstEndPos.
                // It represents s[... firstEndPos].
                // We need the match source to be in s[0...i-1].
                // So the match must end <= i-1.
                if (sam.nodes[nextU].firstEndPos < i) {
                    matchLen++;
                    u = nextU;
                    // Update DP
                    // If we copy length matchLen, we reach i + matchLen
                    int cost = dp[i] + b;
                    if (cost < dp[i + matchLen]) {
                        dp[i + matchLen] = cost;
                    }
                } else {
                    break;
                }
            }
        }
        
        return dp[n];
    }

    // Suffix Automaton Class
    static class SAM {
        static class Node {
            int len, link;
            int[] next = new int[26];
            int firstEndPos = -1; // End position of the first occurrence
            
            Node() { Arrays.fill(next, -1); }
        }
        
        Node[] nodes;
        int sz, last;
        
        SAM(String s) {
            nodes = new Node[s.length() * 2 + 1];
            for(int i=0; i<nodes.length; i++) nodes[i] = new Node();
            sz = 1; 
            last = 0; 
            nodes[0].link = -1;
            
            for (int i = 0; i < s.length(); i++) {
                extend(s.charAt(i) - 'a', i);
            }
        }
        
        void extend(int c, int idx) {
            int cur = sz++;
            nodes[cur].len = nodes[last].len + 1;
            nodes[cur].firstEndPos = idx; // Record first end position
            int p = last;
            while (p != -1 && nodes[p].next[c] == -1) {
                nodes[p].next[c] = cur;
                p = nodes[p].link;
            }
            if (p == -1) {
                nodes[cur].link = 0;
            } else {
                int q = nodes[p].next[c];
                if (nodes[p].len + 1 == nodes[q].len) {
                    nodes[cur].link = q;
                } else {
                    int clone = sz++;
                    nodes[clone].len = nodes[p].len + 1;
                    System.arraycopy(nodes[q].next, 0, nodes[clone].next, 0, 26);
                    nodes[clone].link = nodes[q].link;
                    nodes[clone].firstEndPos = nodes[q].firstEndPos; // Clone inherits first occurrence pos
                    
                    while (p != -1 && nodes[p].next[c] == q) {
                        nodes[p].next[c] = clone;
                        p = nodes[p].link;
                    }
                    nodes[q].link = nodes[cur].link = clone;
                }
            }
            last = cur;
        }
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
        String outputPath = System.getenv("OUTPUT_PATH");
        BufferedWriter bufferedWriter;
        if (outputPath != null) {
            bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        } else {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        }

        String line = bufferedReader.readLine();
        if (line != null) {
            int t = Integer.parseInt(line.trim());
            for (int tItr = 0; tItr < t; tItr++) {
                String[] input = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
                int n = Integer.parseInt(input[0]);
                int a = Integer.parseInt(input[1]);
                int b = Integer.parseInt(input[2]);
                String s = bufferedReader.readLine();

                int result = Result.buildString(a, b, s);

                bufferedWriter.write(String.valueOf(result));
                bufferedWriter.newLine();
            }
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
