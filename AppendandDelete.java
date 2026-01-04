import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'appendAndDelete' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts following parameters:
     * 1. STRING s
     * 2. STRING t
     * 3. INTEGER k
     */

    public static String appendAndDelete(String s, String t, int k) {
        // Step 1: Find the length of the common prefix
        int commonLength = 0;
        int minLength = Math.min(s.length(), t.length());
        
        for (int i = 0; i < minLength; i++) {
            if (s.charAt(i) == t.charAt(i)) {
                commonLength++;
            } else {
                break;
            }
        }

        // Step 2: Calculate the minimum operations required
        // Deletions needed + Append operations needed
        int minOps = (s.length() - commonLength) + (t.length() - commonLength);

        // Step 3: Check feasibility based on the rules
        
        // Case A: Not enough moves to do the minimal work
        if (k < minOps) {
            return "No";
        }
        
        // Case B: We have enough moves to delete s entirely and rebuild t.
        // This covers the case where we delete on an empty string (Rule 2).
        if (k >= s.length() + t.length()) {
            return "Yes";
        }
        
        // Case C: We have extra moves, and the difference is even.
        // We can waste moves in pairs (append + delete) to consume the difference.
        if ((k - minOps) % 2 == 0) {
            return "Yes";
        }

        // Otherwise
        return "No";
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String s = bufferedReader.readLine();

        String t = bufferedReader.readLine();

        int k = Integer.parseInt(bufferedReader.readLine().trim());

        String result = Result.appendAndDelete(s, t, k);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
A
