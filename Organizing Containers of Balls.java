import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'organizingContainers' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts 2D_INTEGER_ARRAY container as parameter.
     */

    public static String organizingContainers(List<List<Integer>> container) {
        int n = container.size();
        
        // Arrays to store the total capacity of each container (row sums)
        // and the total count of each ball type (column sums).
        // Using long to prevent overflow as values can be up to 10^9.
        long[] containerCapacities = new long[n];
        long[] ballTypeCounts = new long[n];

        // Iterate through the matrix to calculate sums
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int count = container.get(i).get(j);
                
                // Sum of row i (Total balls currently in container i)
                containerCapacities[i] += count;
                
                // Sum of column j (Total balls of type j across all containers)
                ballTypeCounts[j] += count;
            }
        }

        // Sort both arrays to compare the sets of values
        Arrays.sort(containerCapacities);
        Arrays.sort(ballTypeCounts);

        // Check if the set of container capacities matches the set of ball type counts
        if (Arrays.equals(containerCapacities, ballTypeCounts)) {
            return "Possible";
        } else {
            return "Impossible";
        }
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int q = Integer.parseInt(bufferedReader.readLine().trim());

        for (int qItr = 0; qItr < q; qItr++) {
            int n = Integer.parseInt(bufferedReader.readLine().trim());

            List<List<Integer>> container = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                String[] containerRowTempItems = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

                List<Integer> containerRowItems = new ArrayList<>();

                for (int j = 0; j < n; j++) {
                    int containerItem = Integer.parseInt(containerRowTempItems[j]);
                    containerRowItems.add(containerItem);
                }

                container.add(containerRowItems);
            }

            String result = Result.organizingContainers(container);

            bufferedWriter.write(result);
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
