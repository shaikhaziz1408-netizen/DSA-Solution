import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'solve' function below.
     *
     * The function is expected to return a LONG_INTEGER.
     * The function accepts INTEGER_ARRAY x as parameter.
     */
    static final long MOD = 1_000_000_007L;

    public static long solve(List<Integer> x) {
        int n = x.size();
        
        // Precompute factorials
        long[] fact = new long[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) {
            fact[i] = (fact[i - 1] * i) % MOD;
        }

        // Identify missing numbers
        boolean[] present = new boolean[n + 1];
        int k = 0; // Count of zeros
        for (int val : x) {
            if (val != 0) present[val] = true;
            else k++;
        }

        // Two BITs: one for fixed numbers present in the array, one for missing numbers
        BIT bitFixed = new BIT(n);
        BIT bitMissing = new BIT(n);

        // Fill bitMissing with 1s at indices corresponding to missing numbers
        for (int i = 1; i <= n; i++) {
            if (!present[i]) {
                bitMissing.update(i, 1);
            }
        }

        // Calculate initial G: sum over all fixed numbers r of (count of missing numbers > r)
        // Also populate bitFixed with all fixed numbers initially
        long G = 0;
        for (int val : x) {
            if (val != 0) {
                bitFixed.update(val, 1);
                // Missing numbers > val is (Total Missing) - (Missing <= val)
                long missingGreater = k - bitMissing.query(val);
                G = (G + missingGreater) % MOD;
            }
        }

        long totalSum = 0;
        int z = 0; // Count of zeros encountered so far

        for (int i = 0; i < n; i++) {
            int val = x.get(i);
            long permCount = fact[n - 1 - i]; // (n-1-i)!

            if (val != 0) {
                // Case 1: Fixed Number
                
                // Remove current value from fixed set (it's no longer "to the right")
                bitFixed.update(val, -1);
                
                // Update G: remove contribution of this fixed number
                // We subtract the count of missing numbers greater than current val
                long missingGreater = k - bitMissing.query(val);
                G = (G - missingGreater + MOD) % MOD;

                // 1. Contribution from fixed numbers to the right that are smaller
                long L = bitFixed.query(val - 1);
                long term1 = (fact[k] * L) % MOD;
                term1 = (term1 * permCount) % MOD;
                totalSum = (totalSum + term1) % MOD;

                // 2. Contribution from missing numbers (which are available) that are smaller
                if (k > 0) {
                    long M = bitMissing.query(val - 1);
                    // Expected count involves (k-1)! * (k-z) permutations?
                    // Actually logic derived: (k-1)! * (k-z) * M * permCount
                    long term2 = (fact[k - 1] * (k - z)) % MOD;
                    term2 = (term2 * M) % MOD;
                    term2 = (term2 * permCount) % MOD;
                    totalSum = (totalSum + term2) % MOD;
                }

            } else {
                // Case 2: Zero (Variable)
                if (k > 0) {
                    // 1. Contribution from fixed numbers to the right (relative to the missing number we pick)
                    // Sum over all choices is (k-1)! * G
                    long term1 = (fact[k - 1] * G) % MOD;
                    term1 = (term1 * permCount) % MOD;
                    totalSum = (totalSum + term1) % MOD;

                    // 2. Contribution from other missing numbers
                    // Combinatorial sum: (k! / 2) * (k - z - 1)
                    if (k - z - 1 > 0) {
                        // Modular inverse of 2 is 500000004 for MOD 10^9+7
                        long kFactDiv2 = (fact[k] * 500000004L) % MOD; 
                        long term2 = (kFactDiv2 * (k - z - 1)) % MOD;
                        term2 = (term2 * permCount) % MOD;
                        totalSum = (totalSum + term2) % MOD;
                    }
                }
                z++;
            }
        }

        // Add K! because rank is 1-based (sum of 1s for K! permutations)
        totalSum = (totalSum + fact[k]) % MOD;

        return totalSum;
    }

    // Helper Class: Binary Indexed Tree (Fenwick Tree)
    static class BIT {
        int[] tree;
        int n;

        public BIT(int n) {
            this.n = n;
            this.tree = new int[n + 1];
        }

        public void update(int index, int val) {
            while (index <= n) {
                tree[index] += val;
                index += index & (-index);
            }
        }

        public int query(int index) {
            int sum = 0;
            while (index > 0) {
                sum += tree[index];
                index -= index & (-index);
            }
            return sum;
        }
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        // Using standard output for safety, though environment usually sets OUTPUT_PATH
        String outputPath = System.getenv("OUTPUT_PATH");
        BufferedWriter bufferedWriter;
        if (outputPath != null) {
            bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        } else {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        }

        int n = Integer.parseInt(bufferedReader.readLine().trim());

        String[] aTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        List<Integer> a = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int aItem = Integer.parseInt(aTemp[i]);
            a.add(aItem);
        }

        long result = Result.solve(a);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
