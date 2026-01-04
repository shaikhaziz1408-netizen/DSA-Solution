import java.io.*;
import java.util.*;

class Result {
    static final int MOD = 1000000007;
    private static int stateCounter;
    private static String regex;
    private static int pos;

    static class NFAState {
        int id;
        Map<Character, List<Integer>> transitions = new HashMap<Character, List<Integer>>();
        NFAState(int id) { this.id = id; }

        void add(char c, int to) {
            if (!transitions.containsKey(c)) transitions.put(c, new ArrayList<Integer>());
            transitions.get(c).add(to);
        }
    }

    static List<NFAState> nfa;

    private static int newState() {
        nfa.add(new NFAState(stateCounter));
        return stateCounter++;
    }

    // Thompson's Construction using Recursive Descent
    private static int[] parse() {
        if (regex.charAt(pos) == '(') {
            pos++; // consume '('
            int[] left = parse();
            char op = regex.charAt(pos);

            if (op == '|') { // Union: (R1|R2)
                pos++;
                int[] right = parse();
                pos++; // consume ')'
                int start = newState(), end = newState();
                nfa.get(start).add('e', left[0]);
                nfa.get(start).add('e', right[0]);
                nfa.get(left[1]).add('e', end);
                nfa.get(right[1]).add('e', end);
                return new int[]{start, end};
            } else if (op == '*') { // Kleene Star: (R1*)
                pos++;
                pos++; // consume ')'
                int start = newState(), end = newState();
                nfa.get(start).add('e', left[0]);
                nfa.get(start).add('e', end);
                nfa.get(left[1]).add('e', left[0]);
                nfa.get(left[1]).add('e', end);
                return new int[]{start, end};
            } else { // Concatenation: (R1R2)
                int[] right = parse();
                pos++; // consume ')'
                nfa.get(left[1]).add('e', right[0]);
                return new int[]{left[0], right[1]};
            }
        } else { // Base case: 'a' or 'b'
            char c = regex.charAt(pos++);
            int start = newState(), end = newState();
            nfa.get(start).add(c, end);
            return new int[]{start, end};
        }
    }

    public static int countStrings(String r, int l) {
        stateCounter = 0;
        nfa = new ArrayList<NFAState>();
        regex = r;
        pos = 0;
        int[] bounds = parse();
        int nfaEnd = bounds[1];

        // DFA Conversion (Subset Construction)
        Map<BitSet, Integer> dfaMap = new HashMap<BitSet, Integer>();
        List<BitSet> dfaStates = new ArrayList<BitSet>();
        Queue<Integer> queue = new LinkedList<Integer>();

        BitSet startSet = new BitSet();
        startSet.set(bounds[0]);
        BitSet startClosure = getEpsilonClosure(startSet);
        
        dfaMap.put(startClosure, 0);
        dfaStates.add(startClosure);
        queue.add(0);

        List<Boolean> isAccepting = new ArrayList<Boolean>();
        List<int[]> transitions = new ArrayList<int[]>();

        while (!queue.isEmpty()) {
            int u = queue.poll();
            BitSet curr = dfaStates.get(u);
            isAccepting.add(curr.get(nfaEnd));

            int[] trans = new int[2];
            for (int i = 0; i < 2; i++) {
                char symbol = (i == 0) ? 'a' : 'b';
                BitSet move = new BitSet();
                for (int s = curr.nextSetBit(0); s >= 0; s = curr.nextSetBit(s + 1)) {
                    List<Integer> targets = nfa.get(s).transitions.get(symbol);
                    if (targets != null) {
                        for (int t : targets) move.set(t);
                    }
                }
                BitSet closure = getEpsilonClosure(move);
                if (closure.isEmpty()) {
                    trans[i] = -1;
                } else {
                    if (!dfaMap.containsKey(closure)) {
                        dfaMap.put(closure, dfaStates.size());
                        dfaStates.add(closure);
                        queue.add(dfaStates.size() - 1);
                    }
                    trans[i] = dfaMap.get(closure);
                }
            }
            transitions.add(trans);
        }

        // Adjacency Matrix
        int n = dfaStates.size();
        long[][] matrix = new long[n][n];
        for (int i = 0; i < n; i++) {
            int[] t = transitions.get(i);
            if (t[0] != -1) matrix[i][t[0]]++;
            if (t[1] != -1) matrix[i][t[1]]++;
        }

        // Matrix Exponentiation
        long[][] resMatrix = power(matrix, l);
        long totalCount = 0;
        for (int i = 0; i < n; i++) {
            if (isAccepting.get(i)) {
                totalCount = (totalCount + resMatrix[0][i]) % MOD;
            }
        }
        return (int) totalCount;
    }

    private static BitSet getEpsilonClosure(BitSet states) {
        BitSet closure = (BitSet) states.clone();
        Stack<Integer> stack = new Stack<Integer>();
        for (int s = states.nextSetBit(0); s >= 0; s = states.nextSetBit(s + 1)) stack.push(s);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            List<Integer> targets = nfa.get(u).transitions.get('e');
            if (targets != null) {
                for (int v : targets) {
                    if (!closure.get(v)) {
                        closure.set(v);
                        stack.push(v);
                    }
                }
            }
        }
        return closure;
    }

    private static long[][] power(long[][] A, int p) {
        int n = A.length;
        long[][] res = new long[n][n];
        for (int i = 0; i < n; i++) res[i][i] = 1;
        while (p > 0) {
            if (p % 2 == 1) res = multiply(res, A);
            A = multiply(A, A);
            p /= 2;
        }
        return res;
    }

    private static long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                if (A[i][k] == 0) continue;
                for (int j = 0; j < n; j++) {
                    C[i][j] = (C[i][j] + A[i][k] * B[k][j]) % MOD;
                }
            }
        }
        return C;
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int t = Integer.parseInt(br.readLine().trim());
        while (t-- > 0) {
            String[] parts = br.readLine().trim().split(" ");
            System.out.println(Result.countStrings(parts[0], Integer.parseInt(parts[1])));
        }
    }
}
