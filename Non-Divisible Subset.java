import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'nonDivisibleSubset' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER k
     * 2. INTEGER_ARRAY s
     */

    public static int nonDivisibleSubset(int k, List<Integer> s) {
        // Step 1: Create an array to store frequency of each remainder
        int[] remainderCounts = new int[k];
        
        for (int num : s) {
            int remainder = num % k;
            remainderCounts[remainder]++;
        }

        int subsetSize = 0;

        // Step 2: Handle remainder 0
        // We can pick at most one element that is evenly divisible by k
        if (remainderCounts[0] > 0) {
            subsetSize++;
        }

        // Step 3: Handle remainder pairs (1 vs k-1, 2 vs k-2, etc.)
        // We iterate up to k/2
        for (int i = 1; i <= k / 2; i++) {
            // Check if we are at the exact middle (only happens if k is even)
            if (i == k - i) {
                // Similar to remainder 0, we can only take one element from the k/2 bucket
                if (remainderCounts[i] > 0) {
                    subsetSize++;
                }
            } else {
                // Otherwise, pick the bucket with the most elements
                subsetSize += Math.max(remainderCounts[i], remainderCounts[k - i]);
            }
        }

        return subsetSize;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] firstMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        int n = Integer.parseInt(firstMultipleInput[0]);

        int k = Integer.parseInt(firstMultipleInput[1]);

        String[] sTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        List<Integer> s = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int sItem = Integer.parseInt(sTemp[i]);
            s.add(sItem);
        }

        int result = Result.nonDivisibleSubset(k, s);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
