import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'pickingNumbers' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY a as parameter.
     */

    public static int pickingNumbers(List<Integer> a) {
        // The constraints state 0 < a[i] < 100, so values are 1 to 99.
        // We create a frequency array of size 101 to handle indices up to 100 safely.
        int[] frequency = new int[101];

        // Step 1: Populate the frequency array
        for (int num : a) {
            frequency[num]++;
        }

        int maxLength = 0;

        // Step 2: Iterate through the range of possible values (1 to 99).
        // We check the sum of frequencies for adjacent numbers (i and i+1).
        for (int i = 1; i < 100; i++) {
            // The size of the multiset containing only {i, i+1}
            int currentLength = frequency[i] + frequency[i + 1];
            
            if (currentLength > maxLength) {
                maxLength = currentLength;
            }
        }

        return maxLength;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = Integer.parseInt(bufferedReader.readLine().trim());

        String[] aTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        List<Integer> a = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int aItem = Integer.parseInt(aTemp[i]);
            a.add(aItem);
        }

        int result = Result.pickingNumbers(a);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
